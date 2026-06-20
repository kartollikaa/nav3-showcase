package io.dodo.nav3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import io.dodo.nav3.core.designsystem.theme.Nav3ShowcaseTheme
import io.dodo.nav3.feature.catalog.ProductDetail
import io.dodo.nav3.feature.catalog.ProductList
import io.dodo.nav3.feature.catalog.ProductListScreen
import io.dodo.nav3.feature.catalog.ProductDetailScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            Nav3ShowcaseTheme {
                Scaffold { padding ->
                    CatalogNavigation(Modifier.padding(padding))
                }
            }
        }
    }
}

/**
 * This is the ENTIRE navigation setup for branch 1. Read it top to bottom — there is no hidden
 * framework state anywhere.
 */
@Composable
fun CatalogNavigation(modifier: Modifier = Modifier) {
    // (1) The back stack is a plain observable list that WE own.
    //     rememberNavBackStack additionally saves/restores it across config changes & process death.
    val backStack = rememberNavBackStack(ProductList)

    // (2) NavDisplay observes that list and renders the entry on top.
    NavDisplay(
        backStack = backStack,
        modifier = modifier,
        // (3) "Back" is just: remove the last key from the list.
        onBack = { backStack.removeLastOrNull() },
        // (4) entryProvider maps a key -> the content to show for it.
        entryProvider = entryProvider {
            entry<ProductList> {
                // "Navigate forward" is just: add a key to the list.
                ProductListScreen(onProductClick = { id -> backStack.add(ProductDetail(id)) })
            }
            entry<ProductDetail> { key ->
                // The key is fully typed: key.id is a String, guaranteed by the compiler.
                ProductDetailScreen(id = key.id, onBack = { backStack.removeLastOrNull() })
            }
        },
    )
}
