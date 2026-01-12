package dev.yveskalume.newsappp.ui.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import dev.yveskalume.newsappp.ui.screens.home.NewsUiState
import dev.yveskalume.newsappp.ui.screens.home.SourcesUiState

data class HomeScreenPreviewData(
    val sourcesUiState: SourcesUiState,
    val newsUiState: NewsUiState
)

class HomeScreenPreviewProvider : PreviewParameterProvider<HomeScreenPreviewData> {

    override val values: Sequence<HomeScreenPreviewData> = sequenceOf(
        // Success state with articles
        HomeScreenPreviewData(
            sourcesUiState = SourcesUiState.Success(
                sources = PreviewSampleData.sampleSources,
                selected = null
            ),
            newsUiState = NewsUiState.Success(
                articles = PreviewSampleData.sampleArticles
            )
        ),
        // Success state with selected source
        HomeScreenPreviewData(
            sourcesUiState = SourcesUiState.Success(
                sources = PreviewSampleData.sampleSources,
                selected = PreviewSampleData.sampleSources.first()
            ),
            newsUiState = NewsUiState.Success(
                articles = PreviewSampleData.sampleArticles.take(2)
            )
        ),
        // Loading state
        HomeScreenPreviewData(
            sourcesUiState = SourcesUiState.Loading,
            newsUiState = NewsUiState.Loading
        ),
        // Error state
        HomeScreenPreviewData(
            sourcesUiState = SourcesUiState.Success(
                sources = PreviewSampleData.sampleSources,
                selected = null
            ),
            newsUiState = NewsUiState.Error(
                message = "Failed to load news. Please check your internet connection."
            )
        ),
        // Empty state
        HomeScreenPreviewData(
            sourcesUiState = SourcesUiState.Success(
                sources = PreviewSampleData.sampleSources,
                selected = null
            ),
            newsUiState = NewsUiState.Success(
                articles = emptyList()
            )
        )
    )
}