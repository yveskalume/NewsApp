package dev.yveskalume.newsappp.ui.screens.search.reducers

import dev.yveskalume.newsappp.core.Reducer
import dev.yveskalume.newsappp.ui.screens.search.SearchEvent
import dev.yveskalume.newsappp.ui.screens.search.SearchResultUiState
import dev.yveskalume.newsappp.ui.screens.search.SearchUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchQueryChangedReducer(
    private val coroutineScope: CoroutineScope,
    private val onPublishEvent: (SearchEvent) -> Unit
) : Reducer<SearchUiState, SearchEvent.QueryChanged> {

    private var debounceJob: Job? = null

    override suspend fun reduce(
        state: SearchUiState,
        event: SearchEvent.QueryChanged
    ): SearchUiState {
        if (state.query == event.query) {
            return state
        }

        debounceJob?.cancel()
        if (event.query.isBlank()) {
            return state.copy(
                query = event.query,
                searchResultUiState = SearchResultUiState.Idle
            )
        }

        debounceJob = coroutineScope.launch {
            delay(DEBOUNCE_MILLIS)
            onPublishEvent(SearchEvent.PerformSearch)
        }

        return state.copy(query = event.query)
    }

    companion object Companion {
        private const val DEBOUNCE_MILLIS = 500L
    }
}

