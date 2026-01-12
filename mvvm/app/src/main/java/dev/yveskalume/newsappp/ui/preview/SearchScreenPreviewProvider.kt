package dev.yveskalume.newsappp.ui.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import dev.yveskalume.newsappp.ui.screens.search.SearchUiState

class SearchScreenPreviewProvider : PreviewParameterProvider<SearchUiState> {

    override val values: Sequence<SearchUiState> = sequenceOf(
        SearchUiState.Idle,
        SearchUiState.Loading,
        SearchUiState.Success(
            news = PreviewSampleData.sampleArticles
        ),
        SearchUiState.Success(
            news = PreviewSampleData.sampleArticles,
            pagingState = SearchUiState.PagingState.Loading
        ),
        SearchUiState.Empty,
        SearchUiState.Error(message = "Search failed. Please retry.")
    )
}
