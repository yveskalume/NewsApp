package dev.yveskalume.newsappp.ui.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import dev.yveskalume.newsappp.ui.screens.search.SearchResultUiState
import dev.yveskalume.newsappp.ui.screens.search.SearchUiState

class SearchScreenPreviewProvider : PreviewParameterProvider<SearchUiState> {

    override val values: Sequence<SearchUiState> = sequenceOf(
        SearchUiState(
            query = "",
            searchResultUiState = SearchResultUiState.Idle
        ),
        SearchUiState(
            query = "kotlin",
            searchResultUiState = SearchResultUiState.Loading
        ),
        SearchUiState(
            query = "kotlin",
            searchResultUiState = SearchResultUiState.Success(
                news = PreviewSampleData.sampleArticles
            )
        ),
        SearchUiState(
            query = "kotlin",
            searchResultUiState = SearchResultUiState.Success(
                news = PreviewSampleData.sampleArticles,
                pagingState = SearchResultUiState.PagingState.Loading
            )
        ),
        SearchUiState(
            query = "missing",
            searchResultUiState = SearchResultUiState.Empty
        ),
        SearchUiState(
            query = "kotlin",
            searchResultUiState = SearchResultUiState.Error(message = "Search failed. Please retry.")
        )
    )
}
