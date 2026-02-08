package dev.yveskalume.newsappp.core

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.koin.core.scope.Scope
import kotlin.reflect.KClass

/**
 * This will act like Base ViewModel
 *
 * @param S State type
 * @param E The Event type
 *
 * Manages state and processes events through reducers
 * Uses Koin scope to resolve reducers dynamically
 */

class StateManager<S : State, E : Event>(
    private val scope: Scope,
    initialState: S,
) : ViewModel() {

    val stateFlow = MutableStateFlow(initialState)
    private val eventFlow = MutableSharedFlow<E>()
    private var started = false


    fun onEvent(event: E) {
        viewModelScope.launch {
            eventFlow.emit(event)
        }
    }

    fun startProcessingEvents() {
        if (started) return
        started = true
        viewModelScope.launch {
            eventFlow.collect { event ->
                val currentState = stateFlow.value
                val reducer = findReducer(currentState::class, event::class)
                Log.d("StateManager", "Processing event: $event, State is: ${currentState::class.simpleName}.")
                reducer?.let {
                    val newState = it.reduce(currentState, event)
                    stateFlow.value = newState
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun findReducer(
        stateClass: KClass<out S>,
        eventClass: KClass<out E>
    ): Reducer<S, E>? {
        return scope.getReducer(stateClass, eventClass) as? Reducer<S, E>
    }
}