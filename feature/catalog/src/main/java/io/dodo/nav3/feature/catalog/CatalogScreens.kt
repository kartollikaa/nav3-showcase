package io.dodo.nav3.feature.catalog

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.skydoves.navgraph.annotations.NavDestination
import com.github.skydoves.navgraph.annotations.NavPreview
import io.dodo.nav3.core.designsystem.ContentBlue
import io.dodo.nav3.core.designsystem.ContentGreen
import io.dodo.nav3.core.designsystem.ContentMauve
import io.dodo.nav3.core.designsystem.theme.Nav3ShowcaseTheme

// Dumb, callback-only screens — identical in spirit to every other feature in the repo, so the
// branches differ ONLY in their navigation wiring, never in the UI.

@NavDestination(route = CatalogList::class)
@Composable
fun ProductListScreen(onProductClick: (String) -> Unit, onOpenFilter: () -> Unit) {
    ContentGreen(title = "Catalog — products") {
        OutlinedButton(onClick = onOpenFilter, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
            Text("Open filter  (BottomSheetDialogFragment dest)")
        }
        listOf("apple", "banana", "cherry").forEach { id ->
            Button(onClick = { onProductClick(id) }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                Text("Open \"$id\"")
            }
        }
    }
}

@NavDestination(route = ProductDetail::class)
@Composable
fun ProductDetailScreen(id: String, onBack: () -> Unit) {
    ContentBlue(title = "Product: $id") {
        Button(onClick = onBack, modifier = Modifier.padding(top = 8.dp)) { Text("Back") }
    }
}

@NavDestination(route = FilterKey::class)
@Composable
fun FilterSheet(onApply: () -> Unit) {
    ContentMauve(title = "Filter") {
        Text("Imagine some filter controls here.")
        Button(onClick = onApply, modifier = Modifier.padding(top = 8.dp)) { Text("Apply & close") }
    }
}

// ── @NavPreview thumbnails ─────────────────────────────────────────────────────────────────────
@NavPreview(route = CatalogList::class, primary = true)
@Preview
@Composable
private fun ProductListPreview() = Nav3ShowcaseTheme { ProductListScreen(onProductClick = {}, onOpenFilter = {}) }

@NavPreview(route = ProductDetail::class, primary = true)
@Preview
@Composable
private fun ProductDetailPreview() = Nav3ShowcaseTheme { ProductDetailScreen(id = "apple", onBack = {}) }

@NavPreview(route = FilterKey::class, primary = true)
@Preview
@Composable
private fun FilterSheetPreview() = Nav3ShowcaseTheme { FilterSheet(onApply = {}) }
