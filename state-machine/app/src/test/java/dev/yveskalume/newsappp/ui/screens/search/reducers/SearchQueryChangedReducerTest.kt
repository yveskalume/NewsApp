package dev.yveskalume.newsappp.ui.screens.search.reducers

import dev.yveskalume.newsappp.ui.screens.search.SearchEvent
import dev.yveskalume.newsappp.ui.screens.search.SearchResultUiState
import dev.yveskalume.newsappp.ui.screens.search.SearchUiState
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class SearchQueryChangedReducerTest : ShouldSpec({

    should("ignore identical query") {
        runTest {
            val events = mutableListOf<SearchEvent>()
            val reducer = SearchQueryChangedReducer(this, events::add)
            val state = SearchUiState.initial().copy(query = "kotlin")

            val result = reducer.reduce(state, SearchEvent.QueryChanged("kotlin"))

            (result === state) shouldBe true
            events shouldBe emptyList()
        }
    }

    should("reset state when query becomes blank") {
        runTest {
            val events = mutableListOf<SearchEvent>()
            val reducer = SearchQueryChangedReducer(this, events::add)
            val state = SearchUiState.initial().copy(query = "kotlin")

            val result = reducer.reduce(state, SearchEvent.QueryChanged(""))

            result.query shouldBe ""
            result.searchResultUiState shouldBe SearchResultUiState.Idle
            events shouldBe emptyList()
        }
    }

    should("emit perform search after debounce when query changes") {
        runTest {
            val events = mutableListOf<SearchEvent>()
            val reducer = SearchQueryChangedReducer(this, events::add)
            val state = SearchUiState.initial()

            val result = reducer.reduce(state, SearchEvent.QueryChanged("kotlin"))

            result.query shouldBe "kotlin"
            advanceTimeBy(600)
            events shouldBe listOf(SearchEvent.PerformSearch)
        }
    }
})
