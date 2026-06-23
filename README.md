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
| `flow1/04-animations` | Per-screen **and** per-entry transitions: one default `transitionSpec` for the whole graph, overridden for a single entry via metadata (predictive-back included). |

### Flow 2 — Migration (Fragments + Nav2 → Nav3, incrementally)

| Branch | What it adds |
| --- | --- |
| `flow2/01-nav2-baseline` | A realistic multi-module app on **Fragments + Navigation 2**, but every screen is Compose. Nested auth flow, a bottom sheet, a dialog. |
| `flow2/02-nav3-catalog` | Migrate the **first whole feature**: the Catalog (list → product **and** its filter bottom sheet) runs on a local `NavDisplay` inside one Fragment; everything else stays Nav2. |
| `flow2/03-nav3-auth-flow` | Replace a feature's **per-fragment nested navigation** (phone → SMS → name) with a single in-feature `NavDisplay` back stack. |
| `flow2/04-unified` | The destination: **one root `NavDisplay`** on a single back stack in a plain `ComponentActivity` — Fragments, the FragmentManager, and every XML graph deleted. |
| `flow2/05-navgraph-plugin` | Same app at runtime; add [skydoves/compose-nav-graph](https://github.com/skydoves/compose-nav-graph) to render the whole flow as an interactive map from a few annotations (docs/visualization, no runtime change). |

Each branch has its own `BRANCH.md` with: the concept, the key code, **why it matters**, **what
breaks if you do it the old way**, and 🎤 **speaker cues**. (`README.md` is this shared overview,
carried unchanged on every branch.)

---

## Suggested talk running order

1. `main` — 30 sec: "the back stack is a list you own." Set the frame.
2. `flow1/01-basics` — the whole API in ~15 lines. Live-add a destination to show how cheap it is.
3. `flow1/02-scenes` — resize the window to show multipane kick in; pop a bottom sheet.
4. `flow1/03-viewmodel-decorator` — the subtle one. Show a ViewModel leaking, then fix it with one line.
5. `flow1/04-animations` — tap a product to **zoom** (per-entry override), open the filter to **slide** (scene): default vs. per-destination motion, one screen.
6. *Breather / Q&A.*
7. `flow2/01-nav2-baseline` — "here's the app we actually have." Point at the Fragment + nested-graph pain.
8. `flow2/02` → `03` → `04` — walk the incremental migration (first feature → auth flow → unified root). End on the single root `NavDisplay`.
9. `flow2/05-navgraph-plugin` — optional closer: generate the annotated flow map so the navigation you just built becomes visible and reviewable.

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
