package com.stevedenheyer.scriptassistant.editor.viewmodels

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stevedenheyer.scriptassistant.editor.domain.usecases.GenerateWaveform
import com.stevedenheyer.scriptassistant.editor.domain.usecases.GetFileNames
import com.stevedenheyer.scriptassistant.editor.domain.usecases.GetWaveformMapFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WaveformGeneratorViewModel @Inject constructor(
    state: SavedStateHandle,
    private val getFileNames: GetFileNames,
    private val generateWaveform: GenerateWaveform
):ViewModel() {

    private val projectId = state.get<Long>("projectId")!!

    init {
        viewModelScope.launch {
            getFileNames(projectId).collect {
                generateWaveform(it)
            }
        }
    }
}