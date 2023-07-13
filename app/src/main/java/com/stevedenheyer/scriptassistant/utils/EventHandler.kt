package com.stevedenheyer.scriptassistant.utils

import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@ActivityRetainedScoped
class EventHandler<T> @Inject constructor() {
    private val eventFlow = MutableStateFlow<T?>(null)

    fun onEvent(event: T) {
        eventFlow.tryEmit(event)
    }

    fun getEventFlow() = eventFlow

 /*   fun resetEvent() {
        eventFlow.tryEmit(null)
    }*/
}