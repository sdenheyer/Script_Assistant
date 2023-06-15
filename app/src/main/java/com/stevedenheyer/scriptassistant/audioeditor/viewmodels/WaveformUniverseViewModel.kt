package com.stevedenheyer.scriptassistant.audioeditor.viewmodels

 import android.util.Log
 import android.util.Range
 import androidx.lifecycle.*
import com.stevedenheyer.scriptassistant.audioeditor.domain.model.EditorEvent
import com.stevedenheyer.scriptassistant.audioeditor.domain.model.Sentence
 import com.stevedenheyer.scriptassistant.audioeditor.domain.model.SentencesCollection
import com.stevedenheyer.scriptassistant.audioeditor.domain.model.Waveform
import com.stevedenheyer.scriptassistant.audioeditor.domain.usecases.GetAudioDetails
import com.stevedenheyer.scriptassistant.audioeditor.domain.usecases.GetSettings
import com.stevedenheyer.scriptassistant.audioeditor.domain.usecases.GetWaveformMapFlow
 import com.stevedenheyer.scriptassistant.audioeditor.domain.usecases.UpdateAudioDetails
 import com.stevedenheyer.scriptassistant.common.data.sentances.FindSentences
import com.stevedenheyer.scriptassistant.common.domain.model.audio.Settings
 import com.stevedenheyer.scriptassistant.utils.EventHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject
 import kotlin.coroutines.coroutineContext
 import kotlin.math.roundToInt

data class AudioFileTabUi(
    val id: Long,
    val name: String,
)

@HiltViewModel
class WaveformUniverseViewModel @Inject constructor(
    private val state: SavedStateHandle,
    private val getSettings: GetSettings,
    private val getAudioDetails: GetAudioDetails,
    private val getWaveformMapFlow: GetWaveformMapFlow,
    private val updateAudioDetails: UpdateAudioDetails,
   // private val eventHandler: EventHandler<EditorEvent>,
) : ViewModel() {

    private val projectId = state.get<Long>("projectId")!!

    private val settingsMap = HashMap<Long, Settings>()

    val sentencesMap = LinkedHashMap<Long, List<Sentence>>()

    val audioFileTabUiState = getAudioDetails(projectId).map { details ->
        val tabs = ArrayList<AudioFileTabUi>()
        details.forEach {
            tabs.add(AudioFileTabUi(it.key, it.value.name))
        }
        tabs.toList()
    }.stateIn(viewModelScope, started = SharingStarted.Lazily, initialValue = emptyList())

    private val currentAudioId = MutableStateFlow(0L)

    val currentAudioIndex = currentAudioId.map {id ->
        var index = 0
        audioFileTabUiState.value.forEachIndexed { i, tab ->
            if (tab.id == id) {
                index = i
            }
        }
        index }

    val waveform = combine(currentAudioId, getWaveformMapFlow(projectId)) { id, map ->
        val waveform = map[id] ?: Waveform(id = 0, data = emptyArray<Byte>().toByteArray(), isLoading = true)
        waveform
    }

    private val _threshold = MutableStateFlow(0F)
    val threshold = _threshold.asStateFlow()

    private val _pause = MutableStateFlow(0F)
    val pause = _pause.asStateFlow()

    private val userIsChangingSettings = MutableStateFlow(true) //TEMP!!!

    private val eventFlow:MutableStateFlow<EditorEvent?> = MutableStateFlow(null)

    private val generatedRanges = combine(threshold, pause, waveform) { threshold, pause, waveform ->
        val ranges = ArrayList<Range<Int>>()
        if (!waveform.isLoading) {
            val findSentences = FindSentences(coroutineContext.job)
            ranges.addAll(findSentences(threshold.roundToInt().toByte(), pause.roundToInt(), waveform.data))
        }
        ranges
        }.flowOn(Dispatchers.IO).distinctUntilChanged()

    val sentences = combine(currentAudioId, userIsChangingSettings, generatedRanges) { id, userIsChanging, ranges ->
        if (userIsChanging) {
            val newSentenceList = ArrayList<Sentence>()
            ranges.forEach {range ->
                newSentenceList.add(Sentence(range = range, lineId = 0, take = 0))
            }
            sentencesMap[id] = newSentenceList
           // Log.d("WUVM", "Found ${newSentenceList.size}")
        }
        sentencesMap[id]
    }.flowOn(Dispatchers.IO).distinctUntilChanged()
    init {
        viewModelScope.launch {
            settingsMap.putAll(getSettings(projectId).first())
            refreshSliders()
        }

        viewModelScope.launch {
            combine(eventFlow, getAudioDetails(projectId)) { event, oldDetails ->
                Log.d("WUVM", "Changing ${event.toString()}")
                val id = currentAudioId.value
                if (event is EditorEvent.RequestSentenceUpdate && !event.completed) {  //Infinite loop
                    Log.d("WUVM", "Updating details...")
                    val details = oldDetails[id]!!.copy(settings = Settings(threshold = threshold.value, pause = pause.value))
                    updateAudioDetails(projectId, details)
                    eventFlow.value = EditorEvent.RequestSentenceUpdate(true)
                }
            }.collect()
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
            currentAudioId.value = key
            refreshSliders()
        }
    }

    fun setThreshold(value: Float) {
        userIsChangingSettings.value = true
        _threshold.value = value
        val id = currentAudioId.value
        if ((value >= 0) && settingsMap.containsKey(id)) {
            settingsMap[id] = settingsMap[id]!!.copy(threshold = value)
            //refreshSliders()
        }
    }

    fun setPause(value: Float) {
        userIsChangingSettings.value = true
        _pause.value = value
        val id = currentAudioId.value
        if ((value >= 0) && settingsMap.containsKey(id)) {
            settingsMap[id] = settingsMap[id]!!.copy(pause = value)
            //refreshSliders()
        }
    }

    fun setUserIsDoneChangingSettings() {
        userIsChangingSettings.value = false
        eventFlow.value = EditorEvent.RequestSentenceUpdate(false)
    }

    private fun refreshSliders() {
        val id = currentAudioId.value
        _threshold.value = settingsMap[id]?.threshold ?: 0F
        _pause.value = settingsMap[id]?.pause ?: 0F
    }

}