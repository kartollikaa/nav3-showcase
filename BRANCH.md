# flow2/05-navgraph-plugin — Отрисовка всего флоу по аннотациям

Рантайм приложения идентичен `flow2/04-unified` — **навигация не изменилась ни на байт**. В этой ветке добавлен
[skydoves/compose-nav-graph](https://github.com/skydoves/compose-nav-graph): Gradle-плагин + KSP-процессор
+ плагин для Android Studio, которые читают несколько аннотаций и отрисовывают **весь флоу приложения как
интерактивную карту** — каждый пункт назначения это узел (с отрисованным `@Preview` в виде миниатюры),
каждый переход — подписанная стрелка, каждый типизированный аргумент — выноска на узле.

Это **инструмент документирования/визуализации**, а не навигационная библиотека: он не генерирует никакого кода
во время выполнения; «маршрут» — это любой класс (реализовывать `NavKey` не обязательно). KSP извлекает граф в
`nav-graph.json`, Gradle-плагин отрисовывает миниатюры, а IDE-плагин рисует холст.

## Что изменилось по сравнению с `flow2/04-unified`
- **Сборка:** в каталог версий добавлены KSP (`2.2.21-2.0.5`) и navgraph-плагин (`0.1.2`),
  оба подключены к `:feature:home`, `:feature:catalog`, `:feature:auth` и `:app`. Плагин автоматически
  добавляет аннотации + KSP-процессор. `ui-tooling` повышен до полной `implementation` в фича-модулях
  (бездевайсному рендереру нужен `ComposeViewAdapter`).
- **Только аннотации** на существующем коде — навигационная логика не затронута.

## Четыре аннотации
```kotlin
// 1) Ключи и есть граф — аннотируем классы маршрутов, задавая структуру.
@NavGraphRoot                                          // стартовый пункт назначения
@NavEdge(to = ConfirmKey::class, label = "Confirm order")
@Serializable data object HomeKey : NavKey

// 2) Связываем каждый маршрут с composable-функцией, которая его отрисовывает (цель клика по узлу).
@NavDestination(route = CatalogList::class)
@Composable fun ProductListScreen(/* … */) { /* … */ }

// 3) Привязываем @Preview к маршруту, чтобы его отрисованное изображение стало миниатюрой узла.
@NavPreview(route = CatalogList::class, primary = true)
@Preview @Composable private fun ProductListPreview() = Nav3ShowcaseTheme { ProductListScreen(/* stubs */) }
```

## Карта, которую строит инструмент — 8 узлов, 8 рёбер
- **Старт:** `HomeKey`.
- **Рёбра внутри фичи** на классах маршрутов (один модуль): `HomeKey → ConfirmKey`;
  `CatalogList → ProductDetail`, `CatalogList → FilterKey`; `PhoneKey → SmsKey`, `SmsKey → NameKey`.
- **Межфичевые рёбра** в `:app` (`RootNavigation`, явные `from`/`to`): `HomeKey → CatalogList`,
  `HomeKey → PhoneKey`, `NameKey → HomeKey` — `:app` — единственный модуль, видящий ключи всех фич.
- **Стрелки типизированных аргументов:** `ProductDetail.id`, `SmsKey.phone`, `NameKey.phone`/`code` (сериализуемые
  свойства становятся аргументами автоматически — тип и есть контракт).
- **Миниатюры:** все 8 узлов связаны с безаргументным `@Preview` через `@NavPreview`.

## Требования / подводные камни
- Граф (`nav-graph.json`, KSP) собирается на обычном **JDK 17**-тулчейне проекта; `assembleDebug`
  работает без изменений.
- **Отрисовка миниатюр требует JDK 21.** Рендерер layoutlib из navgraph-плагина скомпилирован под Java 21
  (`class file version 65.0`); на JDK 17 шаг отрисовки падает и граф получается без миниатюр.
  Для их генерации запускайте Android Studio / Gradle на JDK 21. AGP / Kotlin / Gradle / compileSdk не меняются.

## 🎤 Реплики для докладчика
- `git diff flow2/04 flow2/05 --stat` — «навигационный код не изменился; это только аннотации + подключение плагина.»
- Открываем `HomeKeys.kt` / `CatalogKeys.kt`: «Ключи *и есть* граф. `@NavGraphRoot` + `@NavEdge` — и карта рисуется сама.»
- `./gradlew :app:generateNavGraph` (на JDK 21) → открываем инструмент **NavGraph**: живая карта с
  миниатюрами, стрелками типизированных аргументов и межфичевыми рёбрами `Open Catalog` / `Start auth` / `Finish`.
- Честная оговорка: «это не меняет то, как вы навигируете; это делает уже имеющуюся навигацию
  *видимой и поддающейся ревью*.»

## Попробуй сам
`./gradlew :app:generateNavGraph`, затем открой проект в Android Studio (JDK 21) и инструмент **NavGraph
Graph**. Или загляни напрямую в `app/build/navgraph-aggregated/nav-graph.json`.
