# MVVM Implementation Notes (This Project)

This folder contains the MVVM version of the app, with a few specific design decisions to keep behavior clear, testable, and preview-friendly.

## 1) ViewModel as interface in UI contracts

Screens consume `IHomeViewModel` / `ISearchViewModel` instead of concrete ViewModel classes.

Why:
- Keeps Composables decoupled from framework-heavy objects.
- Makes previews easy (we pass a small fake object in `@Preview`).
- Reduces test setup for UI-level tests.
- Limits the screen API to only user intents (`refresh`, `loadMore`, `selectSource`, etc.).

## 2) No network request in `init` and no fetch in `LaunchedEffect`

Requests start when the screen begins observing state, not earlier.

How:
- `sourcesUiState` uses `.onStart { ... }` before `stateIn(...)` in `HomeViewModel`.
- The custom `Pager` also triggers its first load from `snapshot.onStart { request(...) }`.
- Collection begins from screen observation (`collectAsStateWithLifecycle()`), so work starts only when UI is active.

Benefits:
- Avoids eager work in constructors (easier to test)
- Keeps startup behavior lifecycle-aware.
- Makes data flow timing easier to reason about.

## 3) Custom paging abstraction (`Pager`)

The project uses a custom pager (`util/paging/Pager.kt`) instead of Paging3.


## 4) Restartable `StateFlow` for manual re-collection

`RestartableStateFlow` is a project-specific utility that extends `StateFlow` with `restart()`.

## 5) Practical tradeoffs

Pros in this implementation:
- Clear lifecycle-driven fetch timing.
- Lightweight screen contracts via interfaces.

Cons in this implementation:
- More custom infrastructure to maintain (`Pager`, `RestartableStateFlow`, helpers).
