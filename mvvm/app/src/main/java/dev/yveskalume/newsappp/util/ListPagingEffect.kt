package dev.yveskalume.newsappp.util

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun ListPagingEffect(
    listState: LazyListState,
    canLoadMore: Boolean,
    onLoadMore: () -> Unit
) {
    LaunchedEffect(listState, canLoadMore) {
        snapshotFlow {
            listState.canScrollForward to listState.layoutInfo.totalItemsCount
        }.distinctUntilChanged().collect { (canScrollForward, totalItems) ->
            val shouldLoadMore = totalItems > 0 && !canScrollForward
            if (shouldLoadMore && canLoadMore) {
                onLoadMore()
            }
        }
    }
}