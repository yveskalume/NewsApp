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
class SetPagingLoadingReducerTest : ShouldSpec({

    should("set paging state to loading when article state is success") {
        runTest {
            val reducer = SetPagingLoadingReducer()
            val state = HomeUiState.initial().copy(
                articleUiState = ArticleUiState.Success(
                    articles = FakeArticleRepositorySuccess.sampleArticles,
                    pagingState = ArticleUiState.PagingState.Idle(currentPage = 1)
                )
            )

            val result = reducer.reduce(state, HomeEvent.SetPagingLoading)
            val success = result.articleUiState as ArticleUiState.Success

            success.pagingState shouldBe ArticleUiState.PagingState.Loading
        }
    }

    should("keep the same state instance when article state is not success") {
        runTest {
            val reducer = SetPagingLoadingReducer()
            val state = HomeUiState.initial().copy(articleUiState = ArticleUiState.Loading)

            val result = reducer.reduce(state, HomeEvent.SetPagingLoading)

            (result === state) shouldBe true
        }
    }
})
