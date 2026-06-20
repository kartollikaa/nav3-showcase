# flow1/02-scenes — Bottom sheets & multipane, without changing the back stack

**Goal:** add real layout complexity (an overlay bottom sheet + an adaptive two-pane list/detail)
and show that it's all done by **scene strategies**, not by changing how you navigate.

## What changed vs `flow1/01-basics`
- Added a copied-in **`BottomSheetSceneStrategy`** recipe (`app/.../scene/BottomSheetSceneStrategy.kt`).
- Added a `Filter` destination rendered as a bottom sheet via metadata.
- Wrapped list/detail with Material's adaptive **`ListDetailSceneStrategy`** so they go side-by-side on wide screens.
- `NavDisplay` now takes `sceneStrategies = listOf(...)`. The back stack code is otherwise identical.

## The mental model: Scenes decide *layout*, the back stack stays the same

```kotlin
val listDetailStrategy = rememberListDetailSceneStrategy<NavKey>()
val bottomSheetStrategy = remember { BottomSheetSceneStrategy<NavKey>() }

NavDisplay(
    backStack = backStack,
    onBack = { backStack.removeLastOrNull() },
    sceneStrategies = listOf(bottomSheetStrategy, listDetailStrategy), // tried in order; first match wins
    entryProvider = entryProvider {
        entry<ProductList>(metadata = ListDetailSceneStrategy.listPane(detailPlaceholder = { … })) { … }
        entry<ProductDetail>(metadata = ListDetailSceneStrategy.detailPane()) { … }
        entry<Filter>(metadata = BottomSheetSceneStrategy.bottomSheet()) { … }   // overlay
    },
)
```

Key points:
- A **`SceneStrategy`** inspects the back stack and returns a `Scene` (a way to render one or more entries together). `NavDisplay` tries your strategies in order and falls back to single-pane if none apply.
- An entry **opts in** to a scene through `metadata` (`listPane()`, `detailPane()`, `bottomSheet()`). The screen composable doesn't know or care.
- **Order matters.** Overlay strategies (bottom sheet, dialog) must come first so they draw on top of the layout beneath them.
- **Bottom sheets are a recipe, not core.** There is no `androidx` `BottomSheetSceneStrategy`; you copy the official one in. (Dialogs *are* core: `DialogSceneStrategy`.)

## Why it matters
- **Adaptive UI is a navigation concern handled declaratively.** The same back stack (`[ProductList, ProductDetail]`) renders as two stacked screens on a phone and as two panes on a tablet/foldable — no separate navigation graphs, no `if (isTablet)` branching in your screens.
- **A bottom sheet is a real back-stack entry.** System back / predictive back dismiss it for free, and it survives config changes, because it's just another `NavEntry` — not an ad-hoc `ModalBottomSheet` boolean state hanging off a screen.

## What breaks if you do it the old way
- Nav2 had no first-class multipane; you hand-rolled `if (twoPane)` layouts and juggled two `NavHost`s or a fragment + detail container, keeping their state in sync manually.
- Bottom sheets were typically local `var showSheet by remember { mutableStateOf(false) }` — invisible to the back stack, easy to get wrong with predictive back, and not restored after process death.

## 🎤 Speaker cues
- Run on a phone: tap a product (it pushes a detail screen). Now **rotate to landscape / run on a tablet / resize a foldable** — the *same* navigation state snaps into two panes. "I changed zero lines of navigation to get this."
- Open the filter: show that **system back dismisses the sheet** because it's a back-stack entry.
- Point at `sceneStrategies = listOf(bottomSheetStrategy, listDetailStrategy)` and explain the order rule (overlays first).
- Note for the team: "Bottom sheet = copy the recipe; dialog = `DialogSceneStrategy` is built in."
