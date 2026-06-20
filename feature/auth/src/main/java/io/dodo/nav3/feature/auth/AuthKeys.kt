package io.dodo.nav3.feature.auth

import androidx.navigation3.runtime.NavKey
import com.github.skydoves.navgraph.annotations.NavEdge
import kotlinx.serialization.Serializable

// Linear auth sub-flow. Each edge's `from` defaults to the annotated route class. SmsKey/NameKey carry
// typed data (phone, code) — those serializable properties become typed argument arrows on the map.

@NavEdge(to = SmsKey::class, label = "Send code")
@Serializable
data object PhoneKey : NavKey

@NavEdge(to = NameKey::class, label = "Verify")
@Serializable
data class SmsKey(val phone: String) : NavKey

@Serializable
data class NameKey(val phone: String, val code: String) : NavKey
