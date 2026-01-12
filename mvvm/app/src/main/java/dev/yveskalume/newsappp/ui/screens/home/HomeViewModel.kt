package dev.yveskalume.newsappp.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.yveskalume.newsappp.data.repository.ArticleRepository
import dev.yveskalume.newsappp.data.repository.SourcesRepository
import dev.yveskalume.newsappp.domain.model.SourceItem
import dev.yveskalume.newsappp.ui.screens.home.NewsUiState.PagingState
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val PAGE_SIZE = 20

interface IHomeViewModel {
    fun selectSource(source: SourceItem?)
    fun refresh()
    fun loadMore()
}

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModel(
    private val articleRepository: ArticleRepository,
    private val sourcesRepository: SourcesRepository
) : ViewModel(), IHomeViewModel {

    private val refreshTrigger = MutableSharedFlow<Unit>(replay = 1)
    private val selectedSourceFlow = MutableStateFlow<SourceItem?>(null)
    private val pagingStateFlow = MutableStateFlow<PagingState>(PagingState.Idle(currentPage = 1))

    private val sourcesStateFlow = MutableStateFlow<List<SourceItem>>(emptyList())

    val refreshUiState: StateFlow<RefreshUiState>
        field = MutableStateFlow<RefreshUiState>(RefreshUiState.Idle)

    val sourcesUiState: StateFlow<SourcesUiState> = combine(
        refreshTrigger.onStart { emit(Unit) },
        sourcesStateFlow,
        selectedSourceFlow
    ) { _, sources, selectedSource ->
        SourcesUiState.Success(sources = sources, selected = selectedSource)
    }.onStart {
        sourcesStateFlow.update { sourcesRepository.getSources().getOrThrow() }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SourcesUiState.Loading
    )

    /**
     * Base headlines fetch (page=1). Only refresh/source changes should trigger this.
     * Paging state must not be an input here, otherwise loadMore() causes a page=1 refetch.
     */
    private val baseNewsFlow: Flow<NewsUiState> = combine(
        refreshTrigger.onStart { emit(Unit) },
        selectedSourceFlow
    ) { _, selectedSource ->
        selectedSource
    }.flatMapLatest { selectedSource ->
        flow {
            // New base fetch, reset paging accumulator so we don't append stale pages.
            pagingStateFlow.value = PagingState.Idle(currentPage = 1)

            emit(NewsUiState.Loading)

            emit(
                articleRepository.getTopHeadlines(
                    sources = selectedSource?.id,
                    pageSize = PAGE_SIZE,
                    page = 1
                ).fold(
                    onSuccess = { articles ->
                        NewsUiState.Success(
                            articles = articles,
                            pagingState = PagingState.Idle(currentPage = 1)
                        )
                    },
                    onFailure = { error ->
                        NewsUiState.Error(
                            message = error.message ?: "Failed to load news",
                        )
                    }
                )
            )
            refreshUiState.update { RefreshUiState.Idle }
        }
    }

    val newsUiState: StateFlow<NewsUiState> = combine(
        baseNewsFlow,
        pagingStateFlow
    ) { base, pagingState ->
        when (base) {
            is NewsUiState.Success -> {
                val merged = base.articles + pagingState.newsOrEmpty
                base.copy(articles = merged, pagingState = pagingState)
            }
            else -> base
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = NewsUiState.Loading
    )

    override fun selectSource(source: SourceItem?) {
        selectedSourceFlow.tryEmit(source.takeIf { it?.id != selectedSourceFlow.value?.id })
    }

    override fun refresh() {
        refreshUiState.update { RefreshUiState.Refreshing }
        refreshTrigger.tryEmit(Unit)
    }

    override fun loadMore() {
        val currentList = (newsUiState.value as? NewsUiState.Success) ?: return

        if (currentList.pagingState !is PagingState.Idle) {
            return
        }

        viewModelScope.launch {
            val nextPage = currentList.pagingState.currentPage + 1
            pagingStateFlow.update { PagingState.Loading }

            articleRepository.getTopHeadlines(
                sources = selectedSourceFlow.value?.id,
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
                pagingStateFlow.update {
                    PagingState.Error(
                        error.message ?: "Failed to load more news"
                    )
                }
            }
        }
    }
}
