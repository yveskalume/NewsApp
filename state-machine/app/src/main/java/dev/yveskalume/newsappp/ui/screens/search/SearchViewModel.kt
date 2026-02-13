package dev.yveskalume.newsappp.ui.screens.search

import androidx.compose.runtime.Immutable
import dev.yveskalume.newsappp.core.BaseViewModel
import dev.yveskalume.newsappp.core.StateManager

@Immutable
class SearchViewModel(
    stateManager: StateManager<SearchUiState, SearchEvent>
) : BaseViewModel<SearchUiState, SearchEvent>(stateManager)

