package dev.yveskalume.newsappp.ui.screens.home.reducers

import dev.yveskalume.newsappp.fake.FakeArticleRepositoryConfigurable
import dev.yveskalume.newsappp.fake.FakeArticleRepositoryFailure
import dev.yveskalume.newsappp.fake.FakeArticleRepositorySuccess
import dev.yveskalume.newsappp.ui.screens.home.ArticleUiState
import dev.yveskalume.newsappp.ui.screens.home.HomeEvent
import dev.yveskalume.newsappp.ui.screens.home.HomeUiState
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class LoadArticlesReducerTest : ShouldSpec({

    should("set success state for page 1 on success") {
        runTest {
            val reducer = LoadArticlesReducer(FakeArticleRepositorySuccess())
            val state = HomeUiState.initial()

            val result = reducer.reduce(state, HomeEvent.LoadArticles(page = 1))

            result.articleUiState shouldBe ArticleUiState.Success(
                articles = FakeArticleRepositorySuccess.sampleArticles,
                pagingState = ArticleUiState.PagingState.Idle(currentPage = 1)
            )
        }
    }

    should("merge existing and new articles for page 2 success") {
        runTest {
            val fakeRepository = FakeArticleRepositoryConfigurable()
            val reducer = LoadArticlesReducer(fakeRepository)
            val state = HomeUiState.initial().copy(
                articleUiState = ArticleUiState.Success(
                    articles = FakeArticleRepositorySuccess.sampleArticles,
                    pagingState = ArticleUiState.PagingState.Idle(currentPage = 1)
                )
            )

            val result = reducer.reduce(state, HomeEvent.LoadArticles(page = 2))

            result.articleUiState shouldBe ArticleUiState.Success(
                articles = FakeArticleRepositorySuccess.sampleArticles + fakeRepository.page2Articles,
                pagingState = ArticleUiState.PagingState.Idle(currentPage = 2)
            )
        }
    }

    should("set paging error when page 2 fails from success state") {
        runTest {
            val errorMessage = "Load more failed"
            val reducer = LoadArticlesReducer(FakeArticleRepositoryFailure(errorMessage))
            val state = HomeUiState.initial().copy(
                articleUiState = ArticleUiState.Success(
                    articles = FakeArticleRepositorySuccess.sampleArticles,
                    pagingState = ArticleUiState.PagingState.Idle(currentPage = 1)
                )
            )

            val result = reducer.reduce(state, HomeEvent.LoadArticles(page = 2))
            val success = result.articleUiState as ArticleUiState.Success

            success.pagingState shouldBe ArticleUiState.PagingState.Error(errorMessage)
        }
    }

    should("set error state when page 1 fails") {
        runTest {
            val errorMessage = "Load failed"
            val reducer = LoadArticlesReducer(FakeArticleRepositoryFailure(errorMessage))
            val state = HomeUiState.initial()

            val result = reducer.reduce(state, HomeEvent.LoadArticles(page = 1))

            result.articleUiState shouldBe ArticleUiState.Error(errorMessage)
        }
    }
})
