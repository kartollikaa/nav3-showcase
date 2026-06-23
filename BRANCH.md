# flow2/04-unified — Один корневой NavDisplay, фрагменты уходят

Финал. Главный экран, каталог (список/детали + фильтр), шаги авторизации и диалог подтверждения теперь
живут в **одном бэкстеке**, который отображает **один корневой `NavDisplay`** в обычной `ComponentActivity`.
Navigation 2, фрагменты, FragmentManager и все XML-графы навигации — в прошлом. **Хост мигрировал
последним** — именно в том порядке, обратном зависимостям, которому следует реальная миграция.

## Что изменилось по сравнению с flow2/03
- **Удалены** все Fragment-хосты (`HomeFragment`, `CatalogFragment`, `AuthFragment`,
  `ConfirmDialogFragment`), все XML-графы (`nav_main`, `nav_catalog`, `nav_auth`), лейаут активити
  и общий `navigation_ids.xml` (мостовые resId для Nav2 больше не нужны).
- `:app` — это `ComponentActivity` с `setContent { … RootNavigation() }` — один `NavDisplay`.
- Каждая фича предоставляет билдер `EntryProviderScope<NavKey>` — `homeEntries`, `catalogEntries`,
  `authEntries` — паттерн **модуляризации** Nav3. Приложение вызывает все три внутри одного `entryProvider { }`.
- **Фильтр каталога** остаётся оверлеем `BottomSheetSceneStrategy`; **диалог подтверждения** становится
  записью `DialogSceneStrategy.dialog()` в корне. Оба — **сцены на одном дисплее**, а не отдельные хосты.
- `:app`, `:feature:home`, `:feature:catalog`, `:feature:auth` избавились от зависимостей на Fragment / Navigation 2 /
  Material Components; тема приложения вернулась к простому `android:Theme.Material.Light.NoActionBar`.

## Вся навигация приложения — в одном месте
```kotlin
val backStack = rememberNavBackStack(HomeKey)   // ОДИН стек для всего приложения

NavDisplay(
    backStack = backStack,
    onBack = { backStack.removeLastOrNull() },
    entryDecorators = listOf(
        rememberSaveableStateHolderNavEntryDecorator(),
        rememberViewModelStoreNavEntryDecorator(),
    ),
    sceneStrategies = listOf(bottomSheetStrategy, DialogSceneStrategy()),
    entryProvider = entryProvider {
        homeEntries(backStack, onOpenCatalog = { backStack.add(CatalogList) }, onOpenAuth = { backStack.add(PhoneKey) })
        catalogEntries(backStack)                                  // список → детали + нижний лист фильтра
        authEntries(backStack, onComplete = { _, _ -> /* выходим из суб-флоу авторизации */ })
    },
)
```

### Паттерн модуляризации
Фича владеет своими ключами и расширением на `EntryProviderScope<NavKey>`. Приложение компонует их внутри
одного `entryProvider { }`. Фича никогда не зависит от другой фичи; переходы между фичами прокидываются
через приложение (единственный модуль, которому видны все ключи) в виде лямбд. В масштабе эти билдеры
можно регистрировать через Dagger multibindings (`@IntoSet`) и обходить через `forEach` — никакого
центрального списка, который нужно поддерживать.

## Миграция от начала до конца (ради этого и задуман флоу)
1. **flow2/01** — всё на Nav2 + фрагментах; листовая фича (Catalog) и корневой диалог на месте.
2. **flow2/02** — мигрируем фичу **Catalog** (её граф + нижний лист) на локальный Nav3-остров.
3. **flow2/03** — мигрируем флоу **авторизации** на Nav3; типизированные потоки данных: телефон → смс → имя.
4. **flow2/04** — мигрируем **хост**: объединяем острова в один корневой `NavDisplay`; диалог подтверждения
   (последний оверлей в корне) становится сценой `DialogSceneStrategy`. От листьев к корню.

## 🎤 Реплики для докладчика
- `git diff flow2/03 flow2/04 --stat` — смотрим, как удаляются фрагменты, лейауты и XML-графы навигации.
- Открываем `MainActivity.kt`: «Это вся навигация приложения. Один стек, две стратегии сцен, три билдера фич.»
- Запускаем всё приложение: главный → Каталог (список → детали, нижний лист фильтра) → назад; главный → авторизация (телефон → смс →
  имя) → завершение; главный → диалог подтверждения. Всё — один бэкстек; системная кнопка «Назад» и предиктивный бэк работают везде.
- «Мы мигрировали сначала листья (каталог, авторизацию), а хост — последним: каждый шаг был небольшим, и внешняя
  оболочка продолжала работать всё это время.»
