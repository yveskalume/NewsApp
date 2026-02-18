package dev.yveskalume.newsappp.ui.screens.search.reducers

import dev.yveskalume.newsappp.fake.FakeArticleRepositoryConfigurable
import dev.yveskalume.newsappp.fake.FakeArticleRepositoryFailure
import dev.yveskalume.newsappp.fake.FakeArticleRepositorySuccess
import dev.yveskalume.newsappp.ui.screens.search.SearchEvent
import dev.yveskalume.newsappp.ui.screens.search.SearchResultUiState
import dev.yveskalume.newsappp.ui.screens.search.SearchUiState
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class SearchLoadArticlesReducerTest : ShouldSpec({

    should("set idle when query is blank") {
        runTest {
            val reducer = SearchLoadArticlesReducer(FakeArticleRepositorySuccess())
            val state = SearchUiState.initial().copy(query = "")

            val result = reducer.reduce(state, SearchEvent.LoadArticles(page = 1))

            result.searchResultUiState shouldBe SearchResultUiState.Idle
        }
    }

    should("set success when page 1 loads successfully") {
        runTest {
            val reducer = SearchLoadArticlesReducer(FakeArticleRepositorySuccess())
            val state = SearchUiState.initial().copy(query = "kotlin")

            val result = reducer.reduce(state, SearchEvent.LoadArticles(page = 1))

            result.searchResultUiState shouldBe SearchResultUiState.Success(
                news = FakeArticleRepositorySuccess.sampleArticles,
                pagingState = SearchResultUiState.PagingState.Idle(currentPage = 1)
            )
        }
    }

    should("merge news and update paging for page 2 success") {
        runTest {
            val fakeRepository = FakeArticleRepositoryConfigurable()
            val reducer = SearchLoadArticlesReducer(fakeRepository)
            val state = SearchUiState(
                query = "kotlin",
                searchResultUiState = SearchResultUiState.Success(
                    news = FakeArticleRepositorySuccess.sampleArticles,
                    pagingState = SearchResultUiState.PagingState.Idle(currentPage = 1)
                )
            )

            val result = reducer.reduce(state, SearchEvent.LoadArticles(page = 2))

            result.searchResultUiState shouldBe SearchResultUiState.Success(
                news = FakeArticleRepositorySuccess.sampleArticles + fakeRepository.page2Articles,
                pagingState = SearchResultUiState.PagingState.Idle(currentPage = 2)
            )
        }
    }

    should("set end reached when page 2 returns no articles") {
        runTest {
            val fakeRepository = FakeArticleRepositoryConfigurable().apply {
                page2Articles = emptyList()
            }
            val reducer = SearchLoadArticlesReducer(fakeRepository)
            val state = SearchUiState(
                query = "kotlin",
                searchResultUiState = SearchResultUiState.Success(
                    news = FakeArticleRepositorySuccess.sampleArticles,
                    pagingState = SearchResultUiState.PagingState.Idle(currentPage = 1)
                )
            )

            val result = reducer.reduce(state, SearchEvent.LoadArticles(page = 2))
            val success = result.searchResultUiState as SearchResultUiState.Success

            success.pagingState shouldBe SearchResultUiState.PagingState.EndReached
        }
    }

    should("set error state when page 1 fails") {
        runTest {
            val errorMessage = "Search failed"
            val reducer = SearchLoadArticlesReducer(FakeArticleRepositoryFailure(errorMessage))
            val state = SearchUiState.initial().copy(query = "kotlin")

            val result = reducer.reduce(state, SearchEvent.LoadArticles(page = 1))

            result.searchResultUiState shouldBe SearchResultUiState.Error(errorMessage)
        }
    }

    should("set paging error when page 2 fails from success state") {
        runTest {
            val errorMessage = "Load more failed"
            val reducer = SearchLoadArticlesReducer(FakeArticleRepositoryFailure(errorMessage))
            val state = SearchUiState(
                query = "kotlin",
                searchResultUiState = SearchResultUiState.Success(
                    news = FakeArticleRepositorySuccess.sampleArticles,
                    pagingState = SearchResultUiState.PagingState.Idle(currentPage = 1)
                )
            )

            val result = reducer.reduce(state, SearchEvent.LoadArticles(page = 2))
            val success = result.searchResultUiState as SearchResultUiState.Success

            success.pagingState shouldBe SearchResultUiState.PagingState.Error(errorMessage)
        }
    }
})
