package dev.yveskalume.newsappp.ui.screens.home.reducers

import dev.yveskalume.newsappp.core.Reducer
import dev.yveskalume.newsappp.ui.screens.home.ArticleUiState
import dev.yveskalume.newsappp.ui.screens.home.HomeEvent
import dev.yveskalume.newsappp.ui.screens.home.HomeUiState

class SetPagingLoadingReducer : Reducer<HomeUiState, HomeEvent.SetPagingLoading> {
    override suspend fun reduce(
        state: HomeUiState,
        event: HomeEvent.SetPagingLoading
    ): HomeUiState {
        if (state.articleUiState !is ArticleUiState.Success) {
            return state
        }
        return state.copy(
            articleUiState = state.articleUiState.copy(
                pagingState = ArticleUiState.PagingState.Loading
            )
        )
    }
}