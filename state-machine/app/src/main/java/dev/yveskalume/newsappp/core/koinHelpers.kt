package dev.yveskalume.newsappp.core

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.getKoin
import org.koin.core.definition.Definition
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.dsl.ScopeDSL
import org.koin.mp.KoinPlatformTools
import org.koin.mp.generateId
import kotlin.reflect.KClass


internal inline fun <reified S : State, reified E : Event> ScopeDSL.scopedReducer(
    noinline definition: Definition<Reducer<S, E>>,
) {
    scoped(
        qualifier = buildQualifierString(S::class, E::class),
        definition = definition,
    )
}

internal fun <S : State, E : Event> Scope.getReducer(
    stateClass: KClass<S>,
    eventClass: KClass<out E>,
): Reducer<S, E>? {
    return getOrNull(
        Reducer::class,
        buildQualifierString(stateClass, eventClass),
    ) as? Reducer<S, E>
}

internal inline fun <reified S : State, reified E : Event> Scope.getStateManager(): StateManager<S, E>? {
    return get<StateManager<S, E>>()
}


internal fun <S : State, E : Event> buildQualifierString(
    stateClass: KClass<S>,
    eventClass: KClass<E>,
) = named("reducer_S_${stateClass.qualifiedName}_E_${eventClass.qualifiedName}")

const val LISTENER_KEY = "NewsApp"

@Composable
inline fun <reified VM : ViewModel> getScopedViewModel(container: String): VM {
    val scopeId = remember { KoinPlatformTools.generateId() }
    val scope = getKoin().getOrCreateScope(
        scopeId = scopeId,
        qualifier = named(container)
    )
    val viewModel = koinViewModel<VM>(scope = scope)
    scope.declare(viewModel.viewModelScope)

    val listener = viewModel.getCloseable<AutoCloseable>(LISTENER_KEY)
    if (listener == null) {
        viewModel.addCloseable(LISTENER_KEY) {
            scope.close()
        }
    }

    return viewModel
}


