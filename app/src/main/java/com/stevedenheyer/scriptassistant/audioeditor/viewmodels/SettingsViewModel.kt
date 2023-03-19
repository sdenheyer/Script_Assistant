package com.stevedenheyer.scriptassistant.audioeditor.viewmodels

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stevedenheyer.scriptassistant.common.domain.model.audio.Settings
import com.stevedenheyer.scriptassistant.common.domain.repositories.SentencesRepository
import com.stevedenheyer.scriptassistant.audioeditor.domain.usecases.GetSettings
import com.stevedenheyer.scriptassistant.audioeditor.domain.model.EditorEvent
import com.stevedenheyer.scriptassistant.utils.EventHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class SettingsViewModel @Inject constructor (
    private val state: SavedStateHandle,
    private val getSettings: GetSettings,
    private val sentencesRepository: SentencesRepository,
    private val eventHandler: EventHandler<EditorEvent>,
    ) : ViewModel() {

    private val projectId = state.get<Long>("projectId")!!

    private val currentAudioId = MutableStateFlow(0L)

    private val settingsMap = MutableStateFlow(HashMap<Long, Settings>())

    val threshold = combine(currentAudioId, settingsMap) { id, settings ->
        (settings.get(id)?.threshold ?: 0).toFloat()
    }

    val pause = combine(currentAudioId, settingsMap) { id, settings ->
        (settings.get(id)?.pause ?: 0).toFloat()
    }

    /*val threshold = object:ObservableInt() {
        override fun get(): Int {
            return settingsMap[currentAudioId.value]?.threshold ?: 0
        }

        override fun set(value: Int) {
            if ((value > 0) && settingsMap.containsKey(currentAudioId.value)) {
                settingsMap[currentAudioId.value] =
                    settingsMap[currentAudioId.value]!!.copy(threshold = value)
                sentencesRepository.setThreshold(value)
            }
        }
    }
*/
  /*  val pause = object:ObservableInt() {
        override fun get(): Int {
            return settingsMap[currentAudioId.value]?.pause ?: 0
        }

        override fun set(value: Int) {
            if ((value > 0) && settingsMap.containsKey(currentAudioId.value)) {
                settingsMap[currentAudioId.value] =
                    settingsMap[currentAudioId.value]!!.copy(pause = value)
                sentencesRepository.setPause(value)
            }
        }
    }
*/

  //  private var currentAudioId: Long = -1

    init {
        viewModelScope.launch {
            getSettings(projectId).collect {
                if (!settingsMap.value.equals(it)) {
                    settingsMap.value.putAll(it)
                }
            }
        }
    }

    fun setCurrentAudioId(key: Long) {
        if (key != currentAudioId.value) {
            currentAudioId.value = key
        }
    }

    fun setThreshold(value: Float) {
        val threshold = value.roundToInt()
        Log.d("SVM", "Received update to id ${currentAudioId.value}")
        if ((value > 0) && settingsMap.value.containsKey(currentAudioId.value)) {
            settingsMap.update { map ->
                Log.d("SVM", "updating value $value")
                map[currentAudioId.value] =
                    map[currentAudioId.value]!!.copy(threshold = threshold)
                map
            }
            sentencesRepository.setThreshold(value)
        }
    }

    fun setPause(value: Float) {
        val pause = value.roundToInt()
        if ((value > 0) && settingsMap.value.containsKey(currentAudioId.value)) {
            settingsMap.value[currentAudioId.value] =
                settingsMap.value[currentAudioId.value]!!.copy(pause = pause)
            sentencesRepository.setPause(value)
        }
    }

    fun onStopTrackingTouch() {
        eventHandler.onEvent(EditorEvent.RequestSentenceUpdate(false))
    }
}