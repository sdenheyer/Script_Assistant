package com.stevedenheyer.scriptassistant.editor.viewmodels

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stevedenheyer.scriptassistant.common.domain.model.audio.AudioDetails
import com.stevedenheyer.scriptassistant.editor.domain.model.EditorEvent
import com.stevedenheyer.scriptassistant.editor.domain.usecases.*
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
    private val getAudioDetails: GetAudioDetails,
    private val updateAudioDetails: UpdateAudioDetails,
    private val getScript: GetScript,
    private val getWaveformMapFlow: GetWaveformMapFlow,
  //  private val getCurrentIdFlow: GetCurrentIdFlow,
    ) : ViewModel() {

    private val projectId = state.get<Long>("projectId")!!

    private val scriptId = state.get<Long>("scriptId")!!

    private val audioDetailsMap = getAudioDetails(projectId).stateIn(viewModelScope, SharingStarted.Eagerly, emptyMap())

    private val waveform = getWaveformMapFlow().stateIn(viewModelScope, SharingStarted.Eagerly, emptyMap())

    private val script = getScript(scriptId).stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val recyclerItems = combine(eventHandler.getEventFlow(), audioDetailsMap) {event, details ->
        val recyclerItemViewList = ArrayList<WaveformRecyclerItemView>()
        if (event is EditorEvent.RequestRecyclerUpdate) {
            when (event.focus) {
                is EditorEvent.Focus.WaveformFocus -> {
                    Log.d("RECVM", "Updating view... ${scriptId} ${script.value.size}")
                    val id = event.focus.audioId
                    details[id]?.sentences?.forEach { sentence ->
                        val text = script.value.find { it.id == sentence.scriptLineId }?.text ?: ""
                        recyclerItemViewList.add(
                            WaveformRecyclerItemView(
                                audioOwnerId = id,
                                text = text,
                                range = sentence.waveformRange,
                                waveform = waveform.value[id]?.data?.copyOfRange(
                                    sentence.waveformRange.lower,
                                    sentence.waveformRange.upper
                                ) ?: emptyArray<Byte>().toByteArray()
                            )
                        )
                    }
                }
            }
        }
        recyclerItemViewList.toTypedArray()
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyArray())
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

    fun onScriptDropped(item: WaveformRecyclerItemView, line: ScriptLineItemView) {
        Log.d("RECVM", "script dropped: ${item.audioOwnerId} ${line.id} ${line.text}")
        val sentencesToChange = audioDetailsMap.value[item.audioOwnerId]!!.sentences
        val index = sentencesToChange.indexOfFirst { it.waveformRange == item.range }
        Log.d("RECVM", "index found: ${index}")
        sentencesToChange[index] = sentencesToChange[index].copy(scriptLineId = line.id)
        val detailsToChange = audioDetailsMap.value[item.audioOwnerId]!!.copy(sentences = sentencesToChange)
       /*
        val detailsToChange = audioDetailsMap.value[item.audioOwnerId]
        val index = detailsToChange!!.sentences.indexOfFirst { it.waveformRange == item.range }
        val sentencesToChange = detailsToChange!!.sentences
        sentencesToChange[index] = sentencesToChange[index].copy(scriptLineId = line.id)
        detailsToChange = detailsToChange.copy(sentences = sentencesToChange)*/
        viewModelScope.launch { updateAudioDetails(projectId, detailsToChange) }
    }

}