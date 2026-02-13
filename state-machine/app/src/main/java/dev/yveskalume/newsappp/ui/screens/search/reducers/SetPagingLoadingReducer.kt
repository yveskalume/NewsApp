package dev.yveskalume.newsappp.ui.screens.search.reducers

import dev.yveskalume.newsappp.core.Reducer
import dev.yveskalume.newsappp.ui.screens.search.SearchEvent
import dev.yveskalume.newsappp.ui.screens.search.SearchResultUiState
import dev.yveskalume.newsappp.ui.screens.search.SearchUiState

class SetPagingLoadingReducer : Reducer<SearchUiState, SearchEvent.SetPagingLoading> {
    override suspend fun reduce(
        state: SearchUiState,
        event: SearchEvent.SetPagingLoading
    ): SearchUiState {
        if (state.searchResultUiState !is SearchResultUiState.Success) {
            return state
        }

        return state.copy(
            searchResultUiState = state.searchResultUiState.copy(
                pagingState = SearchResultUiState.PagingState.Loading
            )
        )
    }
}

