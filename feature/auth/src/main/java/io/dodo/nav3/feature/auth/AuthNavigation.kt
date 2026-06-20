package io.dodo.nav3.feature.auth

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay

/**
 * The auth flow's navigation, now entirely Navigation 3 — one back stack, three keys.
 *
 * Notice how data flows forward: the phone number is carried in `SmsKey(phone)`, both are carried in
 * `NameKey(phone, code)`. It's just typed constructor arguments. In the Nav2 version this needed Safe
 * Args / bundles / a graph-scoped ViewModel; here it's the type system.
 *
 * Back behaviour: NavDisplay consumes the system back button while there's more than one step on the
 * stack (name → sms → phone). On the first step it stops consuming, so back falls through to the OUTER
 * Nav2 NavController, which exits the auth feature back to Home. Nothing to wire — it just composes.
 */
@Composable
fun AuthNavigation(
    onAuthComplete: (phone: String, name: String) -> Unit,
) {
    val backStack = rememberNavBackStack(PhoneKey)

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = entryProvider {
            entry<PhoneKey> {
                PhoneScreen(onNext = { phone -> backStack.add(SmsKey(phone)) })
            }
            entry<SmsKey> { key ->
                SmsScreen(phone = key.phone, onNext = { code -> backStack.add(NameKey(key.phone, code)) })
            }
            entry<NameKey> { key ->
                NameScreen(onFinish = { name -> onAuthComplete(key.phone, name) })
            }
        },
    )
}
