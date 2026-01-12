package dev.yveskalume.newsappp.ui.screens.search

import androidx.compose.runtime.Stable
import dev.yveskalume.newsappp.domain.model.Article

@Stable
sealed interface SearchUiState {
    data object Idle : SearchUiState

    data object Loading : SearchUiState


    data class Success(
        val news: List<Article>,
        val pagingState: PagingState = PagingState.Idle(currentPage = 1)
    ) : SearchUiState

    /** Search completed successfully, but no articles matched. */
    data object Empty : SearchUiState

    data class Error(
        val message: String
    ) : SearchUiState

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

    val isLoading: Boolean
        get() = this is Loading

    val isLoadingMore: Boolean
        get() = (this as? Success)?.pagingState is PagingState.Loading

    val canLoadMore: Boolean
        get() {
            val paging = (this as? Success)?.pagingState ?: return false
            return paging !is PagingState.EndReached && paging !is PagingState.Loading
        }
}
