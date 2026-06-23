# flow1/03-viewmodel-decorator — Привязка ViewModel к пункту назначения

**Цель:** сделать так, чтобы каждый пункт назначения владел собственной ViewModel, — то есть VM **создаётся при переходе на экран и очищается при выходе с него**, — а также показать, что происходит без этого механизма.

## Что изменилось по сравнению с `flow1/02-scenes`
- В `:feature:catalog` появились `ProductDetailViewModel`, `ProductRepository` и ручной DI-граф `CatalogGraph` («шов Dagger»).
- `NavDisplay` теперь передаёт `entryDecorators = listOf(rememberSaveableStateHolderNavEntryDecorator(), rememberViewModelStoreNavEntryDecorator())`.
- Запись `ProductDetail` создаёт VM через `viewModel { ProductDetailViewModel(...) }`.

## Одна строка, которая решает всё

```kotlin
NavDisplay(
    entryDecorators = listOf(
        rememberSaveableStateHolderNavEntryDecorator(), // должен быть первым; обеспечивает работу rememberSaveable {}
        rememberViewModelStoreNavEntryDecorator(),      // ← даёт каждой NavEntry собственный ViewModelStore
    ),
    /* … */
    entryProvider = entryProvider {
        entry<ProductDetail> { key ->
            val vm = viewModel { ProductDetailViewModel(CatalogGraph.productRepository, key.id) }
            ProductDetailScreen(product = vm.product, clicks = vm.clicks, /* … */)
        }
    },
)
```

### Что такое декоратор
`NavEntryDecorator` оборачивает содержимое **каждой** записи — это место для установки инфраструктуры для отдельной записи через `CompositionLocalProvider` и для очистки ресурсов при извлечении записи из стека. Nav3 поставляет два декоратора, которые почти всегда следует использовать в следующем порядке:

1. **`rememberSaveableStateHolderNavEntryDecorator()`** — должен быть первым. Оборачивает каждую запись в `SaveableStateProvider`, так что `rememberSaveable {}` внутри экранов привязывается к конкретной записи и переживает смену конфигурации / гибель процесса.
2. **`rememberViewModelStoreNavEntryDecorator()`** (из `androidx.lifecycle:lifecycle-viewmodel-navigation3`)
   — устанавливает отдельный `ViewModelStoreOwner` для каждой записи. `viewModel()` внутри этой записи теперь обращается к хранилищу, которое живёт ровно столько, сколько запись находится в бэкстеке, и **очищается (`clear()`) при её извлечении** (срабатывает ваш `onCleared()`).

> Заметка о терминологии для доклада: в ранних альфа-версиях Nav3 эта функциональность была разделена между `rememberSceneSetupNavEntryDecorator` и `rememberSavedStateNavEntryDecorator`. В стабильной версии (1.x) описанные выше два декоратора являются каноническими. Если в ваших слайдах остались старые названия — это то переименование, которое стоит упомянуть.

## Почему это важно
- **Правильный жизненный цикл = правильная работа с памятью и состоянием.** Детальная VM (и корутины её `viewModelScope`, потоки, подписчики) уничтожается в момент, когда пользователь уходит с экрана, — а не когда Activity наконец завершается.
- **Состояние отдельно для каждого экземпляра.** Открываем *apple*, затем «открыть ещё один товар» → *banana*. Каждая запись `ProductDetail` получает **собственный** экземпляр VM (наблюдайте за меткой `VM #n` и тегом logcat `Nav3VMScope`). Счётчики `clicks` у них независимы.
- **Переживает поворот.** Несколько раз нажмите кнопку, поверните устройство — счётчик сохранится, поскольку VM переживает смену конфигурации, пока запись активна.

## Что сломается, если НЕ добавить `rememberViewModelStoreNavEntryDecorator`
Без него `viewModel()` откатывается к ближайшему `ViewModelStoreOwner` — то есть к **Activity**. Возникают два сбоя:

1. **Утечка состояния.** `viewModel()`, ключом которого служит только тип, возвращает *один и тот же* `ProductDetailViewModel` для *apple* и *banana*. Открываем apple (clicks = 5), открываем banana → там тоже показывается 5, и записи конфликтуют. Пришлось бы придумывать ручную схему ключей для обхода этой проблемы.
2. **Утечки / устаревшая работа.** VM никогда не очищается при уходе с экрана; она (и все активные задачи `viewModelScope`, сборщики, кешированные данные) живёт до уничтожения Activity. На глубоком или цикличном бэкстеке это растущая куча зомби-ViewModels.

Чтобы продемонстрировать вживую: закомментируйте строку `rememberViewModelStoreNavEntryDecorator()`, перезапустите приложение и переключайтесь между apple и banana — счётчик и метка `VM #n` перестанут быть привязаны к отдельному товару.

## 🎤 Реплики для докладчика
- Откройте logcat с фильтром `Nav3VMScope`. Перейдите на apple → «created VM #1». «Открыть ещё один» → banana → «created VM #2». Нажмите «Назад» → «cleared VM #2». *«Жизненный цикл автоматический и привязан к бэкстеку.»*
- Несколько раз нажмите кнопку на apple, **поверните экран** → счётчик сохранился (смена конфигурации), но VM не утекает между экранами.
- Кульминация: удалите строку с декоратором, перезапустите, покажите утечку состояния между товарами. Верните строку обратно.
- Связь с DI: *«Dagger строит объект; Navigation 3 управляет тем, когда он живёт и умирает. Это разные задачи.»*
