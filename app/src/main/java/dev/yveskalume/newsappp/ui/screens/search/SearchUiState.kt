package dev.yveskalume.newsappp.ui.screens.search

import androidx.compose.runtime.Stable
import dev.yveskalume.newsappp.domain.model.Article

@Stable
sealed interface SearchUiState {
    val query: String

    @Stable
    data class Idle(
        override val query: String = ""
    ) : SearchUiState

    @Stable
    data class Loading(
        override val query: String
    ) : SearchUiState

    @Stable
    data class Success(
        override val query: String,
        val news: List<Article>,
        val pagingState: PagingState = PagingState.Idle(currentPage = 1)
    ) : SearchUiState

    /** Search completed successfully, but no articles matched. */
    @Stable
    data class Empty(
        override val query: String
    ) : SearchUiState

    @Stable
    data class Error(
        override val query: String,
        val message: String
    ) : SearchUiState

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
