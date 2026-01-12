package dev.yveskalume.newsappp.ui.screens.home

import androidx.compose.runtime.Stable
import dev.yveskalume.newsappp.domain.model.Article
import dev.yveskalume.newsappp.domain.model.SourceItem

@Stable
sealed interface SourcesUiState {
    data object Loading : SourcesUiState
    data class Success(val sources: List<SourceItem>,val selected: SourceItem?) : SourcesUiState
    data class Error(val message: String) : SourcesUiState
}

@Stable
sealed interface NewsUiState {
    data object Loading : NewsUiState

    data class Success(
        val isRefreshing: Boolean = false,
        val articles: List<Article>,
        val pagingState: PagingState = PagingState.Idle(currentPage = 1),
    ) : NewsUiState

    data class Error(
        val message: String,
    ) : NewsUiState


    @Stable
    sealed interface PagingState {
        data class Idle(val currentPage: Int, val news: List<Article> = emptyList()) : PagingState
        data object Loading : PagingState
        data class Error(val message: String) : PagingState
        data object EndReached : PagingState

        val newsOrEmpty: List<Article>
            get() {
                return when (this) {
                    is Idle -> this.news
                    else -> emptyList()
                }
            }
    }

    val isLoadingMore: Boolean
        get() {
            return when (this) {
                is Success -> this.pagingState is PagingState.Loading
                else -> false
            }
        }

    val canLoadMore: Boolean
        get() {
            return when (this) {
                is Success -> this.pagingState !is PagingState.EndReached && this.pagingState !is PagingState.Loading
                else -> false
            }
        }
}

sealed interface RefreshUiState {
    data object Idle : RefreshUiState
    data object Refreshing : RefreshUiState
}


