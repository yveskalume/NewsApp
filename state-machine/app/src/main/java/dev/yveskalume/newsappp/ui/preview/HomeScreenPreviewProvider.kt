package dev.yveskalume.newsappp.ui.preview

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import dev.yveskalume.newsappp.ui.screens.home.ArticleUiState
import dev.yveskalume.newsappp.ui.screens.home.HomeUiState
import dev.yveskalume.newsappp.ui.screens.home.RefreshUiState
import dev.yveskalume.newsappp.ui.screens.home.SourcesUiState

class HomeScreenPreviewProvider : PreviewParameterProvider<HomeUiState> {

    override val values: Sequence<HomeUiState> = sequenceOf(
        // Success state with articles
        HomeUiState(
            sourcesUiState = SourcesUiState.Success(
                sources = PreviewSampleData.sampleSources,
                selected = null
            ),
            articleUiState = ArticleUiState.Success(
                articles = PreviewSampleData.sampleArticles
            ),
            selectedSource = null,
            refreshUiState = RefreshUiState.Idle
        ),
        // Success state with selected source
        HomeUiState(
            sourcesUiState = SourcesUiState.Success(
                sources = PreviewSampleData.sampleSources,
                selected = PreviewSampleData.sampleSources.first()
            ),
            articleUiState = ArticleUiState.Success(
                articles = PreviewSampleData.sampleArticles.take(2)
            ),
            selectedSource = PreviewSampleData.sampleSources.first(),
            refreshUiState = RefreshUiState.Idle
        ),
        // Loading state
        HomeUiState(
            sourcesUiState = SourcesUiState.Loading,
            articleUiState = ArticleUiState.Loading,
            selectedSource = null,
            refreshUiState = RefreshUiState.Idle
        ),
        // Error state
        HomeUiState(
            sourcesUiState = SourcesUiState.Success(
                sources = PreviewSampleData.sampleSources,
                selected = null
            ),
            articleUiState = ArticleUiState.Error(
                message = "Failed to load news. Please check your internet connection."
            ),
            selectedSource = null,
            refreshUiState = RefreshUiState.Idle
        ),
        // Empty state
        HomeUiState(
            sourcesUiState = SourcesUiState.Success(
                sources = PreviewSampleData.sampleSources,
                selected = null
            ),
            articleUiState = ArticleUiState.Success(
                articles = emptyList()
            ),
            selectedSource = null,
            refreshUiState = RefreshUiState.Idle
        )
    )
}