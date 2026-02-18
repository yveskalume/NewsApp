package dev.yveskalume.newsappp.ui.screens.search.reducers

import dev.yveskalume.newsappp.ui.screens.search.SearchEvent
import dev.yveskalume.newsappp.ui.screens.search.SearchResultUiState
import dev.yveskalume.newsappp.ui.screens.search.SearchUiState
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class PerformSearchReducerTest : ShouldSpec({

    should("reset to idle when query is blank") {
        runTest {
            val events = mutableListOf<SearchEvent>()
            val reducer = PerformSearchReducer(events::add)
            val state = SearchUiState.initial().copy(query = "   ")

            val result = reducer.reduce(state, SearchEvent.PerformSearch)

            result.searchResultUiState shouldBe SearchResultUiState.Idle
            events shouldBe emptyList()
        }
    }

    should("emit load event and set loading when query is not blank") {
        runTest {
            val events = mutableListOf<SearchEvent>()
            val reducer = PerformSearchReducer(events::add)
            val state = SearchUiState.initial().copy(query = "kotlin")

            val result = reducer.reduce(state, SearchEvent.PerformSearch)

            result.searchResultUiState shouldBe SearchResultUiState.Loading
            events shouldBe listOf(SearchEvent.LoadArticles(page = 1))
        }
    }
})
