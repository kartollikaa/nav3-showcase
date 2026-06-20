# flow2/01-nav2-baseline — The "before": Fragments + Navigation 2 (with Compose UI)

This is the app we're migrating. Every screen is **Compose**, but it's hosted in **Fragments** and
wired with **Navigation 2** — the most common setup in production today. We'll peel it toward
Navigation 3 over the next three branches.

## What's here
- `:app` — a `FragmentActivity` whose only layout is a `NavHostFragment` (`nav_main.xml`). The
  Navigation 2 `NavController` owns the whole back stack.
- `:feature:home` — `HomeFragment` (Compose via `ComposeView`), plus two overlay destinations:
  - `PromoBottomSheetFragment` — a Material `BottomSheetDialogFragment` used as a `<dialog>` dest.
  - `ConfirmDialogFragment` — a `DialogFragment` dest.
- `:feature:auth` — its **own nested nav graph** `nav_auth.xml`: `phone → sms → name`, each a Fragment.
- Cross-module navigation uses **shared destination resource IDs** (`dest_auth`, `dest_promo`,
  `dest_confirm`) declared in `:core:designsystem` — a module every feature depends on. So `HomeFragment`
  can `findNavController().navigate(R.id.dest_…)` to destinations owned by `:app` and `:feature:auth`.
  (A destination's id is otherwise generated in the module that owns its graph XML and invisible to siblings.)

```
NavHostFragment (nav_main)
├── homeFragment
├── include(nav_auth)            ← feature-owned nested graph, @id/dest_auth
│     └── phone → sms → name
├── promoSheet   (BottomSheetDialogFragment, @id/dest_promo)
└── confirmDialog(DialogFragment,            @id/dest_confirm)
```

## The pain points to point at during the talk (this is the motivation for Nav3)
1. **Ceremony per screen.** Every destination is a `Fragment` + an `onCreateView` that news up a
   `ComposeView`, sets a `ViewCompositionStrategy`, and re-hosts the theme. Compose, wrapped in a View,
   wrapped in a Fragment, wrapped in the FragmentManager.
2. **The back stack is hidden.** It lives inside the `NavController`/`FragmentManager`. You can't
   inspect it as data or unit-test "what's on the stack".
3. **Stringly-typed graph + IDs.** `R.id.action_phone_to_sms`, `@id/...`, XML graphs. Typos compile,
   then crash at runtime. And because a destination's id is invisible outside its module, multi-module
   navigation forces you to hoist shared `<item type="id">` constants into a common module just to call
   `navigate(R.id.…)` across the boundary (or fall back to magic deep-link URI strings).
4. **Shared state across a flow is awkward.** Passing the phone number to the SMS step needs Safe Args,
   a bundle, or a graph-scoped ViewModel. (We didn't even bother here — each step keeps local state.)
5. **Two UI worlds.** Fragment lifecycle *and* composition lifecycle, bridged by `ComposeView`. Two
   sets of rules for one screen.

## Why we DON'T migrate all at once
The official guide assumes a single atomic migration and explicitly does **not** support running Nav2
and Nav3 side-by-side. But a real app can't stop for a big-bang rewrite. So the next branches do a
**pragmatic, incremental** migration: convert the leaf-most, lowest-risk pieces first, keeping the
Nav2 `NavController` as the outer shell, and converge on Nav3 from the inside out.

## 🎤 Speaker cues
- Walk the structure: "Compose screens, but the navigation is pure Nav2 + Fragments. Sound familiar?"
- Open `HomeFragment.kt`: "Count the lines before any actual UI. This boilerplate is on *every* screen."
- Open the auth flow on device: phone → sms → name → finish. "Three fragments, an XML graph, and the
  data doesn't even flow between steps yet."
- Open the promo sheet and the confirm dialog: "Overlays are separate Fragment subclasses + `<dialog>` entries."
- End with: "Next branch — we replace exactly ONE of these with Nav3, and nothing else changes."
