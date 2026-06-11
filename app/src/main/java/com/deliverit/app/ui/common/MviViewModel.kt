package com.deliverit.app.ui.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch

abstract class MviViewModel<State, Intent, Events>(initialState: State) : ViewModel() {

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<State> = _state.asStateFlow()

    private val _event = Channel<Events>(Channel.BUFFERED)
    val event: SharedFlow<Events> = _event.receiveAsFlow()
        .shareIn(viewModelScope, SharingStarted.Eagerly)

    val currentState: State
        get() = _state.value

    abstract fun onIntent(intent: Intent)

    protected fun setState(reduce: State.() -> State) {
        _state.value = _state.value.reduce()
    }

    protected fun sendEvent(event: Events) {
        viewModelScope.launch {
            _event.send(event)
        }
    }
}
