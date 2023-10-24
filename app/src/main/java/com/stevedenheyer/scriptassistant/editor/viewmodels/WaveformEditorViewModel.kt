package com.stevedenheyer.scriptassistant.editor.viewmodels

import android.util.Log
import android.util.Range
import androidx.lifecycle.*
import com.stevedenheyer.scriptassistant.editor.domain.model.EditorEvent
import com.stevedenheyer.scriptassistant.common.domain.model.audio.SentenceAudio
import com.stevedenheyer.scriptassistant.editor.domain.model.Waveform
import com.stevedenheyer.scriptassistant.editor.domain.usecases.GetAudioDetails
import com.stevedenheyer.scriptassistant.editor.domain.usecases.GetProjectFlow
import com.stevedenheyer.scriptassistant.editor.domain.usecases.GetSettings
import com.stevedenheyer.scriptassistant.editor.domain.usecases.GetWaveformMapFlow
import com.stevedenheyer.scriptassistant.editor.domain.usecases.UpdateAudioDetails
import com.stevedenheyer.scriptassistant.editor.domain.usecases.UpdateProject
import com.stevedenheyer.scriptassistant.common.data.sentances.FindSentences
import com.stevedenheyer.scriptassistant.common.domain.model.audio.StartingSettings
import com.stevedenheyer.scriptassistant.common.domain.model.project.Project
import com.stevedenheyer.scriptassistant.utils.EventHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import kotlin.math.roundToInt

data class AudioFileTabUi(
    val id: Long,
    val name: String,
)

