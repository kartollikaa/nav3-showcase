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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.skydoves.navgraph.annotations.NavDestination
import com.github.skydoves.navgraph.annotations.NavPreview
import io.dodo.nav3.core.designsystem.ContentGreen
import io.dodo.nav3.core.designsystem.theme.Nav3ShowcaseTheme

@NavDestination(route = HomeKey::class)
@Composable
fun HomeScreen(
    onOpenCatalog: () -> Unit,
    onOpenAuth: () -> Unit,
    onConfirm: () -> Unit,
) {
    ContentGreen(title = "Home — one root NavDisplay (Nav3)") {
        Text("No Fragments, no FragmentManager, no XML graphs. Home, the catalog, the auth steps, and every overlay share one back stack.")
        Button(onClick = onOpenCatalog, modifier = Modifier.fillMaxWidth().padding(top = 12.dp)) {
            Text("Open Catalog  (list → detail, filter sheet)")
        }
        OutlinedButton(onClick = onOpenAuth, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
            Text("Start auth flow  (phone → sms → name)")
        }
        OutlinedButton(onClick = onConfirm, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
            Text("Confirm order  (dialog scene)")
        }
    }
}

@NavDestination(route = ConfirmKey::class)
@Composable
fun ConfirmDialogContent(onYes: () -> Unit, onNo: () -> Unit) {
    Surface(shape = MaterialTheme.shapes.large) {
        Column(Modifier.padding(24.dp)) {
            Text("Place order?", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(12.dp))
            Text("A dialog destination, rendered by DialogSceneStrategy on the root back stack.")
            Spacer(Modifier.height(16.dp))
            Row {
                TextButton(onClick = onNo) { Text("Cancel") }
                Spacer(Modifier.width(8.dp))
                Button(onClick = onYes) { Text("Confirm") }
            }
        }
    }
}

// ── @NavPreview thumbnails ─────────────────────────────────────────────────────────────────────
@NavPreview(route = HomeKey::class, primary = true)
@Preview
@Composable
private fun HomeScreenPreview() = Nav3ShowcaseTheme { HomeScreen(onOpenCatalog = {}, onOpenAuth = {}, onConfirm = {}) }

@NavPreview(route = ConfirmKey::class, primary = true)
@Preview
@Composable
private fun ConfirmDialogPreview() = Nav3ShowcaseTheme { ConfirmDialogContent(onYes = {}, onNo = {}) }
