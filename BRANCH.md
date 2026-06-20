# flow1/01-basics — Navigation 3 in its simplest form

**Goal:** show the whole Nav3 API with nothing else around it. No scenes, no DI, no nested graphs,
no ViewModels. Just an owned back stack and one `NavDisplay`.

## What changed vs `main`
- New feature module `:feature:catalog` with two typed keys and two dumb screens.
- `MainActivity` gained ~15 lines of navigation. That's all of it.

## Core idea: the back stack is a list you own

```kotlin
val backStack = rememberNavBackStack(ProductList)   // a saveable observable List<NavKey>

NavDisplay(
    backStack = backStack,
    onBack = { backStack.removeLastOrNull() },        // back  = remove last
    entryProvider = entryProvider {
        entry<ProductList> {
            ProductListScreen(onProductClick = { id -> backStack.add(ProductDetail(id)) }) // forward = add
        }
        entry<ProductDetail> { key -> ProductDetailScreen(id = key.id, onBack = { backStack.removeLastOrNull() }) }
    },
)
```

Three concepts, and you've seen all of them:
1. **Keys** — typed objects implementing `NavKey` (`ProductList`, `ProductDetail(id)`). They are the routes.
2. **Back stack** — a `List` you mutate. `add` = navigate, `removeLastOrNull` = back.
3. **`NavDisplay` + `entryProvider`** — observes the stack, renders the top key's content.

## Why it matters
- **You can see and test your navigation state.** It's a list. `println(backStack)`, assert on it in a unit test, time-travel it. In Nav2 the back stack lived inside a `NavController` you couldn't easily inspect.
- **Type-safe arguments for free.** `ProductDetail(id)` is constructed by the compiler. There is no `"product/{id}"` template to typo and no `bundle.getString("id")!!` to crash on.
- **Screens don't depend on navigation.** They take callbacks. The whole feature module doesn't even depend on `navigation3-ui` — only on `navigation3-runtime` for the `NavKey` marker.

## What breaks if you do it the old way (Nav2)
- Route strings → runtime crashes when a route or argument name is mistyped; refactors don't catch call sites.
- Arguments serialized into the route string → you hand-roll parsing, and complex args don't fit.
- The back stack is framework-owned → harder to do "remove the 2 screens under me", conditional flows, or restore arbitrary stacks.

## 🎤 Speaker cues
- Open `MainActivity.kt` and say: *"This is the whole thing. The back stack is a list. Forward is `add`, back is `removeLastOrNull`."*
- Live edit: add `@Serializable data object Cart : NavKey`, an `entry<Cart> { ... }`, and a button that does `backStack.add(Cart)`. ~20 seconds, no graph file, no nav-args plugin. That cheapness is the point.
- Ask the room: *"Who has shipped a crash from a typo'd deep-link route or a missing bundle arg?"* → segue to typed keys.

## Try it
Run the app, tap a product, press system back. Watch that "back" is literally a list mutation.
