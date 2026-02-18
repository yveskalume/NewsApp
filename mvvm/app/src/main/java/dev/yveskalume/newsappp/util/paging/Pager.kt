package dev.yveskalume.newsappp.util.paging

import androidx.compose.runtime.Stable
import dev.yveskalume.newsappp.util.stateIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock


@JvmInline
value class PageNumber(val value: Int) {
    init {
        require(value >= 1) { "PageNumber must be >= 1" }
    }

    operator fun inc() = PageNumber(value + 1)
}

sealed interface PageState {
    data object Idle : PageState
    data object Loading : PageState
    data object EndReached : PageState

    fun isLoading(): Boolean {
        return this is Loading
    }
}

sealed interface DataState<out T> {
    data object Loading : DataState<Nothing>
    data class Error<T>(val message: String) : DataState<T>
    data class Success<T>(val items: List<T>) : DataState<T>

    fun isLoading(): Boolean {
        return this is Loading
    }
}

@Stable
data class PageSnapshot<T>(
    val currentPage: PageNumber? = null,
    val pageState: PageState = PageState.Idle,
    val dataState: DataState<T> = DataState.Loading
)

interface Pager<T> {
    val snapshot: StateFlow<PageSnapshot<T>>
    fun refresh()
    fun loadMore()
    fun retry()
}

fun <T> createPage(
    scope: CoroutineScope,
    initialPage: PageNumber = PageNumber(1),
    loader: suspend (PageNumber) -> Result<List<T>>
): Pager<T> = PagerImpl(scope, initialPage, loader)

private class PagerImpl<T>(
    private val scope: CoroutineScope,
    private val initialPage: PageNumber,
    private val loader: suspend (PageNumber) -> Result<List<T>>
) : Pager<T> {

    private val mutex = Mutex()

    private val _snapshot = MutableStateFlow(PageSnapshot<T>())
    override val snapshot = _snapshot.asStateFlow().onStart {
        request(page = initialPage, reset = true)
    }.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PageSnapshot()
    )

    override fun refresh() {
        request(page = initialPage, reset = true)
    }

    override fun retry() {
        refresh()
    }

    override fun loadMore() {
        val currentSnapshot = _snapshot.value
        if (currentSnapshot.pageState != PageState.Idle) return

        request(
            page = if (currentSnapshot.currentPage == null) initialPage else currentSnapshot.currentPage.inc(),
            reset = false
        )
    }


    private fun request(page: PageNumber, reset: Boolean) = scope.launch {
        mutex.withLock {
            val currentSnapshot = _snapshot.value
            val previousItems = (currentSnapshot.dataState as? DataState.Success)?.items
            val hasPreviousData = previousItems != null

            _snapshot.update {
                createLoadingState(
                    hasPreviousData = hasPreviousData,
                    snapShot = currentSnapshot,
                    reset = reset
                )
            }

            loader(page).onSuccess { newItems ->
                _snapshot.update {
                    getSnapshotFor(
                        isInitial = reset,
                        newItems = newItems,
                        page = page,
                        before = currentSnapshot
                    )
                }
            }.onFailure { e ->
                _snapshot.update {
                    if (hasPreviousData) {
                        currentSnapshot.copy(pageState = PageState.Idle)
                    } else {
                        currentSnapshot.copy(
                            pageState = PageState.Idle,
                            dataState = DataState.Error(
                                message = e.message ?: "Paging failed"
                            )
                        )
                    }
                }
            }
        }
    }

    private fun createLoadingState(
        hasPreviousData: Boolean,
        snapShot: PageSnapshot<T>,
        reset: Boolean
    ): PageSnapshot<T> {
        return if (hasPreviousData && !reset) {
            snapShot.copy(pageState = PageState.Loading)
        } else {
            snapShot.copy(
                pageState = PageState.Idle,
                dataState = DataState.Loading
            )
        }
    }

    private fun getSnapshotFor(
        isInitial: Boolean,
        newItems: List<T>,
        page: PageNumber,
        before: PageSnapshot<T>
    ): PageSnapshot<T> {
        val items = if (isInitial) {
            newItems
        } else {
            val oldItems = (before.dataState as? DataState.Success)?.items.orEmpty()
            oldItems + newItems
        }

        return before.copy(
            dataState = DataState.Success(items),
            currentPage = before.currentPage.takeIf { newItems.isEmpty() } ?: page,
            pageState = getPageStateFor(newItems)
        )
    }

    private fun getPageStateFor(newItems: List<T>): PageState {
        return if (newItems.isEmpty()) {
            PageState.EndReached
        } else {
            PageState.Idle
        }
    }
}
