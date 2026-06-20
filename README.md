# Navigation 3 Showcase

A teaching repository for an internal mobile-engineering talk on **Jetpack Navigation 3 (Nav3)**.
Everything is **Jetpack Compose**. The repo is organized as a set of git branches that build on
each other, so you can `git checkout` a branch live during the talk and `git diff` against the
previous one to show *exactly* what changed.

> Verified against the official docs (developer.android.com/guide/navigation/navigation-3) as of
> **June 2026**. Nav3 core is stable at **1.1.2**. See [`gradle/libs.versions.toml`](gradle/libs.versions.toml).

---

## The one-paragraph mental model

In Navigation 2 the framework **owned** your back stack; you navigated by route strings and asked a
`NavController` to mutate hidden state. In **Navigation 3 you own the back stack** — it's a plain
observable `List` of type-safe key objects. Navigating forward is `backStack.add(key)`; going back
is `backStack.removeLastOrNull()`. A single composable, **`NavDisplay`**, observes that list and
renders the top entry. Everything else (bottom sheets, multipane, dialogs, ViewModel scoping,
animations) is layered on as *strategies* and *decorators* — opt-in, composable, and inspectable.

---

## Branch map

Two independent lineages, both branched from `main`. Within a lineage each branch builds on the one
before it.

### Flow 1 — Intro (complexity rising)

| Branch | What it adds |
| --- | --- |
| `flow1/01-basics` | The simplest possible Nav3: an owned back stack + one `NavDisplay`, type-safe keys, no scenes/DI. |
| `flow1/02-scenes` | Scenes: a **bottom sheet** overlay strategy and a **list-detail multipane** scene that adapts to width. |
| `flow1/03-viewmodel-decorator` | `rememberViewModelStoreNavEntryDecorator` — per-destination ViewModel scoping, and what breaks without it. |

### Flow 2 — Migration (Fragments + Nav2 → Nav3, incrementally)

| Branch | What it adds |
| --- | --- |
| `flow2/01-nav2-baseline` | A realistic multi-module app on **Fragments + Navigation 2**, but every screen is Compose. Nested auth flow, a bottom sheet, a dialog. |
| `flow2/02-nav3-bottomsheet` | The smallest first step: replace **one** Nav2 bottom-sheet destination with a local `NavDisplay` + `BottomSheetSceneStrategy`. |
| `flow2/03-nav3-auth-flow` | Replace a feature's **per-fragment nested navigation** (phone → SMS → name) with a single in-feature `NavDisplay` back stack. |
| `flow2/04-unified` | Join the per-feature NavDisplays toward a **single root NavDisplay** — and a written answer to *"is more than one NavDisplay OK, and what for?"* |

Each branch has its own `README.md` with: the concept, the key code, **why it matters**, **what
breaks if you do it the old way**, and 🎤 **speaker cues**.

---

## Suggested talk running order

1. `main` — 30 sec: "the back stack is a list you own." Set the frame.
2. `flow1/01-basics` — the whole API in ~15 lines. Live-add a destination to show how cheap it is.
3. `flow1/02-scenes` — resize the window to show multipane kick in; pop a bottom sheet.
4. `flow1/03-viewmodel-decorator` — the subtle one. Show a ViewModel leaking, then fix it with one line.
5. *Breather / Q&A.*
6. `flow2/01-nav2-baseline` — "here's the app we actually have." Point at the Fragment + nested-graph pain.
7. `flow2/02` → `03` → `04` — walk the incremental migration. End on the multiple-NavDisplay guidance.

---

## Building

Open the project root in **Android Studio** (Narwhal or newer — needs AGP 8.11 / Gradle 8.14 / a JDK 17
toolchain, `compileSdk = 36`). Android Studio will create the Gradle wrapper jar on first sync; if you
prefer the CLI, run `gradle wrapper` once in the project root, then `./gradlew :app:assembleDebug`.

> This repo is checked in **without** the binary `gradle-wrapper.jar` (text-only history for clean
> diffs during the talk). `gradle/wrapper/gradle-wrapper.properties` pins the Gradle version.

## Module layout (grows per branch)

```
app/                      ← Activity host + root navigation
core/designsystem/        ← theme + the trivial colored Content() screens (so nothing distracts from nav)
core/navigation/          ← shared NavKey types (introduced in the migration flow)
feature/<name>/           ← one module per feature (catalog, auth, home, …)
```
