package dev.yveskalume.newsappp.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalForInheritanceCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingCommand
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.stateIn

/**
 * A [StateFlow] that can be restarted.
 *
 * This interface extends the standard [StateFlow] and adds a [restart] method.
 * Calling [restart] will cause the upstream [Flow] that created this state flow
 * to be re-collected, effectively re-running its logic and updating the state.
 *
 * This is particularly useful for scenarios where we need to refresh data from a source
 * on demand, such as a "pull-to-refresh" action in a UI.
 *
 * @param T The type of the state.
 */
@OptIn(ExperimentalForInheritanceCoroutinesApi::class)
interface RestartableStateFlow<out T> : StateFlow<T> {
    fun restart()
}

interface SharingRestartable : SharingStarted {
    fun restart()
}

private data class SharingRestartableImpl(
    private val sharingStarted: SharingStarted,
) : SharingRestartable {

    private val restartFlow = MutableSharedFlow<SharingCommand>(extraBufferCapacity = 2)

    override fun command(subscriptionCount: StateFlow<Int>): Flow<SharingCommand> {
        return merge(restartFlow, sharingStarted.command(subscriptionCount))
    }

    override fun restart() {
        restartFlow.tryEmit(SharingCommand.STOP_AND_RESET_REPLAY_CACHE)
        restartFlow.tryEmit(SharingCommand.START)
    }
}

/**
 * Converts a [Flow] to a [RestartableStateFlow].
 *
 * @param scope The [CoroutineScope] in which the sharing is started.
 * @param started The strategy that defines when the sharing is started and stopped. For example,
 *   [SharingStarted.WhileSubscribed] or [SharingStarted.Lazily].
 * @param initialValue The initial value of the state flow. This is also the value that will be
 *   used when the flow is reset via [RestartableStateFlow.restart].
 * @return A [RestartableStateFlow] that reflects the latest value of the upstream flow and can be
 *   manually restarted.
 */
@OptIn(ExperimentalForInheritanceCoroutinesApi::class)
fun <T> Flow<T>.stateIn(
    scope: CoroutineScope,
    started: SharingStarted,
    initialValue: T
): RestartableStateFlow<T> {
    val sharingRestartable = SharingRestartableImpl(started)
    val stateFlow = this@stateIn.stateIn(scope, sharingRestartable, initialValue)
    return object : RestartableStateFlow<T>, StateFlow<T> by stateFlow {
        override fun restart() = sharingRestartable.restart()
    }
}