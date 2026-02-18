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
class SetPagingLoadingReducerTest : ShouldSpec({

    should("set paging state to loading when result state is success") {
        runTest {
            val reducer = SetPagingLoadingReducer()
            val state = SearchUiState(
                query = "kotlin",
                searchResultUiState = SearchResultUiState.Success(
                    news = FakeArticleRepositorySuccess.sampleArticles,
                    pagingState = SearchResultUiState.PagingState.Idle(currentPage = 1)
                )
            )

            val result = reducer.reduce(state, SearchEvent.SetPagingLoading)
            val success = result.searchResultUiState as SearchResultUiState.Success

            success.pagingState shouldBe SearchResultUiState.PagingState.Loading
        }
    }

    should("keep the same state instance when result state is not success") {
        runTest {
            val reducer = SetPagingLoadingReducer()
            val state = SearchUiState.initial().copy(searchResultUiState = SearchResultUiState.Idle)

            val result = reducer.reduce(state, SearchEvent.SetPagingLoading)

            (result === state) shouldBe true
        }
    }
})
