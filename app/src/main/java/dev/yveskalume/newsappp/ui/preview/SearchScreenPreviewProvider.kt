package dev.yveskalume.newsappp.ui.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import dev.yveskalume.newsappp.ui.screens.search.SearchUiState

class SearchScreenPreviewProvider : PreviewParameterProvider<SearchUiState> {

    override val values: Sequence<SearchUiState> = sequenceOf(
        SearchUiState.Idle(query = ""),
        SearchUiState.Loading(query = "android"),
        SearchUiState.Success(
            query = "android",
            news = PreviewSampleData.sampleArticles
        ),
        SearchUiState.Success(
            query = "android",
            news = PreviewSampleData.sampleArticles,
            pagingState = SearchUiState.PagingState.Loading
        ),
        SearchUiState.Empty(query = "somethingthatdoesnotexist"),
        SearchUiState.Error(query = "android", message = "Search failed. Please retry.")
    )
}
