package io.dodo.nav3.feature.catalog

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.dodo.nav3.core.designsystem.ContentBlue
import io.dodo.nav3.core.designsystem.ContentGreen

/**
 * Plain composables. They know NOTHING about navigation — they only expose callbacks. The caller
 * decides what "open a product" or "go back" means by mutating the back stack. This keeps screens
 * trivially testable and previewable.
 */
@Composable
fun ProductListScreen(onProductClick: (String) -> Unit) {
    ContentGreen(title = "Products") {
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
