package io.dodo.nav3.feature.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.dodo.nav3.core.designsystem.ContentGreen

@Composable
fun HomeScreen(
    onOpenCatalog: () -> Unit,
    onOpenAuth: () -> Unit,
    onConfirm: () -> Unit,
) {
    ContentGreen(title = "Home — Fragments + Navigation 2") {
        Text("Each screen is a Fragment hosting a ComposeView. The NavController (Navigation 2) owns the back stack.")
        Button(onClick = onOpenCatalog, modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) {
            Text("Open Catalog  (list → detail, filter sheet)")
        }
        OutlinedButton(onClick = onOpenAuth, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
            Text("Start auth flow  (nested nav graph)")
        }
        OutlinedButton(onClick = onConfirm, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
            Text("Confirm order  (DialogFragment dest)")
        }
    }
}

@Composable
fun ConfirmDialogContent(onYes: () -> Unit, onNo: () -> Unit) {
    Surface(shape = MaterialTheme.shapes.large) {
        Column(Modifier.padding(24.dp)) {
            Text("Place order?", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(12.dp))
            Text("A DialogFragment destination hosting Compose.")
            Spacer(Modifier.height(16.dp))
            Row {
                TextButton(onClick = onNo) { Text("Cancel") }
                Spacer(Modifier.width(8.dp))
                Button(onClick = onYes) { Text("Confirm") }
            }
        }
    }
}
