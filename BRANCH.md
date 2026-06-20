# flow2/02-nav3-catalog — Migrate the first feature to Navigation 3

We start the migration where it's safest: **one leaf feature**. The whole **Catalog** feature — its
internal `list → product` navigation AND its **filter bottom sheet** — now runs on a **local Nav3
`NavDisplay`** inside a single `CatalogFragment`. Everything else (home, auth, the confirm dialog) is
still Navigation 2. The two frameworks coexist by isolation: Nav3 lives entirely inside one Fragment
that the outer Nav2 `NavController` still hosts.

## What changed vs flow2/01
- `:feature:catalog` went from **three Fragments + an XML graph** (`CatalogListFragment`,
  `ProductFragment`, `FilterBottomSheetFragment`, with actions/args/`<dialog>`) to:
  - Typed keys: `CatalogList`, `ProductDetail(id)`, `FilterKey` (`NavKey`, `@Serializable`).
  - `CatalogFragment` — one host Fragment whose content is a Nav3 `NavDisplay` (`CatalogNavigation`).
  - The filter is a Nav3 destination rendered by the copied-in `BottomSheetSceneStrategy` recipe
    (`entry<FilterKey>(metadata = BottomSheetSceneStrategy.bottomSheet())`), not a `BottomSheetDialogFragment`.
  - `nav_catalog.xml` shrank to a single `catalogFragment` destination (still `@id/dest_catalog`).
- Deps: catalog dropped `google-android-material`, `navigation-fragment-ktx` and `core-ktx`, and gained
  `navigation3-runtime/ui`, `kotlinx-serialization-core`, `lifecycle-runtime-compose` + the
  serialization plugin.
- **Untouched:** home, auth (nested Nav2 graph), the root confirm dialog. Home still navigates to the
  catalog by the same `R.id.dest_catalog`.

## The shape
```
NavHostFragment (nav_main)  — Navigation 2
├── homeFragment
├── include(nav_catalog)  @id/dest_catalog → catalogFragment
│      └── CatalogFragment hosts a Nav3 NavDisplay:
│             CatalogList → ProductDetail(id)         (owned back stack: add / removeLastOrNull)
│             FilterKey   → BottomSheetSceneStrategy   (overlay scene)
├── include(nav_auth)     @id/dest_auth   → phone → sms → name   (still Nav2)
└── confirmDialog         @id/dest_confirm                       (still Nav2, still in root)
```

## Why this is the right first step
- **Lowest risk, highest clarity.** A leaf feature has no incoming dependencies; converting it can't
  break anyone else. The outer Nav2 graph doesn't even know its contents changed.
- **The win is immediate.** Inside the island the back stack is a list you own, the product id is a
  typed `ProductDetail(id)` (no Bundle), and the bottom sheet is a destination, not a Fragment subclass.
- **The seam is one Fragment.** `CatalogFragment` is the entire Nav2↔Nav3 boundary. Because the catalog
  is a leaf, it doesn't even need to bridge back out — contrast the migration branches that follow.

## 🎤 Speaker cues
- `git diff flow2/01 flow2/02 --stat` — "the whole change is one feature; nothing else moved."
- Open `CatalogNavigation.kt`: "list → detail is `backStack.add(...)`; the filter is just another
  destination with a bottom-sheet scene. No XML, no Bundle, no BottomSheetDialogFragment."
- On device: home → Catalog → product → back, and open the filter sheet. "All Nav3, inside one Fragment."
- Then home → auth and home → confirm: "still Nav2 — we migrated exactly one feature."
- Tease next: "Next we convert the auth flow; the host stays Nav2 the longest, on purpose."