@HiltViewModel
class WaveformEditorViewModel @Inject constructor(
    state: SavedStateHandle,
    private val getProjectFlow: GetProjectFlow,
    private val updateProject: UpdateProject,
    private val getSettings: GetSettings,
    private val getAudioDetails: GetAudioDetails,
    getWaveformMapFlow: GetWaveformMapFlow,
    private val updateAudioDetails: UpdateAudioDetails,
    eventHandler: EventHandler<EditorEvent>,
) : ViewModel() {

    private val projectId = state.get<Long>("projectId")!!

    private val projectFlow = getProjectFlow(projectId).stateIn(viewModelScope, SharingStarted.Eagerly, Project(-1, "", null, null))

    private val currentAudioId = projectFlow.map { it.selectedAudioId ?: -1 }.stateIn(viewModelScope, SharingStarted.Eagerly, -1)

    private val audioDetailsFlow = combine(currentAudioId, getAudioDetails(projectId)) { id, details ->
        details[id]
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val startingSettingsMap = HashMap<Long, StartingSettings>()

    //val sentencesMap = LinkedHashMap<Long, List<SentenceAudio>>()

    val audioFileTabUiState = getAudioDetails(projectId).map { details ->
        val tabs = ArrayList<AudioFileTabUi>()
        details.forEach {
            tabs.add(AudioFileTabUi(it.key, it.value.name))
        }
        tabs.toList()
    }.stateIn(viewModelScope, started = SharingStarted.Lazily, initialValue = emptyList())

    val currentAudioIndex = combineTransform(currentAudioId, audioFileTabUiState) { id, tab ->
            val index = tab.indexOfFirst { it ->
                it.id == id
            }
            if (index >= 0) emit(index)
        }

    val waveform = combine(currentAudioId, getWaveformMapFlow()) { id, map ->
        Log.d("WFMVM", "New waveform triggered")
        val waveform =
            map[id] ?: Waveform(id = 0, data = emptyArray<Byte>().toByteArray(), 0, isLoading = true)
        waveform
    }.stateIn(viewModelScope, SharingStarted.Lazily, Waveform(id = 0, data = emptyArray<Byte>().toByteArray(), 0, isLoading = true))

    val sentencesFromDB = combine(currentAudioId, audioDetailsFlow) { id, details ->
        details?.sentences ?: emptyArray()
    }

    private val _threshold = MutableStateFlow(0F)
    val threshold = _threshold.asStateFlow()

    private val _pause = MutableStateFlow(0F)
    val pause = _pause.asStateFlow()

    private val _offsetX = MutableStateFlow(0F)
    val offsetX = _offsetX.asStateFlow()

    private val _scaleX = MutableStateFlow(0F)
    val scaleX = _scaleX.asStateFlow()

    private val userIsChangingSettings = MutableStateFlow(false)

   // private val localEventFlow: MutableStateFlow<EditorEvent?> = MutableStateFlow(null)

    private val eventFlow = eventHandler.getEventFlow()

    private val generatedRanges =
        combineTransform(threshold, pause, userIsChangingSettings) { threshold, pause, userIsChanging ->
            if (!waveform.value.isLoading && userIsChangingSettings.value) {
                val ranges = ArrayList<Range<Int>>()
                val findSentences = FindSentences(currentCoroutineContext().job)
                ranges.addAll(
                    findSentences(
                        threshold.roundToInt().toByte(),
                        pause.roundToInt(),
                        waveform.value.data
                    )
                )
                emit(ranges)
            }
        }.flowOn(Dispatchers.IO).stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val sentenceList = combine(
        sentencesFromDB,
        generatedRanges
    ) { sentences, ranges ->

        val newSentenceList = sentences.filter {
            it.isLocked
        }.toMutableList()
        val rangesWithoutLocked = ranges.filter { range ->
            var disjointFromAllLocked = true
            newSentenceList.forEach { sentence ->
                disjointFromAllLocked = !(range.contains(sentence.waveformRange.upper) || range.contains(sentence.waveformRange.lower) || sentence.waveformRange.contains(range))
            }
            disjointFromAllLocked
        }
            rangesWithoutLocked.forEach { range ->
                newSentenceList.add(SentenceAudio(waveformRange = range, scriptLineId = 0, scriptTake = 0))
            }
            //sentencesMap[id] = newSentenceList
            // Log.d("WUVM", "Found ${newSentenceList.size}")
        newSentenceList
    }.flowOn(Dispatchers.IO).stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val sentenceInsertFlow = MutableStateFlow<Pair<Int, SentenceAudio>?>(null)

    val sentences = combine (sentenceList, sentenceInsertFlow) { sentences, insert ->
        val newSentenceList = sentences.toMutableList()
        if (insert != null) {
            newSentenceList[insert.first] = insert.second.copy(isLocked = true)
        }
        newSentenceList
    }.flowOn(Dispatchers.IO).stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    init {
        viewModelScope.launch {
            startingSettingsMap.putAll(getSettings(projectId).first())
            refreshSliders()
        }

        viewModelScope.launch {
            currentAudioId.collect {id ->
                eventFlow.update { EditorEvent.RequestRecyclerUpdate (EditorEvent.Focus.WaveformFocus(id)) }
                refreshSliders()
            }
        }

/*        viewModelScope.launch {
            combine(localEventFlow, getAudioDetails(projectId)) { event, oldDetails,  ->
                Log.d("WUVM", "Changing ${event.toString()}")
                val id = currentAudioId.value
                if (event is EditorEvent.RequestSentenceUpdate && !event.completed && oldDetails[id] != null) {  //Infinite loop

                    val sentences = sentencesMap[id]?.toTypedArray() ?: oldDetails[id]!!.sentences
                    Log.d("WUVM", "Updating details... ${sentences.size}")
                    val details = oldDetails[id]!!.copy(
                        settings = Settings(
                            threshold = threshold.value,
                            pause = pause.value,
                        ), sentences = sentences
                    )
                    updateAudioDetails(projectId, details)
                    localEventFlow.value = EditorEvent.RequestSentenceUpdate(true)

                    eventFlow.value = EditorEvent.RequestRecyclerUpdate(
                        SentencesCollection(
                            id = id,
                            data = sentences
                        )
                    )
                }
            }.collect()
        }*/

        viewModelScope.launch {
            getWaveformMapFlow().collect {wfmMap ->
                if (wfmMap.count() == 1) {
                    setCurrentAudioId(wfmMap.values.first().id)
                }
            }
        }



        /*        getSettings(projectId).collect {
                    if (!settingsMap.equals(it)) {
                        settingsMap.putAll(it)
                        refreshSliders()
                    }
                }*/
    }

    fun setCurrentAudioId(key: Long) {
        if (key != currentAudioId.value) {
            viewModelScope.launch {
                val project = projectFlow.value.copy(selectedAudioId = key)
                updateProject(project)
            }
        }
    }

    fun setThreshold(value: Float) {
        userIsChangingSettings.value = true
        _threshold.value = value
        val id = currentAudioId.value
        if ((value >= 0) && startingSettingsMap.containsKey(id)) {
            startingSettingsMap[id] = startingSettingsMap[id]!!.copy(thresholdSlider = value)
            //refreshSliders()
        }
    }

    fun setPause(value: Float) {
        userIsChangingSettings.value = true
        _pause.value = value
        val id = currentAudioId.value
        if ((value >= 0) && startingSettingsMap.containsKey(id)) {
            startingSettingsMap[id] = startingSettingsMap[id]!!.copy(pauseSlider = value)
            //refreshSliders()
        }
    }

    fun setUserIsDoneChangingSettings() {
        userIsChangingSettings.value = false
        eventFlow.update { EditorEvent.RequestRecyclerUpdate (EditorEvent.Focus.WaveformFocus(currentAudioId.value)) }
        sentenceInsertFlow.value = null
        viewModelScope.launch {
            val details = audioDetailsFlow.value?.copy(
                startingSettings = StartingSettings(
                    thresholdSlider = threshold.value,
                    pauseSlider = pause.value,
                    viewOffsetX = offsetX.value,
                    viewScaleX = scaleX.value
                ), sentences = sentences.value.toTypedArray()
            )
            if (details != null) {
                updateAudioDetails(projectId, details)
            }
        }
    }

    fun setMark(index: Int, sentence: SentenceAudio) {
        sentenceInsertFlow.update {
            Pair(index, sentence)
        }
    }

    private fun refreshSliders() {
        val id = currentAudioId.value
        _threshold.value = startingSettingsMap[id]?.thresholdSlider ?: 0F
        _pause.value = startingSettingsMap[id]?.pauseSlider ?: 0F
    }

}