package io.dodo.nav3.feature.catalog

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.dodo.nav3.core.designsystem.ContentBlue
import io.dodo.nav3.core.designsystem.ContentGreen
import io.dodo.nav3.core.designsystem.ContentMauve

@Composable
fun ProductListScreen(
    onProductClick: (String) -> Unit,
    onOpenFilter: () -> Unit,
) {
    ContentGreen(title = "Products") {
        OutlinedButton(onClick = onOpenFilter, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
            Text("Open filter (bottom sheet)")
        }
        listOf("apple", "banana", "cherry").forEach { id ->
            Button(
                onClick = { onProductClick(id) },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            ) {
                Text("Open \"$id\"")
            }
        }
    }
}

@Composable
fun ProductDetailScreen(id: String, onBack: () -> Unit) {
    ContentBlue(title = "Product: $id") {
        Button(onClick = onBack, modifier = Modifier.padding(top = 8.dp)) {
            Text("Back")
        }
    }
}

@Composable
fun FilterSheet(onApply: () -> Unit) {
    ContentMauve(title = "Filter") {
        Text("Imagine some filter controls here.")
        Button(onClick = onApply, modifier = Modifier.padding(top = 8.dp)) {
            Text("Apply & close")
        }
    }
}
