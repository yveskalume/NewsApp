package dev.yveskalume.newsappp.ui.screens.home.reducers

import dev.yveskalume.newsappp.core.Reducer
import dev.yveskalume.newsappp.ui.screens.home.HomeEvent
import dev.yveskalume.newsappp.ui.screens.home.HomeUiState
import dev.yveskalume.newsappp.ui.screens.home.RefreshUiState

class SetRefreshLoadingReducer : Reducer<HomeUiState, HomeEvent.SetRefreshLoading> {
    override suspend fun reduce(
        state: HomeUiState,
        event: HomeEvent.SetRefreshLoading
    ): HomeUiState {
        return state.copy(
            refreshUiState = if (event.isLoading) RefreshUiState.Refreshing else RefreshUiState.Idle
        )
    }
}