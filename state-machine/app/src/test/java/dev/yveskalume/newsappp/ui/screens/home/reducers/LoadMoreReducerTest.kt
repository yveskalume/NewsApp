package dev.yveskalume.newsappp.ui.screens.home.reducers

import dev.yveskalume.newsappp.fake.FakeArticleRepositorySuccess
import dev.yveskalume.newsappp.ui.screens.home.ArticleUiState
import dev.yveskalume.newsappp.ui.screens.home.HomeEvent
import dev.yveskalume.newsappp.ui.screens.home.HomeUiState
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class LoadMoreReducerTest : ShouldSpec({

    should("publish paging loading and next page events when paging is idle") {
        runTest {
            val events = mutableListOf<HomeEvent>()
            val reducer = LoadMoreReducer(events::add)
            val state = HomeUiState.initial().copy(
                articleUiState = ArticleUiState.Success(
                    articles = FakeArticleRepositorySuccess.sampleArticles,
                    pagingState = ArticleUiState.PagingState.Idle(currentPage = 1)
                )
            )

            val result = reducer.reduce(state, HomeEvent.LoadMore)

            (result === state) shouldBe true
            events shouldBe listOf(
                HomeEvent.SetPagingLoading,
                HomeEvent.LoadArticles(page = 2)
            )
        }
    }

    should("do nothing when paging is not idle") {
        runTest {
            val events = mutableListOf<HomeEvent>()
            val reducer = LoadMoreReducer(events::add)
            val state = HomeUiState.initial().copy(
                articleUiState = ArticleUiState.Success(
                    articles = FakeArticleRepositorySuccess.sampleArticles,
                    pagingState = ArticleUiState.PagingState.EndReached
                )
            )

            val result = reducer.reduce(state, HomeEvent.LoadMore)

            (result === state) shouldBe true
            events shouldBe emptyList()
        }
    }
})
