# flow2/03-nav3-auth-flow — Миграция флоу авторизации на Navigation 3

Мигрирована вторая фича. **Флоу авторизации** (`phone → sms → name`) — ранее три Fragment'а, связанных XML-вложенным графом **без передачи данных между шагами** — теперь представляет собой единый `AuthFragment`, который хостит Nav3 `NavDisplay` с тремя типобезопасными ключами. **Фича Catalog уже переведена на Nav3** (предыдущая ветка). **Хост (home) и корневой диалог подтверждения всё ещё на Navigation 2** — хост мигрирует последним.

## Что изменилось по сравнению с flow2/02
- `:feature:auth`: `PhoneFragment` / `SmsFragment` / `NameFragment` + XML-экшены `phone→sms→name`
  схлопываются в:
  - `AuthFragment` — единый хост-Fragment с Nav3 `NavDisplay` (`AuthNavigation`).
  - Типобезопасные ключи, передающие данные вперёд: `PhoneKey → SmsKey(phone) → NameKey(phone, code)`.
  - `AuthScreens` теперь передают своё значение на следующий шаг (`onNext: (String) -> Unit`) вместо
    колбэков без аргументов, как было в Nav2-версии.
  - `nav_auth.xml` сжимается до единственного `authFragment` (по-прежнему `@id/dest_auth`).
- Зависимости: `:feature:auth` получает `navigation3-runtime/ui`, `kotlinx-serialization-core` +
  плагин сериализации; оставляет `navigation-fragment-ktx` только для того, чтобы **выйти** из фичи.
- **Не тронуто:** catalog (уже на Nav3), home, корневой диалог подтверждения.

## Что демонстрирует эта ветка: типизированная передача данных
В Nav2 номер телефона здесь никогда не передавался вперёд — каждый шаг хранил локальное состояние (задокументированный болевой момент в flow2/01). Nav3 делает это задачей системы типов:
```kotlin
entry<PhoneKey> { PhoneScreen(onNext = { phone -> backStack.add(SmsKey(phone)) }) }
entry<SmsKey>   { key -> SmsScreen(phone = key.phone, onNext = { code -> backStack.add(NameKey(key.phone, code)) }) }
entry<NameKey>  { key -> NameScreen(onFinish = { name -> onAuthComplete(key.phone, name) }) }
```
Никакого Safe Args, никакого Bundle, никакого ViewModel, привязанного к графу — только типизированные аргументы конструктора.

## Структура
```
NavHostFragment (nav_main)  — хост Navigation 2
├── homeFragment                                                                  (всё ещё Nav2)
├── include(nav_catalog)  @id/dest_catalog → CatalogFragment → Nav3 NavDisplay   (Nav3, ветка 02)
├── include(nav_auth)     @id/dest_auth   → AuthFragment    → Nav3 NavDisplay    (Nav3, ЭТА ветка)
│        PhoneKey → SmsKey(phone) → NameKey(phone, code)
└── confirmDialog         @id/dest_confirm                                        (всё ещё Nav2, в корне)
```

## Поведение кнопки «Назад» на шве (границе)
`NavDisplay` перехватывает системный жест «Назад», пока в стеке авторизации больше одной записи (name → sms → phone). На первом шаге он перестаёт перехватывать, и жест «Назад» проваливается во внешний Nav2 `NavController`, который выходит из фичи на Home. `AuthFragment` выходит из фичи по завершении через `findNavController().popBackStack()`.

## 🎤 Реплики для докладчика
- `git diff flow2/02 flow2/03 --stat` — «Вторая фича; хост всё ещё не тронут.»
- Открыть `AuthNavigation.kt`: «Три Fragment'а и XML-граф превратились в три типобезопасных ключа на одном бэкстеке — и теперь номер телефона реально передаётся на шаг с SMS, бесплатно.»
- На устройстве: home → auth → phone → sms (обратите внимание на «Code sent to …») → name → finish → возврат на home.
- «Теперь две фичи на Nav3, обе как острова внутри Nav2-хоста. Следующая ветка: мигрируем сам хост и объединяем острова в единый корневой NavDisplay.»
