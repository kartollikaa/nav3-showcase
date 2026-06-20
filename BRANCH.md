# flow2/05-navgraph-plugin — Draw the whole flow from annotations

Same app as `flow2/04-unified` at runtime — **nothing about navigation changed**. This branch adds
[skydoves/compose-nav-graph](https://github.com/skydoves/compose-nav-graph): a Gradle plugin + KSP
processor + Android Studio plugin that reads a few annotations and renders your **entire app flow as
an interactive map** — every destination a node (with its rendered `@Preview` as a thumbnail), every
transition a labelled arrow, every typed argument shown on the node.

It is a **documentation/visualization** tool, not a navigation library: it generates no runtime code;
a "route" is any class (it doesn't need to implement `NavKey`). KSP extracts the graph to
`nav-graph.json`, the Gradle plugin renders thumbnails, and the IDE plugin draws the canvas.

## What changed vs `flow2/04-unified`
- **Build:** added KSP (`2.2.21-2.0.5`) and the navgraph plugin (`0.1.2`) to the version catalog, and
  applied both to `:feature:home`, `:feature:catalog`, `:feature:auth`, and `:app`. The plugin auto-adds
  the annotations + KSP processor. `ui-tooling` is promoted to a full `implementation` in the feature
  modules (the device-free renderer needs `ComposeViewAdapter`).
- **Annotations only** on existing code — no navigation logic touched.

## The four annotations
```kotlin
// 1) The keys ARE the graph — annotate the route classes with structure.
@NavGraphRoot                                          // the start destination
@NavEdge(to = ConfirmKey::class, label = "Confirm order")
@Serializable data object HomeKey : NavKey

// 2) Link each route to the composable that renders it (the node's click target).
@NavDestination(route = CatalogList::class)
@Composable fun ProductListScreen(/* … */) { /* … */ }

// 3) Link a @Preview to a route so its rendered image becomes the node's thumbnail.
@NavPreview(route = CatalogList::class, primary = true)
@Preview @Composable private fun ProductListPreview() = Nav3ShowcaseTheme { ProductListScreen(/* stubs */) }
```

## The map it draws — 8 nodes, 8 edges
- **Start:** `HomeKey`.
- **Intra-feature edges** on the route classes (same module): `HomeKey → ConfirmKey`;
  `CatalogList → ProductDetail`, `CatalogList → FilterKey`; `PhoneKey → SmsKey`, `SmsKey → NameKey`.
- **Cross-feature edges** in `:app` (`RootNavigation`, explicit `from`/`to`): `HomeKey → CatalogList`,
  `HomeKey → PhoneKey`, `NameKey → HomeKey` — `:app` is the only module that sees every feature's keys.
- **Typed-argument arrows:** `ProductDetail.id`, `SmsKey.phone`, `NameKey.phone`/`code` (serializable
  properties become args automatically — the type IS the contract).
- **Thumbnails:** all 8 nodes link a no-arg `@Preview` via `@NavPreview`.

## Requirements / gotcha
- The graph (`nav-graph.json`, KSP) builds on the project's normal **JDK 17** toolchain; `assembleDebug`
  is unchanged and green.
- **Thumbnail rendering needs JDK 21.** The navgraph layoutlib renderer ships compiled for Java 21
  (`class file version 65.0`); on JDK 17 the render step fails and you get the graph with no thumbnails.
  Run Android Studio / Gradle on a JDK 21 to render them. No AGP / Kotlin / Gradle / compileSdk change.

## 🎤 Speaker cues
- `git diff flow2/04 flow2/05 --stat` — "no navigation code changed; it's all annotations + plugin wiring."
- Open `HomeKeys.kt` / `CatalogKeys.kt`: "The keys *are* the graph. `@NavGraphRoot` + `@NavEdge` and the map draws itself."
- `./gradlew :app:generateNavGraph` (on JDK 21) → open the **NavGraph** tool window: the live map with
  thumbnails, typed-argument arrows, and the cross-feature `Open Catalog` / `Start auth` / `Finish` edges.
- The honest caveat: "this doesn't change how you navigate; it makes the navigation you already have
  *visible and reviewable*."

## Try it
`./gradlew :app:generateNavGraph` then open the project in Android Studio (JDK 21) and the **NavGraph
Graph** tool window. Or inspect `app/build/navgraph-aggregated/nav-graph.json` directly.
