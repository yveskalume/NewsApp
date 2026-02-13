package dev.yveskalume.newsappp.ui.screens.search.reducers

import dev.yveskalume.newsappp.core.Reducer
import dev.yveskalume.newsappp.ui.screens.search.SearchEvent
import dev.yveskalume.newsappp.ui.screens.search.SearchResultUiState
import dev.yveskalume.newsappp.ui.screens.search.SearchUiState

class PerformSearchReducer(
    private val onPublishEvent: (SearchEvent) -> Unit
) : Reducer<SearchUiState, SearchEvent.PerformSearch> {
    override suspend fun reduce(
        state: SearchUiState,
        event: SearchEvent.PerformSearch
    ): SearchUiState {
        if (state.query.isBlank()) {
            return state.copy(searchResultUiState = SearchResultUiState.Idle)
        }

        onPublishEvent(SearchEvent.LoadArticles(page = 1))

        return state.copy(
            searchResultUiState = SearchResultUiState.Loading
        )
    }
}

