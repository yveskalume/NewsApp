package dev.yveskalume.newsappp.ui.screens.home.reducers

import dev.yveskalume.newsappp.fake.FakeSourcesRepositoryFailure
import dev.yveskalume.newsappp.fake.FakeSourcesRepositorySuccess
import dev.yveskalume.newsappp.ui.screens.home.HomeEvent
import dev.yveskalume.newsappp.ui.screens.home.HomeUiState
import dev.yveskalume.newsappp.ui.screens.home.SourcesUiState
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class LoadSourcesReducerTest : ShouldSpec({

    should("update state with sources on success") {
        runTest {
            val reducer = LoadSourcesReducer(FakeSourcesRepositorySuccess())
            val state = HomeUiState.initial()

            val result = reducer.reduce(state, HomeEvent.LoadSources)

            result.sourcesUiState shouldBe SourcesUiState.Success(
                FakeSourcesRepositorySuccess.sampleSources
            )
        }
    }

    should("update state with error on failure") {
        runTest {
            val errorMessage = "Sources error"
            val reducer = LoadSourcesReducer(FakeSourcesRepositoryFailure(errorMessage))
            val state = HomeUiState.initial()

            val result = reducer.reduce(state, HomeEvent.LoadSources)

            result.sourcesUiState shouldBe SourcesUiState.Error(errorMessage)
        }
    }
})
