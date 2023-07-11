package com.stevedenheyer.scriptassistant.audioeditor.viewmodels

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stevedenheyer.scriptassistant.common.domain.model.audio.AudioDetails
import com.stevedenheyer.scriptassistant.audioeditor.domain.model.EditorEvent
import com.stevedenheyer.scriptassistant.audioeditor.domain.usecases.*
import com.stevedenheyer.scriptassistant.scripteditor.GetScript
import com.stevedenheyer.scriptassistant.utils.EventHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WaveformRecyclerViewModel @Inject constructor(
    private val state: SavedStateHandle,
    private val eventHandler: EventHandler<EditorEvent>,
 //   private val getAudioDetails: GetAudioDetails,
    private val updateAudioDetails: UpdateAudioDetails,
    private val getScript: GetScript,
    private val getWaveformMapFlow: GetWaveformMapFlow,
  //  private val getCurrentIdFlow: GetCurrentIdFlow,
    ) : ViewModel() {

    private val projectId = state.get<Long>("projectId")!!

    private val scriptId = state.get<Long>("scriptId")!!

    private val audioDetailsMap:Map<Long,AudioDetails> = emptyMap()

    private val waveform = getWaveformMapFlow(projectId).stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private val script = getScript(scriptId).stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val recyclerItems = eventHandler.getEventFlow().map {event ->
        val recyclerItemViewList = ArrayList<WaveformRecyclerItemView>()
        if (event is EditorEvent.RequestRecyclerUpdate) {
            val id = event.sentences.id
            event.sentences.data.forEach {sentence ->
                val text = script.value.find { it.id == sentence.scriptLineId }?.text ?: ""
                recyclerItemViewList.add(
                    WaveformRecyclerItemView(
                        audioOwnerId = id,
                        text = text,
                        range = sentence.waveformRange,
                        waveform = waveform.value.find { it.id == id }?.data?.copyOfRange(sentence.waveformRange.lower, sentence.waveformRange.upper) ?: emptyArray<Byte>().toByteArray()
                    )
                )
            }
        }
        recyclerItemViewList.toTypedArray()
    }
/*
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
    }*/

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
        val index = detailsToChange!!.sentences.indexOfFirst { it.waveformRange == item.range }
        detailsToChange.sentences[index] = detailsToChange.sentences[index].copy(scriptLineId = null)  //TODO:  and this
        viewModelScope.launch { updateAudioDetails(projectId, detailsToChange) }
    }

}