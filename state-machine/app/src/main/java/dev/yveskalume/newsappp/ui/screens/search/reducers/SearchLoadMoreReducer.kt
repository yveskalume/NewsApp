package dev.yveskalume.newsappp.ui.screens.search.reducers

import dev.yveskalume.newsappp.core.Reducer
import dev.yveskalume.newsappp.ui.screens.search.SearchEvent
import dev.yveskalume.newsappp.ui.screens.search.SearchResultUiState
import dev.yveskalume.newsappp.ui.screens.search.SearchResultUiState.PagingState
import dev.yveskalume.newsappp.ui.screens.search.SearchUiState

class SearchLoadMoreReducer(
    private val onPublishEvent: (SearchEvent) -> Unit
) : Reducer<SearchUiState, SearchEvent.LoadMore> {

    override suspend fun reduce(
        state: SearchUiState,
        event: SearchEvent.LoadMore
    ): SearchUiState {
        if (
            state.searchResultUiState !is SearchResultUiState.Success ||
            state.searchResultUiState.pagingState !is PagingState.Idle
        ) {
            return state
        }

        val nextPage = state.searchResultUiState.pagingState.currentPage + 1
        onPublishEvent(SearchEvent.SetPagingLoading)
        onPublishEvent(SearchEvent.LoadArticles(nextPage))

        return state
    }
}

