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

/**
 * Note: each step keeps its input in LOCAL state. In Navigation 2 sharing the phone number with the
 * SMS step means Safe Args / a bundle / a shared (graph-scoped) ViewModel — extra wiring we are NOT
 * doing here. The Nav3 migration branch (flow2/03) makes that shared state trivial.
 */
@Composable
private fun AuthStep(step: String, title: String, fieldLabel: String, buttonText: String, onNext: () -> Unit) {
    var text by remember { mutableStateOf("") }
    ContentBlue(title = title) {
        Text(step)
        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text(fieldLabel) },
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
        )
        Button(
            onClick = onNext,
            enabled = text.isNotBlank(),
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
        ) {
            Text(buttonText)
        }
    }
}

@Composable
fun PhoneScreen(onNext: () -> Unit) = AuthStep("Step 1 of 3", "Phone", "Phone number", "Send code", onNext)

@Composable
fun SmsScreen(onNext: () -> Unit) = AuthStep("Step 2 of 3", "SMS code", "6-digit code", "Verify", onNext)

@Composable
fun NameScreen(onFinish: () -> Unit) = AuthStep("Step 3 of 3", "Your name", "Name", "Finish", onFinish)
