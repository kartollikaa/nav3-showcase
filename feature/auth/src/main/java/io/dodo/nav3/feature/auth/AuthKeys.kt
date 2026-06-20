package io.dodo.nav3.feature.auth

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object PhoneKey : NavKey

@Serializable
data class SmsKey(val phone: String) : NavKey

@Serializable
data class NameKey(val phone: String, val code: String) : NavKey
