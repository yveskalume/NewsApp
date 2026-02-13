package dev.yveskalume.newsappp.ui.screens.search

import dev.yveskalume.newsappp.core.Event

sealed interface SearchEvent : Event {
    data class QueryChanged(val query: String) : SearchEvent
    data object PerformSearch : SearchEvent
    data object ClearSearch : SearchEvent
    data object LoadMore : SearchEvent

    /** System event **/
    data class LoadArticles(val page: Int = 1) : SearchEvent
    data object SetPagingLoading : SearchEvent
}

