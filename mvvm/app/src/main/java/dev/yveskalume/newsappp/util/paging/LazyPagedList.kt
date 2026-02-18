package dev.yveskalume.newsappp.util.paging

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext


/**
 * A Composable that triggers a side effect for pagination when the end of a list is reached.
 *
 * This function is a `LazyColumn` to implement
 * infinite scrolling.
 *
 * @param state The state of the paged list, containing the `LazyListState` and the `PagerState`.
 * @param onLoadMore A lambda function to be invoked when more items need to be loaded. This is
 *                   typically where you would trigger the next page request from your data source.
 * @param modifier The modifier to be applied to this composable.
 * @param contentPadding A padding to be applied to the content. Although this function does not
 *                       render content, this parameter is kept for signature consistency with
 *                       list composables but is not used.
 * @param content The content of the lazy list. Although this function does not render the content
 *                directly, this parameter is kept for signature consistency with list composables
 *                but is not used.
 */
@Composable
fun LazyPagedList(
    state: LazyPagedListState,
    onLoadMore: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
    content: LazyListScope.() -> Unit,
) {
    val listState = state.lazyListState
    val pagerState = state.pagerState.pageState

    LazyColumn(
        state = listState,
        modifier = modifier,
        contentPadding = contentPadding,
        content = content
    )

    LaunchedEffect(listState, pagerState) {
        snapshotFlow {
            listState.canScrollForward to listState.layoutInfo.totalItemsCount
        }.distinctUntilChanged().collect { (canScrollForward, totalItems) ->
            val shouldLoadMore = totalItems > 0 && !canScrollForward
            val canLoadMore = pagerState is PageState.Idle
            if (shouldLoadMore && canLoadMore) {
                onLoadMore()
            }
        }
    }
}


@Composable
fun rememberLazyPagedListState(
    pagerState: PageSnapshot<*>,
    listState: LazyListState = rememberLazyListState(),
): LazyPagedListState {
    return remember(listState, pagerState) {
        LazyPagedListState(listState, pagerState)
    }
}

data class LazyPagedListState(
    val lazyListState: LazyListState,
    val pagerState: PageSnapshot<*>
)

@Composable
fun <T> Pager<T>.collectAsStateWithLifecycle(
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    context: CoroutineContext = EmptyCoroutineContext,
): State<PageSnapshot<T>> =
    snapshot.collectAsStateWithLifecycle(
        lifecycle = lifecycleOwner.lifecycle,
        minActiveState = minActiveState,
        context = context,
    )