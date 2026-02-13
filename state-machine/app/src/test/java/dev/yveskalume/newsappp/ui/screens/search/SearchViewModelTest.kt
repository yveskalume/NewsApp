package dev.yveskalume.newsappp.ui.screens.search

import dev.yveskalume.newsappp.fake.FakeArticleRepositoryConfigurable
import dev.yveskalume.newsappp.fake.FakeArticleRepositoryFailure
import dev.yveskalume.newsappp.fake.FakeArticleRepositorySuccess
import dev.yveskalume.newsappp.util.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // region Initial State Tests

    @Test
    fun `initial uiState should be Idle`() = runTest {
        // Given
        val viewModel = SearchViewModel(
            articleRepository = FakeArticleRepositorySuccess()
        )

        // Then
        assertEquals(SearchUiState.Idle, viewModel.uiState.value)
    }

    @Test
    fun `initial queryUiState should be empty string`() = runTest {
        // Given
        val viewModel = SearchViewModel(
            articleRepository = FakeArticleRepositorySuccess()
        )

        // Then
        assertEquals("", viewModel.queryUiState.value)
    }

    // endregion

    // region Query Change Tests

    @Test
    fun `onQueryChange should update queryUiState`() = runTest {
        // Given
        val viewModel = SearchViewModel(
            articleRepository = FakeArticleRepositorySuccess()
        )

        // When
        val testQuery = "technology"
        viewModel.onQueryChange(testQuery)
        advanceUntilIdle()

        // Then
        assertEquals(testQuery, viewModel.queryUiState.value)
    }

    @Test
    fun `onQueryChange with blank query should keep uiState as Idle`() = runTest {
        // Given
        val viewModel = SearchViewModel(
            articleRepository = FakeArticleRepositorySuccess()
        )

        val collectJob = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        // When
        viewModel.onQueryChange("   ")
        advanceTimeBy(600) // Pass debounce time
        advanceUntilIdle()

        // Then
        assertEquals(SearchUiState.Idle, viewModel.uiState.value)

        collectJob.cancel()
    }

    @Test
    fun `onQueryChange should trigger search after debounce`() = runTest {
        // Given
        val viewModel = SearchViewModel(
            articleRepository = FakeArticleRepositorySuccess()
        )

        val collectJob = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        // When
        viewModel.onQueryChange("test query")
        advanceTimeBy(600) // Pass debounce time (500ms)
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertTrue(state is SearchUiState.Success || state is SearchUiState.Empty)

        collectJob.cancel()
    }

    @Test
    fun `rapid query changes should only use final query after debounce`() = runTest {
        // Given
        val viewModel = SearchViewModel(
            articleRepository = FakeArticleRepositorySuccess()
        )

        val collectJob = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        // When - rapid typing
        viewModel.onQueryChange("t")
        advanceTimeBy(100)
        viewModel.onQueryChange("te")
        advanceTimeBy(100)
        viewModel.onQueryChange("tes")
        advanceTimeBy(100)
        viewModel.onQueryChange("test")

        advanceTimeBy(600)
        advanceUntilIdle()

        // Then - final query should be "test"
        assertEquals("test", viewModel.queryUiState.value)

        collectJob.cancel()
    }

    // endregion

    // region Search Success Tests

    @Test
    fun `uiState should emit Success with articles when search succeeds`() = runTest {
        // Given
        val viewModel = SearchViewModel(
            articleRepository = FakeArticleRepositorySuccess()
        )

        val collectJob = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        // When
        viewModel.onQueryChange("kotlin")
        advanceTimeBy(600)
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertTrue(state is SearchUiState.Success)
        val successState = state as SearchUiState.Success
        assertEquals(FakeArticleRepositorySuccess.sampleArticles, successState.news)

        collectJob.cancel()
    }

    @Test
    fun `uiState Success should have correct initial paging state`() = runTest {
        // Given
        val viewModel = SearchViewModel(
            articleRepository = FakeArticleRepositorySuccess()
        )

        val collectJob = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        // When
        viewModel.onQueryChange("news")
        advanceTimeBy(600)
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertTrue(state is SearchUiState.Success)
        val successState = state as SearchUiState.Success
        assertTrue(successState.pagingState is SearchUiState.PagingState.Idle)
        val pagingIdle = successState.pagingState as SearchUiState.PagingState.Idle
        assertEquals(1, pagingIdle.currentPage)

        collectJob.cancel()
    }

    // endregion

    // region Search Empty Tests

    @Test
    fun `uiState should emit Empty when search returns no results`() = runTest {
        // Given
        val fakeRepository = FakeArticleRepositoryConfigurable().apply {
            articles = emptyList()
        }
        val viewModel = SearchViewModel(
            articleRepository = fakeRepository
        )

        val collectJob = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        // When
        viewModel.onQueryChange("nonexistent query xyz123")
        advanceTimeBy(600)
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertEquals(SearchUiState.Empty, state)

        collectJob.cancel()
    }

    // endregion

    // region Search Failure Tests

    @Test
    fun `uiState should emit Error when search fails`() = runTest {
        // Given
        val errorMessage = "Search failed: network error"
        val viewModel = SearchViewModel(
            articleRepository = FakeArticleRepositoryFailure(errorMessage)
        )

        val collectJob = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        // When
        viewModel.onQueryChange("test")
        advanceTimeBy(600)
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertTrue(state is SearchUiState.Error)
        val errorState = state as SearchUiState.Error
        assertEquals(errorMessage, errorState.message)

        collectJob.cancel()
    }

    // endregion

    // region Clear Search Tests

    @Test
    fun `clearSearch should reset queryUiState to empty`() = runTest {
        // Given
        val viewModel = SearchViewModel(
            articleRepository = FakeArticleRepositorySuccess()
        )

        val collectJob = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        // First set a query
        viewModel.onQueryChange("test query")
        advanceTimeBy(600)
        advanceUntilIdle()

        // When
        viewModel.clearSearch()
        advanceUntilIdle()

        // Then
        assertEquals("", viewModel.queryUiState.value)

        collectJob.cancel()
    }

    @Test
    fun `clearSearch should reset uiState to Idle`() = runTest {
        // Given
        val viewModel = SearchViewModel(
            articleRepository = FakeArticleRepositorySuccess()
        )

        val collectJob = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        // First perform a search
        viewModel.onQueryChange("test query")
        advanceTimeBy(600)
        advanceUntilIdle()

        // Verify we got results
        assertTrue(viewModel.uiState.value is SearchUiState.Success)

        // When
        viewModel.clearSearch()
        advanceTimeBy(600)
        advanceUntilIdle()

        // Then
        assertEquals(SearchUiState.Idle, viewModel.uiState.value)

        collectJob.cancel()
    }

    // endregion

    // region Load More (Pagination) Tests

    @Test
    fun `loadMore should append articles when successful`() = runTest {
        // Given
        val fakeRepository = FakeArticleRepositoryConfigurable()
        val viewModel = SearchViewModel(
            articleRepository = fakeRepository
        )

        val collectJob = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        // Perform initial search
        viewModel.onQueryChange("test")
        advanceTimeBy(600)
        advanceUntilIdle()

        val initialState = viewModel.uiState.value as SearchUiState.Success
        val initialArticleCount = initialState.news.size

        // When
        viewModel.loadMore()
        advanceUntilIdle()

        // Then
        val finalState = viewModel.uiState.value
        assertTrue(finalState is SearchUiState.Success)
        val successState = finalState as SearchUiState.Success

        val expectedCount = initialArticleCount + fakeRepository.page2Articles.size
        assertEquals(expectedCount, successState.news.size)

        collectJob.cancel()
    }

    @Test
    fun `loadMore should set paging state to EndReached when no more articles`() = runTest {
        // Given
        val fakeRepository = FakeArticleRepositoryConfigurable().apply {
            page2Articles = emptyList()
        }
        val viewModel = SearchViewModel(
            articleRepository = fakeRepository
        )

        val collectJob = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        // Perform initial search
        viewModel.onQueryChange("test")
        advanceTimeBy(600)
        advanceUntilIdle()

        // When
        viewModel.loadMore()
        advanceUntilIdle()

        // Then
        val finalState = viewModel.uiState.value
        assertTrue(finalState is SearchUiState.Success)
        val successState = finalState as SearchUiState.Success
        assertTrue(successState.pagingState is SearchUiState.PagingState.EndReached)
        assertFalse(successState.canLoadMore)

        collectJob.cancel()
    }

    @Test
    fun `loadMore should not fetch when paging state is not Idle`() = runTest {
        // Given
        val fakeRepository = FakeArticleRepositoryConfigurable().apply {
            page2Articles = emptyList()
        }
        val viewModel = SearchViewModel(
            articleRepository = fakeRepository
        )

        val collectJob = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        // Perform initial search and load more to reach EndReached
        viewModel.onQueryChange("test")
        advanceTimeBy(600)
        advanceUntilIdle()

        viewModel.loadMore()
        advanceUntilIdle()

        // When - try to load more again
        val stateBeforeSecondLoad = viewModel.uiState.value
        viewModel.loadMore()
        advanceUntilIdle()

        // Then - state should remain unchanged
        val stateAfterSecondLoad = viewModel.uiState.value
        assertEquals(stateBeforeSecondLoad, stateAfterSecondLoad)

        collectJob.cancel()
    }

    @Test
    fun `loadMore should set paging state to Error when fetch fails`() = runTest {
        // Given
        val errorMessage = "Failed to load more results"
        val fakeRepository = FakeArticleRepositoryConfigurable()
        val viewModel = SearchViewModel(
            articleRepository = fakeRepository
        )

        val collectJob = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        // Perform initial search
        viewModel.onQueryChange("test")
        advanceTimeBy(600)
        advanceUntilIdle()

        // Configure repository to fail
        fakeRepository.shouldFail = true
        fakeRepository.errorMessage = errorMessage

        // When
        viewModel.loadMore()
        advanceUntilIdle()

        // Then
        val finalState = viewModel.uiState.value
        assertTrue(finalState is SearchUiState.Success)
        val successState = finalState as SearchUiState.Success
        assertTrue(successState.pagingState is SearchUiState.PagingState.Error)
        val pagingError = successState.pagingState as SearchUiState.PagingState.Error
        assertEquals(errorMessage, pagingError.message)

        collectJob.cancel()
    }

    @Test
    fun `loadMore should do nothing when uiState is not Success`() = runTest {
        // Given
        val viewModel = SearchViewModel(
            articleRepository = FakeArticleRepositorySuccess()
        )

        val collectJob = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        // Don't perform any search - state is Idle

        // When
        viewModel.loadMore()
        advanceUntilIdle()

        // Then - should remain Idle
        assertEquals(SearchUiState.Idle, viewModel.uiState.value)

        collectJob.cancel()
    }

    // endregion

    // region UI State Properties Tests

    @Test
    fun `isLoading should be true when state is Loading`() = runTest {
        // Then
        assertTrue(SearchUiState.Loading.isLoading)
        assertFalse(SearchUiState.Idle.isLoading)
        assertFalse(SearchUiState.Empty.isLoading)
    }

    @Test
    fun `canLoadMore should be true when paging state is Idle`() = runTest {
        // Given
        val viewModel = SearchViewModel(
            articleRepository = FakeArticleRepositorySuccess()
        )

        val collectJob = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect {}
        }

        // Perform search
        viewModel.onQueryChange("test")
        advanceTimeBy(600)
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertTrue(state.canLoadMore)

        collectJob.cancel()
    }

    @Test
    fun `canLoadMore should be false when state is not Success`() = runTest {
        // Then
        assertFalse(SearchUiState.Idle.canLoadMore)
        assertFalse(SearchUiState.Loading.canLoadMore)
        assertFalse(SearchUiState.Empty.canLoadMore)
        assertFalse(SearchUiState.Error("error").canLoadMore)
    }

    @Test
    fun `isLoadingMore should be false when paging state is Idle`() = runTest {
        // Given
        val viewModel = SearchViewModel(
            articleRepository = FakeArticleRepositorySuccess()
        )

        val collectJob = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        // Perform search
        viewModel.onQueryChange("test")
        advanceTimeBy(600)
        advanceUntilIdle()

        // Then
        val state = viewModel.uiState.value
        assertFalse(state.isLoadingMore)

        collectJob.cancel()
    }

    @Test
    fun `isLoadingMore should be false when state is not Success`() = runTest {
        // Then
        assertFalse(SearchUiState.Idle.isLoadingMore)
        assertFalse(SearchUiState.Loading.isLoadingMore)
        assertFalse(SearchUiState.Empty.isLoadingMore)
        assertFalse(SearchUiState.Error("error").isLoadingMore)
    }

    // endregion

    // region New Search Resets Paging Tests

    @Test
    fun `new search should reset paging state`() = runTest {
        // Given
        val fakeRepository = FakeArticleRepositoryConfigurable()
        val viewModel = SearchViewModel(
            articleRepository = fakeRepository
        )

        val collectJob = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.collect()
        }

        // Perform initial search and load more pages
        viewModel.onQueryChange("first query")
        advanceTimeBy(600)
        advanceUntilIdle()

        viewModel.loadMore()
        advanceUntilIdle()

        // Verify we're on page 2
        val stateAfterLoadMore = viewModel.uiState.value as SearchUiState.Success
        val pagingState = stateAfterLoadMore.pagingState as SearchUiState.PagingState.Idle
        assertEquals(2, pagingState.currentPage)

        // When - perform new search
        viewModel.onQueryChange("second query")
        advanceTimeBy(600)
        advanceUntilIdle()

        // Then - paging should reset to page 1
        val newState = viewModel.uiState.value
        assertTrue(newState is SearchUiState.Success)
        val newSuccessState = newState as SearchUiState.Success
        assertTrue(newSuccessState.pagingState is SearchUiState.PagingState.Idle)
        val newPagingState = newSuccessState.pagingState as SearchUiState.PagingState.Idle
        assertEquals(1, newPagingState.currentPage)

        collectJob.cancel()
    }

    // endregion
}
