# flow2/01-nav2-baseline — The "before": Fragments + Navigation 2 (with Compose UI)

This is the app we're migrating. Every screen is **Compose**, but it's hosted in **Fragments** and
wired with **Navigation 2** — the most common setup in production today. Over the next branches we
migrate it to Navigation 3 **from the leaves inward**: the **Catalog feature** first, then **auth**,
and the **host** last. The **confirm dialog stays in root** the whole way.

## What's here
- `:app` — a `FragmentActivity` whose only layout is a `NavHostFragment` (`nav_main.xml`). The
  Navigation 2 `NavController` owns the whole back stack. The **confirm dialog** is a root-level
  `<dialog>` destination here — the one overlay that lives in root until the very end.
- `:feature:catalog` — a self-contained feature with its **own nested graph** `nav_catalog.xml`:
  `catalogList → product` (internal `<action>` + a `Bundle` arg for the id), plus a **filter
  `BottomSheetDialogFragment`** as a `<dialog>` dest. The bottom sheet **belongs to the feature**.
- `:feature:home` — `HomeFragment` (Compose via `ComposeView`); the app's entry screen with buttons
  into Catalog, auth, and the confirm dialog. `ConfirmDialogContent` lives here; its destination is in root.
- `:feature:auth` — its **own nested nav graph** `nav_auth.xml`: `phone → sms → name`, each a Fragment.
- Cross-module navigation uses **shared destination resource IDs** (`dest_catalog`, `dest_auth`,
  `dest_confirm`) declared in `:core:designsystem` — a module every feature depends on. So `HomeFragment`
  can `findNavController().navigate(R.id.dest_…)` across module boundaries. (A destination's id is
  otherwise generated in the module that owns its graph XML and invisible to siblings.) Navigation
  *inside* a feature (catalog list → product → filter) uses that feature's own local R.id.

```
NavHostFragment (nav_main)                          ← :app owns the host + root overlay
├── homeFragment
├── include(nav_catalog)        @id/dest_catalog     ← feature owns its nav AND its bottom sheet
│     ├── catalogList → product (Bundle arg: id)
│     └── filterSheet (BottomSheetDialogFragment)
├── include(nav_auth)           @id/dest_auth        ← feature-owned nested graph
│     └── phone → sms → name
└── confirmDialog (DialogFragment, @id/dest_confirm) ← stays in ROOT until the end
```

## The pain points to point at during the talk (this is the motivation for Nav3)
1. **Ceremony per screen.** Every destination is a `Fragment` + an `onCreateView` that news up a
   `ComposeView`, sets a `ViewCompositionStrategy`, and re-hosts the theme. Compose, wrapped in a View,
   wrapped in a Fragment, wrapped in the FragmentManager.
2. **The back stack is hidden.** It lives inside the `NavController`/`FragmentManager`. You can't
   inspect it as data or unit-test "what's on the stack".
3. **Stringly-typed graph + IDs.** `R.id.action_catalogList_to_product`, `@id/...`, XML graphs. Typos
   compile, then crash at runtime. And because a destination's id is invisible outside its module,
   multi-module navigation forces you to hoist shared `<item type="id">` constants into a common module
   to call `navigate(R.id.…)` across the boundary (or fall back to magic deep-link URI strings).
4. **Shared state across a flow is awkward.** Passing the product id to the detail screen needs a
   `Bundle` (or Safe Args); the auth steps don't even bother and keep local state.
5. **Two UI worlds.** Fragment lifecycle *and* composition lifecycle, bridged by `ComposeView`. Two
   sets of rules for one screen.

## Why we DON'T migrate all at once
The official guide assumes a single atomic migration and explicitly does **not** support running Nav2
and Nav3 side-by-side. But a real app can't stop for a big-bang rewrite. So the next branches do a
**pragmatic, incremental** migration: convert the leaf-most, lowest-risk pieces first (a feature and
its overlay), keep the Nav2 `NavController` as the outer shell, and converge on Nav3 from the inside out.

## 🎤 Speaker cues
- Walk the structure: "Compose screens, but the navigation is pure Nav2 + Fragments. Sound familiar?"
- Open `HomeFragment.kt`: "Count the lines before any actual UI. This boilerplate is on *every* screen."
- Open Catalog on device: list → product (note the `Bundle` arg) → and the filter bottom sheet.
  "This whole feature — its internal nav AND its bottom sheet — is what we migrate FIRST next branch."
- Open the confirm dialog: "This overlay lives in the ROOT graph. It stays Nav2 until the very end."
- End with: "Next branch — we migrate exactly ONE feature (Catalog) to Nav3, and nothing else changes."
