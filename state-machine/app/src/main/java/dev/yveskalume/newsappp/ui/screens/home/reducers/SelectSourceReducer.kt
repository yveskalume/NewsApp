package dev.yveskalume.newsappp.ui.screens.home.reducers

import dev.yveskalume.newsappp.core.Reducer
import dev.yveskalume.newsappp.ui.screens.home.HomeEvent
import dev.yveskalume.newsappp.ui.screens.home.HomeUiState
import dev.yveskalume.newsappp.ui.screens.home.SourcesUiState

class SelectSourceReducer : Reducer<HomeUiState, HomeEvent.SelectSource> {
    override suspend fun reduce(
        state: HomeUiState,
        event: HomeEvent.SelectSource
    ): HomeUiState {
        return if (
            state.sourcesUiState is SourcesUiState.Success &&
            state.selectedSource?.id != event.source?.id
        ) {
            state.copy(selectedSource = event.source)
        } else {
            state.copy(selectedSource = null)
        }
    }
}