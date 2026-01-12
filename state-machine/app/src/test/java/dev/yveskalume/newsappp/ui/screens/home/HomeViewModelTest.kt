package dev.yveskalume.newsappp.ui.screens.home

import dev.yveskalume.newsappp.fake.FakeArticleRepositoryConfigurable
import dev.yveskalume.newsappp.fake.FakeArticleRepositoryFailure
import dev.yveskalume.newsappp.fake.FakeArticleRepositorySuccess
import dev.yveskalume.newsappp.fake.FakeSourcesRepositoryFailure
import dev.yveskalume.newsappp.fake.FakeSourcesRepositorySuccess
import dev.yveskalume.newsappp.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // region Initial State Tests

    @Test
    fun `initial newsUiState should be Loading`() = runTest {
        // Given
        val viewModel = HomeViewModel(
            articleRepository = FakeArticleRepositorySuccess(),
            sourcesRepository = FakeSourcesRepositorySuccess()
        )

        // Then
        assertEquals(NewsUiState.Loading, viewModel.newsUiState.value)
    }

    @Test
    fun `initial sourcesUiState should be Loading`() = runTest {
        // Given
        val viewModel = HomeViewModel(
            articleRepository = FakeArticleRepositorySuccess(),
            sourcesRepository = FakeSourcesRepositorySuccess()
        )

        // Then
        assertEquals(SourcesUiState.Loading, viewModel.sourcesUiState.value)
    }

    @Test
    fun `initial refreshUiState should be Idle`() = runTest {
        // Given
        val viewModel = HomeViewModel(
            articleRepository = FakeArticleRepositorySuccess(),
            sourcesRepository = FakeSourcesRepositorySuccess()
        )

        // Then
        assertEquals(RefreshUiState.Idle, viewModel.refreshUiState.value)
    }

    // endregion

    // region News Success Tests

    @Test
    fun `newsUiState should emit Success with articles when fetch succeeds`() = runTest {
        // Given
        val fakeArticleRepository = FakeArticleRepositorySuccess()
        val viewModel = HomeViewModel(
            articleRepository = fakeArticleRepository,
            sourcesRepository = FakeSourcesRepositorySuccess()
        )

        // When
        val collectJob = launch(UnconfinedTestDispatcher()) {
            viewModel.newsUiState.collect()
        }
        advanceUntilIdle()

        // Then
        val newsState = viewModel.newsUiState.value
        assertTrue(newsState is NewsUiState.Success)
        val successState = newsState as NewsUiState.Success
        assertEquals(FakeArticleRepositorySuccess.sampleArticles, successState.articles)

        collectJob.cancel()
    }

    @Test
    fun `newsUiState Success should have correct initial paging state`() = runTest {
        // Given
        val viewModel = HomeViewModel(
            articleRepository = FakeArticleRepositorySuccess(),
            sourcesRepository = FakeSourcesRepositorySuccess()
        )

        // When
        val collectJob = launch(UnconfinedTestDispatcher()) {
            viewModel.newsUiState.collect()
        }
        advanceUntilIdle()

        // Then
        val newsState = viewModel.newsUiState.value
        assertTrue(newsState is NewsUiState.Success)
        val successState = newsState as NewsUiState.Success
        assertTrue(successState.pagingState is NewsUiState.PagingState.Idle)
        val pagingIdle = successState.pagingState as NewsUiState.PagingState.Idle
        assertEquals(1, pagingIdle.currentPage)

        collectJob.cancel()
    }

    // endregion

    // region News Failure Tests

    @Test
    fun `newsUiState should emit Error when fetch fails`() = runTest {
        // Given
        val errorMessage = "Custom network error"
        val viewModel = HomeViewModel(
            articleRepository = FakeArticleRepositoryFailure(errorMessage),
            sourcesRepository = FakeSourcesRepositorySuccess()
        )

        // When
        val collectJob = launch(UnconfinedTestDispatcher()) {
            viewModel.newsUiState.collect()
        }
        advanceUntilIdle()

        // Then
        val newsState = viewModel.newsUiState.value
        assertTrue(newsState is NewsUiState.Error)
        val errorState = newsState as NewsUiState.Error
        assertEquals(errorMessage, errorState.message)

        collectJob.cancel()
    }

    // endregion

    // region Sources Success Tests

    @Test
    fun `sourcesUiState should emit Success with sources when fetch succeeds`() = runTest {
        // Given
        val viewModel = HomeViewModel(
            articleRepository = FakeArticleRepositorySuccess(),
            sourcesRepository = FakeSourcesRepositorySuccess()
        )

        // When
        val collectJob = launch(UnconfinedTestDispatcher()) {
            viewModel.sourcesUiState.collect()
        }
        advanceUntilIdle()

        // Then
        val sourcesState = viewModel.sourcesUiState.value
        assertTrue(sourcesState is SourcesUiState.Success)
        val successState = sourcesState as SourcesUiState.Success
        assertEquals(FakeSourcesRepositorySuccess.sampleSources, successState.sources)

        collectJob.cancel()
    }

    @Test
    fun `sourcesUiState Success should have null selected initially`() = runTest {
        // Given
        val viewModel = HomeViewModel(
            articleRepository = FakeArticleRepositorySuccess(),
            sourcesRepository = FakeSourcesRepositorySuccess()
        )

        // When
        val collectJob = launch(UnconfinedTestDispatcher()) {
            viewModel.sourcesUiState.collect()
        }
        advanceUntilIdle()

        // Then
        val sourcesState = viewModel.sourcesUiState.value
        assertTrue(sourcesState is SourcesUiState.Success)
        val successState = sourcesState as SourcesUiState.Success
        assertNull(successState.selected)

        collectJob.cancel()
    }

    // endregion

    // region Sources Failure Tests

    @Test
    fun `sourcesUiState should emit Error when fetch fails`() = runTest {
        // Given
        val errorMessage = "Sources fetch error"
        val viewModel = HomeViewModel(
            articleRepository = FakeArticleRepositorySuccess(),
            sourcesRepository = FakeSourcesRepositoryFailure(errorMessage)
        )

        // When
        val collectJob = launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.sourcesUiState.collect()
        }
        advanceUntilIdle()

        // Then
        val sourcesState = viewModel.sourcesUiState.value
        assertTrue(sourcesState is SourcesUiState.Error)
        val errorState = sourcesState as SourcesUiState.Error
        assertEquals(errorMessage, errorState.message)

        collectJob.cancel()
    }

    // endregion

    // region Select Source Tests

    @Test
    fun `selectSource should update selected source in sourcesUiState`() = runTest {
        // Given
        val viewModel = HomeViewModel(
            articleRepository = FakeArticleRepositorySuccess(),
            sourcesRepository = FakeSourcesRepositorySuccess()
        )

        val collectJob = launch(UnconfinedTestDispatcher()) {
            viewModel.sourcesUiState.collect()
        }
        advanceUntilIdle()

        // When
        val sourceToSelect = FakeSourcesRepositorySuccess.sampleSources.first()
        viewModel.selectSource(sourceToSelect)
        advanceUntilIdle()

        // Then
        val sourcesState = viewModel.sourcesUiState.value
        assertTrue(sourcesState is SourcesUiState.Success)
        val successState = sourcesState as SourcesUiState.Success
        assertEquals(sourceToSelect, successState.selected)

        collectJob.cancel()
    }

    @Test
    fun `selectSource with same source should deselect it`() = runTest {
        // Given
        val viewModel = HomeViewModel(
            articleRepository = FakeArticleRepositorySuccess(),
            sourcesRepository = FakeSourcesRepositorySuccess()
        )

        val collectJob = launch(UnconfinedTestDispatcher()) {
            viewModel.sourcesUiState.collect()
        }
        advanceUntilIdle()

        // Select a source first
        val sourceToSelect = FakeSourcesRepositorySuccess.sampleSources.first()
        viewModel.selectSource(sourceToSelect)
        advanceUntilIdle()

        // When - select same source again
        viewModel.selectSource(sourceToSelect)
        advanceUntilIdle()

        // Then - should be deselected (null)
        val sourcesState = viewModel.sourcesUiState.value
        val successState = sourcesState as SourcesUiState.Success
        assertNull(successState.selected)

        collectJob.cancel()
    }

    @Test
    fun `selectSource with null should clear selection`() = runTest {
        // Given
        val viewModel = HomeViewModel(
            articleRepository = FakeArticleRepositorySuccess(),
            sourcesRepository = FakeSourcesRepositorySuccess()
        )

        val collectJob = launch(UnconfinedTestDispatcher()) {
            viewModel.sourcesUiState.collect()
        }
        advanceUntilIdle()

        // Select a source first
        viewModel.selectSource(FakeSourcesRepositorySuccess.sampleSources.first())
        advanceUntilIdle()

        // When
        viewModel.selectSource(null)
        advanceUntilIdle()

        // Then
        val sourcesState = viewModel.sourcesUiState.value as SourcesUiState.Success
        assertNull(sourcesState.selected)

        collectJob.cancel()
    }

    // endregion

    // region Refresh Tests

    @Test
    fun `refresh should set refreshUiState to Refreshing`() = runTest {
        // Given
        val viewModel = HomeViewModel(
            articleRepository = FakeArticleRepositorySuccess(),
            sourcesRepository = FakeSourcesRepositorySuccess()
        )

        val collectJob = launch(UnconfinedTestDispatcher()) {
            viewModel.newsUiState.collect()
        }
        advanceUntilIdle()

        // When
        viewModel.refresh()

        // Then - check immediate state after calling refresh
        assertEquals(RefreshUiState.Refreshing, viewModel.refreshUiState.value)

        collectJob.cancel()
    }

    @Test
    fun `refresh should eventually set refreshUiState back to Idle`() = runTest {
        // Given
        val viewModel = HomeViewModel(
            articleRepository = FakeArticleRepositorySuccess(),
            sourcesRepository = FakeSourcesRepositorySuccess()
        )

        val collectJob = launch(UnconfinedTestDispatcher()) {
            viewModel.newsUiState.collect()
        }
        advanceUntilIdle()

        // When
        viewModel.refresh()
        advanceUntilIdle()

        // Then
        assertEquals(RefreshUiState.Idle, viewModel.refreshUiState.value)

        collectJob.cancel()
    }

    // endregion

    // region Load More (Pagination) Tests

    @Test
    fun `loadMore should append articles when successful`() = runTest {
        // Given
        val fakeRepository = FakeArticleRepositoryConfigurable()
        val viewModel = HomeViewModel(
            articleRepository = fakeRepository,
            sourcesRepository = FakeSourcesRepositorySuccess()
        )

        val collectJob = launch(UnconfinedTestDispatcher()) {
            viewModel.newsUiState.collect()
        }
        advanceUntilIdle()

        val initialState = viewModel.newsUiState.value as NewsUiState.Success
        val initialArticleCount = initialState.articles.size

        // When
        viewModel.loadMore()
        advanceUntilIdle()

        // Then
        val finalState = viewModel.newsUiState.value
        assertTrue(finalState is NewsUiState.Success)
        val successState = finalState as NewsUiState.Success

        // Should have initial articles + page 2 articles
        val expectedCount = initialArticleCount + fakeRepository.page2Articles.size
        assertEquals(expectedCount, successState.articles.size)

        collectJob.cancel()
    }

    @Test
    fun `loadMore should set paging state to EndReached when no more articles`() = runTest {
        // Given
        val fakeRepository = FakeArticleRepositoryConfigurable().apply {
            page2Articles = emptyList() // Simulate no more articles
        }
        val viewModel = HomeViewModel(
            articleRepository = fakeRepository,
            sourcesRepository = FakeSourcesRepositorySuccess()
        )

        val collectJob = launch(UnconfinedTestDispatcher()) {
            viewModel.newsUiState.collect()
        }
        advanceUntilIdle()

        // When
        viewModel.loadMore()
        advanceUntilIdle()

        // Then
        val finalState = viewModel.newsUiState.value
        assertTrue(finalState is NewsUiState.Success)
        val successState = finalState as NewsUiState.Success
        assertTrue(successState.pagingState is NewsUiState.PagingState.EndReached)
        assertFalse(successState.canLoadMore)

        collectJob.cancel()
    }

    @Test
    fun `loadMore should not fetch when paging state is not Idle`() = runTest {
        // Given
        val fakeRepository = FakeArticleRepositoryConfigurable().apply {
            page2Articles = emptyList()
        }
        val viewModel = HomeViewModel(
            articleRepository = fakeRepository,
            sourcesRepository = FakeSourcesRepositorySuccess()
        )

        val collectJob = launch(UnconfinedTestDispatcher()) {
            viewModel.newsUiState.collect()
        }
        advanceUntilIdle()

        // Load more to reach EndReached state
        viewModel.loadMore()
        advanceUntilIdle()

        // When - try to load more again
        val stateBeforeSecondLoad = viewModel.newsUiState.value
        viewModel.loadMore()
        advanceUntilIdle()

        // Then - state should remain unchanged (EndReached)
        val stateAfterSecondLoad = viewModel.newsUiState.value
        assertEquals(stateBeforeSecondLoad, stateAfterSecondLoad)

        collectJob.cancel()
    }

    @Test
    fun `loadMore should set paging state to Error when fetch fails`() = runTest {
        // Given
        val errorMessage = "Failed to load more"
        val fakeRepository = FakeArticleRepositoryConfigurable()
        val viewModel = HomeViewModel(
            articleRepository = fakeRepository,
            sourcesRepository = FakeSourcesRepositorySuccess()
        )

        val collectJob = launch(UnconfinedTestDispatcher()) {
            viewModel.newsUiState.collect()
        }
        advanceUntilIdle()

        // Configure repository to fail for next call
        fakeRepository.shouldFail = true
        fakeRepository.errorMessage = errorMessage

        // When
        viewModel.loadMore()
        advanceUntilIdle()

        // Then
        val finalState = viewModel.newsUiState.value
        assertTrue(finalState is NewsUiState.Success)
        val successState = finalState as NewsUiState.Success
        assertTrue(successState.pagingState is NewsUiState.PagingState.Error)
        val pagingError = successState.pagingState as NewsUiState.PagingState.Error
        assertEquals(errorMessage, pagingError.message)

        collectJob.cancel()
    }

    // endregion

    // region UI State Properties Tests

    @Test
    fun `canLoadMore should be true when paging state is Idle`() = runTest {
        // Given
        val viewModel = HomeViewModel(
            articleRepository = FakeArticleRepositorySuccess(),
            sourcesRepository = FakeSourcesRepositorySuccess()
        )

        val collectJob = launch(UnconfinedTestDispatcher()) {
            viewModel.newsUiState.collect()
        }
        advanceUntilIdle()

        // Then
        val newsState = viewModel.newsUiState.value
        assertTrue(newsState.canLoadMore)

        collectJob.cancel()
    }

    @Test
    fun `canLoadMore should be false when paging state is EndReached`() = runTest {
        // Given
        val fakeRepository = FakeArticleRepositoryConfigurable().apply {
            page2Articles = emptyList()
        }
        val viewModel = HomeViewModel(
            articleRepository = fakeRepository,
            sourcesRepository = FakeSourcesRepositorySuccess()
        )

        val collectJob = launch(UnconfinedTestDispatcher()) {
            viewModel.newsUiState.collect()
        }
        advanceUntilIdle()

        // Load more to reach end
        viewModel.loadMore()
        advanceUntilIdle()

        // Then
        val finalState = viewModel.newsUiState.value
        assertFalse(finalState.canLoadMore)

        collectJob.cancel()
    }

    @Test
    fun `isLoadingMore should be false when paging state is Idle`() = runTest {
        // Given
        val viewModel = HomeViewModel(
            articleRepository = FakeArticleRepositorySuccess(),
            sourcesRepository = FakeSourcesRepositorySuccess()
        )

        val collectJob = launch(UnconfinedTestDispatcher()) {
            viewModel.newsUiState.collect()
        }
        advanceUntilIdle()

        // Then
        val newsState = viewModel.newsUiState.value
        assertFalse(newsState.isLoadingMore)

        collectJob.cancel()
    }

    // endregion
}
