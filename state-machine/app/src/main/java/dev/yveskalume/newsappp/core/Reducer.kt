package dev.yveskalume.newsappp.core

/**
 * A Reducer is a component that receives an [Event] and a [State] from [StateManager],
 * processes it and returns a new [State].
 *
 * For operations with side effects (like API calls), the reducer can:
 * 1. Return the current/updated state immediately
 * 2. Launch coroutines that eventually publish new events with results
 */
fun interface Reducer<S : State, in E : Event> {
  suspend fun reduce(state: S, event: E): S
}