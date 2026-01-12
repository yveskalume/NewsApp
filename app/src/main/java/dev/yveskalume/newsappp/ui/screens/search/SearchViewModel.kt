package dev.yveskalume.newsappp.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.yveskalume.newsappp.data.repository.ArticleRepository
import dev.yveskalume.newsappp.ui.screens.search.SearchUiState.PagingState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val PAGE_SIZE = 20

interface ISearchViewModel {
    fun onQueryChange(query: String)
    fun clearSearch()
    fun loadMore()
}

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
class SearchViewModel(
    private val articleRepository: ArticleRepository
) : ViewModel(), ISearchViewModel {

    private val queryFlow = MutableStateFlow("")

    /**
     * Used to force re-search when the same query is submitted again.
     * Also useful if when we'll add a retry button.
     */
    private val searchTrigger = MutableSharedFlow<Unit>(replay = 1)

    private val pagingStateFlow = MutableStateFlow<PagingState>(PagingState.Idle(currentPage = 1))

    /**
     * Emits only the *base* search result (page=1) and is only retriggered by query/explicit retry.
     * Paging changes must not invalidate this flow, otherwise we'll refetch page=1 on loadMore().
     */
    private val baseSearchFlow: Flow<SearchUiState> = combine(
        queryFlow.debounce(500).distinctUntilChanged(),
        searchTrigger.onStart { emit(Unit) },
    ) { query, _ ->
        query
    }.flatMapLatest { query ->
        flow {
            if (query.isBlank()) {
                emit(SearchUiState.Idle(query = ""))
                return@flow
            }

            // New base fetch, reset paging accumulator so we don't append stale pages.
            pagingStateFlow.value = PagingState.Idle(currentPage = 1)

            emit(SearchUiState.Loading(query = query))

            emit(
                articleRepository.getTopHeadlines(
                    query = query,
                    pageSize = PAGE_SIZE,
                    page = 1
                ).fold(
                    onSuccess = { articles ->
                        if (articles.isEmpty()) {
                            SearchUiState.Empty(query = query)
                        } else {
                            SearchUiState.Success(
                                query = query,
                                news = articles,
                                pagingState = PagingState.Idle(currentPage = 1)
                            )
                        }
                    },
                    onFailure = { error ->
                        SearchUiState.Error(
                            query = query,
                            message = error.message ?: "Search failed"
                        )
                    }
                )
            )
        }
    }

    val uiState: StateFlow<SearchUiState> = combine(
        baseSearchFlow,
        pagingStateFlow
    ) { base, pagingState ->
        when (base) {
            is SearchUiState.Success -> {
                val appended = pagingState.newsOrEmpty
                val merged = base.news + appended
                base.copy(news = merged, pagingState = pagingState)
            }
            else -> base
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SearchUiState.Idle()
    )

    override fun onQueryChange(query: String) {
        queryFlow.value = query
        // New query, reset paging.
        pagingStateFlow.value = PagingState.Idle(currentPage = 1)
        // Trigger a new search (debouncing is on queryFlow).
        searchTrigger.tryEmit(Unit)
    }

    override fun clearSearch() {
        queryFlow.value = ""
        pagingStateFlow.value = PagingState.Idle(currentPage = 1)
        searchTrigger.tryEmit(Unit)
    }

    override fun loadMore() {

        val idle = pagingStateFlow.value as? PagingState.Idle ?: return

        viewModelScope.launch {
            val nextPage = idle.currentPage + 1
            pagingStateFlow.value = PagingState.Loading

            articleRepository.getTopHeadlines(
                query = queryFlow.value,
                pageSize = PAGE_SIZE,
                page = nextPage
            ).onSuccess { articles ->
                pagingStateFlow.update {
                    if (articles.isEmpty()) {
                        PagingState.EndReached
                    } else {
                        PagingState.Idle(currentPage = nextPage, news = articles)
                    }
                }
            }.onFailure { error ->
                pagingStateFlow.value = PagingState.Error(error.message ?: "Failed to load more")
            }
        }
    }
}
