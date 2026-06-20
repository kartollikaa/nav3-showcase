# flow2/04-unified — One root NavDisplay, no more Fragments

The destination. Home, the catalog (list/detail + filter), the auth steps, and the confirm dialog now
all live on a **single back stack** rendered by **one root `NavDisplay`** in a plain `ComponentActivity`.
Navigation 2, Fragments, the FragmentManager, and every XML nav graph are gone. The **host migrated
last** — exactly the reverse-dependency order a real migration follows.

## What changed vs flow2/03
- **Deleted** every Fragment host (`HomeFragment`, `CatalogFragment`, `AuthFragment`,
  `ConfirmDialogFragment`), all XML graphs (`nav_main`, `nav_catalog`, `nav_auth`), the activity layout,
  and the shared `navigation_ids.xml` (no Nav2 resIds left to bridge modules).
- `:app` is a `ComponentActivity` with `setContent { … RootNavigation() }` — a single `NavDisplay`.
- Each feature exposes an `EntryProviderScope<NavKey>` builder — `homeEntries`, `catalogEntries`,
  `authEntries` — the Nav3 **modularization** pattern. The app calls all three in one `entryProvider { }`.
- The **catalog filter** stays a `BottomSheetSceneStrategy` overlay; the **confirm dialog** becomes a
  `DialogSceneStrategy.dialog()` entry in root. Both are **scenes on the one display**, not separate hosts.
- `:app`, `:feature:home`, `:feature:catalog`, `:feature:auth` dropped their Fragment / Navigation 2 /
  Material Components dependencies; the app theme is back to a plain `android:Theme.Material.Light.NoActionBar`.

## The whole app's navigation, in one place
```kotlin
val backStack = rememberNavBackStack(HomeKey)   // ONE stack for the entire app

NavDisplay(
    backStack = backStack,
    onBack = { backStack.removeLastOrNull() },
    entryDecorators = listOf(
        rememberSaveableStateHolderNavEntryDecorator(),
        rememberViewModelStoreNavEntryDecorator(),
    ),
    sceneStrategies = listOf(bottomSheetStrategy, DialogSceneStrategy()),
    entryProvider = entryProvider {
        homeEntries(backStack, onOpenCatalog = { backStack.add(CatalogList) }, onOpenAuth = { backStack.add(PhoneKey) })
        catalogEntries(backStack)                                  // list → detail + filter sheet
        authEntries(backStack, onComplete = { _, _ -> /* pop auth sub-flow */ })
    },
)
```

### Modularization pattern
A feature owns its keys and an extension on `EntryProviderScope<NavKey>`. The app composes them inside
one `entryProvider { }`. A feature never depends on another feature; cross-feature jumps are wired in
the app (the only module that sees all keys), passed as lambdas. At scale you'd register these builders
with Dagger multibindings (`@IntoSet`) and `forEach` them — no central list to maintain.

## The migration, end to end (the point of this flow)
1. **flow2/01** — everything Nav2 + Fragments; a leaf feature (Catalog) and a root dialog in place.
2. **flow2/02** — migrate the **Catalog** feature (its nav + bottom sheet) to a local Nav3 island.
3. **flow2/03** — migrate the **auth** flow to Nav3; typed data flows phone → sms → name.
4. **flow2/04** — migrate the **host**: collapse the islands into one root `NavDisplay`; the confirm
   dialog (last overlay in root) becomes a `DialogSceneStrategy` scene. From the leaves to the root.

## 🎤 Speaker cues
- `git diff flow2/03 flow2/04 --stat` — watch fragments, layouts and nav XML get deleted.
- Open `MainActivity.kt`: "This is the whole app's navigation. One stack, two scene strategies, three feature builders."
- Run the full app: home → Catalog (list → detail, filter sheet) → back; home → auth (phone → sms →
  name) → finish; home → confirm dialog. All one back stack; system/predictive back works throughout.
- "We migrated leaves first (catalog, auth) and the host last — so each step was small and the outer
  shell kept working the whole time."
