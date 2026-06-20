# flow1/04-animations — Different transitions per screen and per entry

**Goal:** control how `NavDisplay` animates between destinations — set one default transition for the
whole graph, then **override it for a single entry** so different screens animate differently.

## What changed vs `flow1/03-viewmodel-decorator`
- `NavDisplay` gained three transition parameters: `transitionSpec` (push), `popTransitionSpec` (back),
  and `predictivePopTransitionSpec` (the swipe-back gesture). They are the **default** for every navigation.
- The `ProductDetail` entry **overrides** those defaults via metadata — it zooms in (scale + fade)
  while every other screen slides.

## The two levels

```kotlin
NavDisplay(
    // 1) DEFAULT for the whole graph. The receiver is an AnimatedContentTransitionScope, so
    //    slideIntoContainer / slideOutOfContainer are available for free.
    transitionSpec = {                                   // push (forward)
        (slideIntoContainer(SlideDirection.Start, tween(350)) + fadeIn(tween(350))) togetherWith
            (slideOutOfContainer(SlideDirection.Start, tween(350)) + fadeOut(tween(350)))
    },
    popTransitionSpec = { /* mirror image: slide towards End */ },
    predictivePopTransitionSpec = { /* reuse the pop animation for the swipe gesture */ },
    entryProvider = entryProvider {
        entry<ProductDetail>(
            // 2) PER-ENTRY override — wins over the default for THIS destination only.
            metadata = ListDetailSceneStrategy.detailPane() +
                NavDisplay.transitionSpec { scaleIn(0.85f) + fadeIn() togetherWith fadeOut() } +
                NavDisplay.popTransitionSpec { fadeIn() togetherWith (scaleOut(0.85f) + fadeOut()) },
        ) { /* … */ }
    },
)
```

A transition is just a `ContentTransform` — an `EnterTransition togetherWith` an `ExitTransition`,
exactly like `AnimatedContent`. Nothing Nav3-specific about the motion itself; Nav3 only decides
**which** `ContentTransform` to use for a given navigation.

## Precedence (who wins)
```
transitioning NavEntry.metadata  >  current Scene.metadata  >  NavDisplay defaults
```
`ProductDetail`'s per-entry spec overrides the display default; any entry without an override falls
back to the default. (A `Scene` can also override entry metadata — see the nuance below.)

## Why it matters
- **One place for app-wide motion.** Set the house style once on `NavDisplay`; every screen inherits it.
- **Per-destination expression.** A detail screen, a dialog, an onboarding step can each animate in a
  way that fits — no global `AnimatedContent` plumbing, just metadata on the entry.
- **Predictive back is first-class.** `predictivePopTransitionSpec` drives the Android 14+ swipe-back
  preview and receives the swipe edge, so you can animate directionally.

## Scenes bring their own animation (the nuance)
This branch still has the adaptive **list/detail** and **bottom-sheet** scenes from the earlier branches.
`transitionSpec` animates **scene changes** — e.g. list → detail when the window is narrow (single pane).
In two-pane mode list and detail share the *same* scene, so moving between them updates in place rather
than running a transition. The bottom-sheet filter is an `OverlayScene`: its slide-up is the scene's own
animation, not `transitionSpec`. Per-entry specs are honored by the single-pane scene.

## 🎤 Speaker cues
- On a phone (single pane): tap a product → it **zooms** in (the per-entry override); press back → it
  zooms out. Open the filter → it **slides up** (scene animation). Three different motions, one screen.
- Point at `transitionSpec`: "This is the default for the whole app — one slide + fade." Then at
  `ProductDetail`'s metadata: "This one block makes just this destination zoom instead."
- Recite the precedence line: "entry beats scene beats the NavDisplay default."
- Mention `predictivePopTransitionSpec`: "the same hook drives the Android swipe-back preview."
