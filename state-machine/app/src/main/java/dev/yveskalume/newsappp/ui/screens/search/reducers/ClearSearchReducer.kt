package dev.yveskalume.newsappp.ui.screens.search.reducers

import dev.yveskalume.newsappp.core.Reducer
import dev.yveskalume.newsappp.ui.screens.search.SearchEvent
import dev.yveskalume.newsappp.ui.screens.search.SearchResultUiState
import dev.yveskalume.newsappp.ui.screens.search.SearchUiState

class ClearSearchReducer : Reducer<SearchUiState, SearchEvent.ClearSearch> {
    override suspend fun reduce(
        state: SearchUiState,
        event: SearchEvent.ClearSearch
    ): SearchUiState {
        return state.copy(
            query = "",
            searchResultUiState = SearchResultUiState.Idle
        )
    }
}