# MVVM Implementation

## 📱 Overview

This is a classic MVVM (Model-View-ViewModel) implementation of the News app, following modern Android development best practices.

## 🎯 Key Patterns

### Initial Data Fetching

Data fetching is triggered lazily using `onStart` on the `StateFlow` chain, not in an `init` block. This means the first request happens when the UI starts collecting the flow:

```kotlin
val sourcesUiState: StateFlow<SourcesUiState> = combine(
    refreshTrigger.onStart { emit(Unit) },
    sourcesStateFlow,
    selectedSourceFlow
) { ... }
.onStart {
    sourcesStateFlow.update { sourcesRepository.getSources().getOrThrow() }
}
.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = SourcesUiState.Loading
)
```

This approach:
- Avoids work in constructors/init blocks
- Makes testing easier and more predictable
- Delays network requests until the UI actually subscribes

### State as Sealed Interface

UI states are represented using `sealed interface` annotated with `@Stable`:

```kotlin
@Stable
sealed interface NewsUiState {
    data object Loading : NewsUiState
    data class Success(val articles: List<Article>, ...) : NewsUiState
    data class Error(val message: String) : NewsUiState
}
```

Nested states (like paging) also use sealed interfaces for type-safe handling.

### ViewModel Interface for Previews

To avoid breaking Compose previews, ViewModels implement an interface (`IHomeViewModel`, `ISearchViewModel`) that defines the actions:

```kotlin
interface IHomeViewModel {
    fun selectSource(source: SourceItem?)
    fun refresh()
    fun loadMore()
}

class HomeViewModel(...) : ViewModel(), IHomeViewModel { ... }
```

The screen composable receives the interface, not the concrete ViewModel:

```kotlin
@Composable
private fun HomeScreen(
    newsUiState: NewsUiState,
    viewModel: IHomeViewModel,  // Interface, not HomeViewModel
    ...
)
```

In previews, we create an anonymous implementation:

```kotlin
@Preview
@Composable
private fun HomeScreenPreview(...) {
    HomeScreen(
        viewModel = object : IHomeViewModel {
            override fun selectSource(source: SourceItem?) {}
            override fun refresh() {}
            override fun loadMore() {}
        },
        ...
    )
}
```

This keeps previews working without needing to instantiate real ViewModels with their dependencies.

## 🛠️ Tech Stack

- **Kotlin** + **Coroutines/Flow**
- **Jetpack Compose**
- **Koin** for DI
- **Kotlin Serialization**

---

**Note**: This is part of the Architecture Madness project.