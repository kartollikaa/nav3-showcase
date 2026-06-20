package io.dodo.nav3.feature.auth

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey

/**
 * The auth feature's contribution to the ROOT back stack. The phone/sms/name steps are now entries on
 * the same single back stack as everything else — auth is no longer a separate navigator.
 *
 * It pushes its OWN keys (SmsKey/NameKey carry typed data forward) and calls `onComplete` so the app
 * can decide what "done" means (here: pop back to Home).
 */
fun EntryProviderScope<NavKey>.authEntries(
    backStack: NavBackStack<NavKey>,
    onComplete: (phone: String, name: String) -> Unit,
) {
    entry<PhoneKey> {
        PhoneScreen(onNext = { phone -> backStack.add(SmsKey(phone)) })
    }
    entry<SmsKey> { key ->
        SmsScreen(phone = key.phone, onNext = { code -> backStack.add(NameKey(key.phone, code)) })
    }
    entry<NameKey> { key ->
        NameScreen(onFinish = { name -> onComplete(key.phone, name) })
    }
}
