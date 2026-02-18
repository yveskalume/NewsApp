package dev.yveskalume.newsappp.ui.screens.home.reducers

import dev.yveskalume.newsappp.ui.screens.home.HomeEvent
import dev.yveskalume.newsappp.ui.screens.home.HomeUiState
import dev.yveskalume.newsappp.ui.screens.home.RefreshUiState
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class SetRefreshLoadingReducerTest : ShouldSpec({

    should("set refresh state to refreshing when loading is true") {
        runTest {
            val reducer = SetRefreshLoadingReducer()
            val state = HomeUiState.initial()

            val result = reducer.reduce(state, HomeEvent.SetRefreshLoading(true))

            result.refreshUiState shouldBe RefreshUiState.Refreshing
        }
    }

    should("set refresh state to idle when loading is false") {
        runTest {
            val reducer = SetRefreshLoadingReducer()
            val state = HomeUiState.initial()

            val result = reducer.reduce(state, HomeEvent.SetRefreshLoading(false))

            result.refreshUiState shouldBe RefreshUiState.Idle
        }
    }
})
