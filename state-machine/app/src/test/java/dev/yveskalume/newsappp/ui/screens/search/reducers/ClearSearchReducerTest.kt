package dev.yveskalume.newsappp.ui.screens.search.reducers

import dev.yveskalume.newsappp.fake.FakeArticleRepositorySuccess
import dev.yveskalume.newsappp.ui.screens.search.SearchEvent
import dev.yveskalume.newsappp.ui.screens.search.SearchResultUiState
import dev.yveskalume.newsappp.ui.screens.search.SearchUiState
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class ClearSearchReducerTest : ShouldSpec({

    should("reset query and search state") {
        runTest {
            val reducer = ClearSearchReducer()
            val state = SearchUiState(
                query = "kotlin",
                searchResultUiState = SearchResultUiState.Success(
                    news = FakeArticleRepositorySuccess.sampleArticles
                )
            )

            val result = reducer.reduce(state, SearchEvent.ClearSearch)

            result.query shouldBe ""
            result.searchResultUiState shouldBe SearchResultUiState.Idle
        }
    }
})
