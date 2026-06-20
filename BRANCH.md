# flow2/03-nav3-auth-flow — Migrate the auth flow to Navigation 3

Second feature migrated. The **auth flow** (`phone → sms → name`) — previously three Fragments wired
by an XML nested graph with **no data flowing between steps** — is now a single `AuthFragment` hosting
a Nav3 `NavDisplay` over three typed keys. The **Catalog feature is already on Nav3** (previous
branch). The **host (home) and the root confirm dialog are still Navigation 2** — the host migrates last.

## What changed vs flow2/02
- `:feature:auth`: `PhoneFragment` / `SmsFragment` / `NameFragment` + the `phone→sms→name` XML actions
  collapse into:
  - `AuthFragment` — one host Fragment with a Nav3 `NavDisplay` (`AuthNavigation`).
  - Typed keys carrying data forward: `PhoneKey → SmsKey(phone) → NameKey(phone, code)`.
  - `AuthScreens` now pass their value to the next step (`onNext: (String) -> Unit`) instead of the
    Nav2 version's no-arg callbacks.
  - `nav_auth.xml` shrinks to a single `authFragment` (still `@id/dest_auth`).
- Deps: `:feature:auth` gains `navigation3-runtime/ui`, `kotlinx-serialization-core` + the
  serialization plugin; keeps `navigation-fragment-ktx` only to **exit** the feature.
- **Untouched:** the catalog (already Nav3), home, the root confirm dialog.

## The win this branch shows: typed data flow
Nav2 here never passed the phone number forward — each step kept local state (a documented pain point
in flow2/01). Nav3 makes it the type system's job:
```kotlin
entry<PhoneKey> { PhoneScreen(onNext = { phone -> backStack.add(SmsKey(phone)) }) }
entry<SmsKey>   { key -> SmsScreen(phone = key.phone, onNext = { code -> backStack.add(NameKey(key.phone, code)) }) }
entry<NameKey>  { key -> NameScreen(onFinish = { name -> onAuthComplete(key.phone, name) }) }
```
No Safe Args, no Bundle, no graph-scoped ViewModel — just typed constructor arguments.

## The shape
```
NavHostFragment (nav_main)  — Navigation 2 host
├── homeFragment                                                                  (still Nav2)
├── include(nav_catalog)  @id/dest_catalog → CatalogFragment → Nav3 NavDisplay   (Nav3, branch 02)
├── include(nav_auth)     @id/dest_auth   → AuthFragment    → Nav3 NavDisplay    (Nav3, THIS branch)
│        PhoneKey → SmsKey(phone) → NameKey(phone, code)
└── confirmDialog         @id/dest_confirm                                        (still Nav2, in root)
```

## Back behaviour across the seam
`NavDisplay` consumes system back while the auth stack has more than one entry (name → sms → phone).
On the first step it stops consuming, so back falls through to the outer Nav2 `NavController`, which
exits the feature to Home. `AuthFragment` bridges out on completion with `findNavController().popBackStack()`.

## 🎤 Speaker cues
- `git diff flow2/02 flow2/03 --stat` — "second feature; the host is still untouched."
- Open `AuthNavigation.kt`: "Three Fragments and an XML graph became three typed keys on one back
  stack — and now the phone number actually flows to the SMS step, for free."
- On device: home → auth → phone → sms (note "Code sent to …") → name → finish → back at home.
- "Two features on Nav3 now, both as islands inside the Nav2 host. Next branch: we migrate the host
  itself and collapse the islands into one root NavDisplay."
