package dev.yveskalume.newsappp.core

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

abstract class BaseViewModel<S : State, E : Event>(
    private val stateManager: StateManager<S, E>,
) : ViewModel() {

    val uiState: StateFlow<S> by lazy {
        createViewStateFlow()
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Lazily,
                initialValue = stateManager.stateFlow.value,
            )
    }

    fun onEvent(event: E) {
        stateManager.onEvent(event)
    }

    private fun createViewStateFlow(): Flow<S> = stateManager.stateFlow
        .onStart {
            stateManager.startProcessingEvents()
            onStateStarted()
        }

    protected open fun onStateStarted() {}
}