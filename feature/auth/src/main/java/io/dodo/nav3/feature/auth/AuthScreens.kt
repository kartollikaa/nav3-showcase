package io.dodo.nav3.feature.auth

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.skydoves.navgraph.annotations.NavDestination
import com.github.skydoves.navgraph.annotations.NavPreview
import io.dodo.nav3.core.designsystem.ContentBlue
import io.dodo.nav3.core.designsystem.theme.Nav3ShowcaseTheme

@Composable
private fun AuthStep(
    step: String,
    title: String,
    fieldLabel: String,
    buttonText: String,
    subtitle: String? = null,
    onNext: (String) -> Unit,
) {
    var text by remember { mutableStateOf("") }
    ContentBlue(title = title) {
        Text(step)
        if (subtitle != null) Text(subtitle)
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text(fieldLabel) },
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
        )
        Button(
            onClick = { onNext(text) },
            enabled = text.isNotBlank(),
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
        ) {
            Text(buttonText)
        }
    }
}

@NavDestination(route = PhoneKey::class)
@Composable
fun PhoneScreen(onNext: (phone: String) -> Unit) =
    AuthStep("Step 1 of 3", "Phone", "Phone number", "Send code", onNext = onNext)

@NavDestination(route = SmsKey::class)
@Composable
fun SmsScreen(phone: String, onNext: (code: String) -> Unit) =
    AuthStep("Step 2 of 3", "SMS code", "6-digit code", "Verify", subtitle = "Code sent to $phone", onNext = onNext)

@NavDestination(route = NameKey::class)
@Composable
fun NameScreen(onFinish: (name: String) -> Unit) =
    AuthStep("Step 3 of 3", "Your name", "Name", "Finish", onNext = onFinish)

// ── @NavPreview thumbnails ─────────────────────────────────────────────────────────────────────
@NavPreview(route = PhoneKey::class, primary = true)
@Preview
@Composable
private fun PhoneScreenPreview() = Nav3ShowcaseTheme { PhoneScreen(onNext = {}) }

@NavPreview(route = SmsKey::class, primary = true)
@Preview
@Composable
private fun SmsScreenPreview() = Nav3ShowcaseTheme { SmsScreen(phone = "+1 555 010 0042", onNext = {}) }

@NavPreview(route = NameKey::class, primary = true)
@Preview
@Composable
private fun NameScreenPreview() = Nav3ShowcaseTheme { NameScreen(onFinish = {}) }
