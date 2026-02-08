package dev.yveskalume.newsappp.ui.screens.home.reducers

import dev.yveskalume.newsappp.core.Reducer
import dev.yveskalume.newsappp.ui.screens.home.ArticleUiState
import dev.yveskalume.newsappp.ui.screens.home.ArticleUiState.PagingState
import dev.yveskalume.newsappp.ui.screens.home.HomeEvent
import dev.yveskalume.newsappp.ui.screens.home.HomeUiState

class LoadMoreReducer(
    private val onPublishEvent: (HomeEvent) -> Unit,
) : Reducer<HomeUiState, HomeEvent.LoadMore> {
    override suspend fun reduce(
        state: HomeUiState,
        event: HomeEvent.LoadMore
    ): HomeUiState {
        if (
            state.articleUiState !is ArticleUiState.Success ||
            state.articleUiState.pagingState !is PagingState.Idle
        ) {
            return state
        }

        onPublishEvent(HomeEvent.SetPagingLoading)

        val nextPage = state.articleUiState.pagingState.currentPage + 1
        onPublishEvent(HomeEvent.LoadArticles(nextPage))

        return state
    }
}