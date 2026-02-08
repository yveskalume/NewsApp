package dev.yveskalume.newsappp.ui.screens.home

import androidx.compose.runtime.Immutable
import dev.yveskalume.newsappp.core.BaseViewModel
import dev.yveskalume.newsappp.core.StateManager
import org.koin.core.scope.Scope

@Immutable
class HomeViewModel(
    stateManager: StateManager<HomeUiState, HomeEvent>
) : BaseViewModel<HomeUiState, HomeEvent>(stateManager) {
    override fun onStateStarted() {
        onEvent(HomeEvent.LoadSources)
        onEvent(HomeEvent.LoadArticles())
    }
}
