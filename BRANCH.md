# flow2/02-nav3-catalog — Миграция первой фичи на Navigation 3

Начинаем миграцию там, где это безопаснее всего: **один листовой модуль**. Вся фича **Catalog** — внутренняя навигация `list → product` И **нижний лист фильтра** — теперь работает на **локальном `NavDisplay` Nav3** внутри одного `CatalogFragment`. Всё остальное (home, auth, диалог подтверждения) по-прежнему использует Navigation 2. Оба фреймворка сосуществуют за счёт изоляции: Nav3 живёт целиком внутри одного Fragment, который внешний `NavController` Nav2 по-прежнему хостит.

## Что изменилось относительно flow2/01
- `:feature:catalog` перешёл от **трёх Fragment + XML-графа** (`CatalogListFragment`,
  `ProductFragment`, `FilterBottomSheetFragment`, с actions/args/`<dialog>`) к:
  - Типобезопасным ключам: `CatalogList`, `ProductDetail(id)`, `FilterKey` (`NavKey`, `@Serializable`).
  - `CatalogFragment` — один хост-Fragment, содержимое которого — `NavDisplay` Nav3 (`CatalogNavigation`).
  - Фильтр — это пункт назначения Nav3, отрисованный с помощью встроенного рецепта `BottomSheetSceneStrategy`
    (`entry<FilterKey>(metadata = BottomSheetSceneStrategy.bottomSheet())`), а не `BottomSheetDialogFragment`.
  - `nav_catalog.xml` сократился до единственного пункта назначения `catalogFragment` (по-прежнему `@id/dest_catalog`).
- Зависимости: catalog убрал `google-android-material`, `navigation-fragment-ktx` и `core-ktx`, добавил
  `navigation3-runtime/ui`, `kotlinx-serialization-core`, `lifecycle-runtime-compose` + плагин сериализации.
- **Не тронуто:** home, auth (вложенный граф Nav2), корневой диалог подтверждения. Home по-прежнему переходит в
  catalog по тому же `R.id.dest_catalog`.

## Структура
```
NavHostFragment (nav_main)  — Navigation 2
├── homeFragment
├── include(nav_catalog)  @id/dest_catalog → catalogFragment
│      └── CatalogFragment хостит NavDisplay Nav3:
│             CatalogList → ProductDetail(id)         (собственный бэкстек: add / removeLastOrNull)
│             FilterKey   → BottomSheetSceneStrategy   (сцена-оверлей)
├── include(nav_auth)     @id/dest_auth   → phone → sms → name   (по-прежнему Nav2)
└── confirmDialog         @id/dest_confirm                       (по-прежнему Nav2, по-прежнему в корне)
```

## Почему это правильный первый шаг
- **Минимальный риск, максимальная наглядность.** У листового модуля нет входящих зависимостей; его конвертация
  не сломает ничего вокруг. Внешний граф Nav2 даже не знает, что его содержимое изменилось.
- **Выигрыш виден сразу.** Внутри островка бэкстек — это список, которым вы владеете; идентификатор продукта — это
  типобезопасный `ProductDetail(id)` (без Bundle); нижний лист — пункт назначения, а не подкласс Fragment.
- **Шов — один Fragment.** `CatalogFragment` — это вся граница Nav2↔Nav3. Поскольку catalog является листовым модулем,
  он даже не требует обратного моста наружу — в отличие от веток миграции, которые следуют далее.

## 🎤 Реплики для докладчика
- `git diff flow2/01 flow2/02 --stat` — «всё изменение — одна фича; больше ничего не двигалось.»
- Открыть `CatalogNavigation.kt`: «list → detail — это `backStack.add(...)`; фильтр — просто ещё один
  пункт назначения со сценой нижнего листа. Никакого XML, никакого Bundle, никакого BottomSheetDialogFragment.»
- На устройстве: home → Catalog → продукт → назад, и открыть панель фильтра. «Всё Nav3 внутри одного Fragment.»
- Затем home → auth и home → confirm: «по-прежнему Nav2 — мы мигрировали ровно одну фичу.»
- Анонс следующего: «Далее конвертируем поток auth; хост дольше всех остаётся на Nav2 — намеренно.»
