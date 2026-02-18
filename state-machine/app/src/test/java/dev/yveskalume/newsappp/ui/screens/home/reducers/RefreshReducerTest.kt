package dev.yveskalume.newsappp.ui.screens.home.reducers

import dev.yveskalume.newsappp.ui.screens.home.HomeEvent
import dev.yveskalume.newsappp.ui.screens.home.HomeUiState
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class RefreshReducerTest : ShouldSpec({

    should("publish refresh events in order") {
        runTest {
            val events = mutableListOf<HomeEvent>()
            val reducer = RefreshReducer(events::add)
            val state = HomeUiState.initial()

            val result = reducer.reduce(state, HomeEvent.Refresh)

            (result === state) shouldBe true
            events shouldBe listOf(
                HomeEvent.SetRefreshLoading(true),
                HomeEvent.LoadArticles(),
                HomeEvent.SetRefreshLoading(false)
            )
        }
    }
})
