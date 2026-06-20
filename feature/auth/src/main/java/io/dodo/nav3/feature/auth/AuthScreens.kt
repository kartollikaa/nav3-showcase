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
import androidx.compose.ui.unit.dp
import io.dodo.nav3.core.designsystem.ContentBlue

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

@Composable
fun PhoneScreen(onNext: (phone: String) -> Unit) =
    AuthStep("Step 1 of 3", "Phone", "Phone number", "Send code", onNext = onNext)

@Composable
fun SmsScreen(phone: String, onNext: (code: String) -> Unit) =
    AuthStep("Step 2 of 3", "SMS code", "6-digit code", "Verify", subtitle = "Code sent to $phone", onNext = onNext)

@Composable
fun NameScreen(onFinish: (name: String) -> Unit) =
    AuthStep("Step 3 of 3", "Your name", "Name", "Finish", onNext = onFinish)
