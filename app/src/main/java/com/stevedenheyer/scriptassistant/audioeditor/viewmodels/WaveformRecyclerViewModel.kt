package com.stevedenheyer.scriptassistant.audioeditor.viewmodels

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.stevedenheyer.scriptassistant.common.domain.model.audio.AudioDetails
import com.stevedenheyer.scriptassistant.audioeditor.domain.model.EditorEvent
import com.stevedenheyer.scriptassistant.audioeditor.domain.usecases.*
import com.stevedenheyer.scriptassistant.utils.EventHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WaveformRecyclerViewModel @Inject constructor(
    private val state: SavedStateHandle,
    private val eventHandler: EventHandler<EditorEvent>,
    private val getAudioDetails: GetAudioDetails,
    private val updateAudioDetails: UpdateAudioDetails,
    private val getWaveformFlow: GetWaveformFlow,
  //  private val getCurrentIdFlow: GetCurrentIdFlow,
    ) : ViewModel() {

    private val projectId = state.get<Long>("projectId")!!

    private var audioDetailsMap:Map<Long,AudioDetails> = emptyMap()

    private val recyclerItems:Flow<List<WaveformRecyclerItemView>> = combineTransform(getAudioDetails(projectId), getWaveformFlow()) { details, waveform ->
        audioDetailsMap = details
        val recyclerItemViewList = ArrayList<WaveformRecyclerItemView>()
        details[0]?.sentences?.forEach {
            recyclerItemViewList.add(
                WaveformRecyclerItemView(
                    audioOwnerId = 0,
                    text = "",              //TODO:  Get this
                    range = it.range,
                    waveform = waveform[0]!!.data.copyOfRange(it.range.lower, it.range.upper)
                )
            )
        }
        emit(recyclerItemViewList)
    }

   /* init {
        viewModelScope.launch {
                combine(eventHandler.getEventFlow(), getSentencesDetails(), getAudioDetails(projectId)) { event, sentences, audioDetails ->
                    if (event is EditorEvent.RequestSentenceUpdate && !event.completed) {
                        val id = sentences.id
                        if (audioDetails[id]?.settings != sentences.settings || audioDetails[id]?.sentences contentEquals sentences.sentences) {
                            val details = audioDetails[id]?.copy(
                                settings = sentences.settings,
                                sentences = sentences.sentences
                            )
                            if (details != null) {
                                Log.d("RECVM", "Updating database...")
                                updateAudioDetails(projectId, details)
                            }
                            eventHandler.onEvent(EditorEvent.RequestSentenceUpdate(true))
                        }
                    }
            }.collect()
        }

    }*/

    fun getRecyclerItems() = recyclerItems

    fun onScriptDropped(item: WaveformRecyclerItemView) {
        Log.d("RECVM", "script dropped: {$item.text}")
        val detailsToChange = audioDetailsMap[item.audioOwnerId]
        val index = detailsToChange!!.sentences.indexOfFirst { it.range == item.range }
        detailsToChange.sentences[index] = detailsToChange.sentences[index].copy(lineId = null)  //TODO:  and this
        viewModelScope.launch { updateAudioDetails(projectId, detailsToChange) }
    }

}