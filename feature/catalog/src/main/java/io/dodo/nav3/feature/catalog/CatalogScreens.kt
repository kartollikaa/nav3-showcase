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

/**
 * The screen takes plain state + callbacks. It has NO idea a ViewModel exists — the entry in
 * MainActivity creates the VM and feeds it in. That keeps the screen previewable and lets the
 * ViewModel's lifetime be owned by navigation.
 */
@Composable
fun ProductDetailScreen(
    product: Product,
    clicks: Int,
    debugLabel: String,
    onIncrement: () -> Unit,
    onOpenAnother: () -> Unit,
    onBack: () -> Unit,
) {
    ContentBlue(title = "Product: ${product.name}") {
        Text(product.description)
        Text("Clicks held by this entry's ViewModel: $clicks")
        Text("($debugLabel)")
        Button(onClick = onIncrement, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
            Text("Increment (writes to THIS entry's ViewModel)")
        }
        OutlinedButton(onClick = onOpenAnother, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
            Text("Open another product (pushes a new entry → new VM)")
        }
        Button(onClick = onBack, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
            Text("Back (pops this entry → its VM is cleared)")
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
