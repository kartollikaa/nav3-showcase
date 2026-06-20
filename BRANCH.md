# flow1/03-viewmodel-decorator — Scoping a ViewModel to a destination

**Goal:** make each destination own its ViewModel, so the VM is **created when you navigate to the
screen and cleared when you leave it** — and show what goes wrong without this.

## What changed vs `flow1/02-scenes`
- `:feature:catalog` gained a `ProductDetailViewModel`, a `ProductRepository`, and a manual-DI
  `CatalogGraph` (the "Dagger seam").
- `NavDisplay` now passes `entryDecorators = listOf(rememberSaveableStateHolderNavEntryDecorator(), rememberViewModelStoreNavEntryDecorator())`.
- The `ProductDetail` entry builds its VM with `viewModel { ProductDetailViewModel(...) }`.

## The one line that matters

```kotlin
NavDisplay(
    entryDecorators = listOf(
        rememberSaveableStateHolderNavEntryDecorator(), // must be first; powers rememberSaveable {}
        rememberViewModelStoreNavEntryDecorator(),      // ← gives each NavEntry its own ViewModelStore
    ),
    /* … */
    entryProvider = entryProvider {
        entry<ProductDetail> { key ->
            val vm = viewModel { ProductDetailViewModel(CatalogGraph.productRepository, key.id) }
            ProductDetailScreen(product = vm.product, clicks = vm.clicks, /* … */)
        }
    },
)
```

### What a decorator is
A `NavEntryDecorator` wraps the content of **every** entry — a place to install per-entry
infrastructure via `CompositionLocalProvider` and to clean up when the entry is popped. Nav3 ships two
you should almost always use, in this order:

1. **`rememberSaveableStateHolderNavEntryDecorator()`** — must be first. It wraps each entry in a
   `SaveableStateProvider` so `rememberSaveable {}` inside your screens is keyed per entry and
   survives config changes / process death.
2. **`rememberViewModelStoreNavEntryDecorator()`** (from `androidx.lifecycle:lifecycle-viewmodel-navigation3`)
   — installs a dedicated `ViewModelStoreOwner` per entry. `viewModel()` inside that entry now resolves
   to a store that lives exactly as long as the entry is on the back stack, and is **`clear()`-ed when
   the entry is popped** (your `onCleared()` runs).

> Naming note for the talk: earlier Nav3 alphas split this into `rememberSceneSetupNavEntryDecorator` /
> `rememberSavedStateNavEntryDecorator`. On stable (1.x) the two above are the canonical decorators. If
> your slides have the old names, this is the rename to mention.

## Why it matters
- **Correct lifecycle = correct memory & state.** The detail VM (and its `viewModelScope` coroutines,
  flows, observers) is torn down the moment the user backs out of that screen — not when the Activity
  finally dies.
- **Per-instance state.** Open *apple*, then "open another product" → *banana*. Each `ProductDetail`
  entry gets its **own** VM instance (watch the `VM #n` label and the `Nav3VMScope` logcat tag).
  Their `clicks` counters are independent.
- **Survives rotation.** Increment a few times, rotate the device — the count stays, because the VM
  outlives configuration changes while the entry is alive.

## What breaks if you DON'T add `rememberViewModelStoreNavEntryDecorator`
Without it, `viewModel()` falls back to the nearest `ViewModelStoreOwner` — the **Activity**. Two failures:

1. **State bleed.** `viewModel()` keyed only by type returns the *same* `ProductDetailViewModel` for
   *apple* and *banana*. Open apple (clicks = 5), open banana → it still shows 5, and writes collide.
   You'd have to invent a manual keying scheme to work around it.
2. **Leaks / stale work.** The VM is never cleared when you leave the screen; it (and any running
   `viewModelScope` jobs, collectors, cached data) lives until the Activity is destroyed. On a deep or
   looping back stack that's a growing pile of zombie ViewModels.

To demo it live: comment out the `rememberViewModelStoreNavEntryDecorator()` line, rerun, and bounce
between apple and banana — the counter and `VM #n` label stop being per-product.

## 🎤 Speaker cues
- Open logcat filtered to `Nav3VMScope`. Push apple → "created VM #1". "Open another" → banana → "created VM #2".
  Press back → "cleared VM #2". *"Lifecycle is automatic and tied to the back stack."*
- Increment on apple, **rotate** → count survives (config change) but the VM isn't leaked across screens.
- The reveal: delete the one decorator line, rerun, show the counter bleeding between products. Put it back.
- Tie to DI: *"Dagger builds the object; Navigation 3 owns when it lives and dies. Those are different jobs."*
