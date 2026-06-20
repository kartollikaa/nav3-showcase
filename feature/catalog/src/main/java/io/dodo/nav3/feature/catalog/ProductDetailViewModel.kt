package io.dodo.nav3.feature.catalog

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import java.util.concurrent.atomic.AtomicInteger

/**
 * Holds the UI state for ONE product detail screen.
 *
 * It logs its own creation and clearing under the tag "Nav3VMScope". Watch logcat during the demo:
 * with per-entry scoping you see one "created" per push and one "cleared" per pop. The `instanceId`
 * is printed on screen so you can see each ProductDetail entry gets its OWN instance.
 */
class ProductDetailViewModel(
    repository: ProductRepository,
    private val productId: String,
) : ViewModel() {

    private val instanceId = NEXT_ID.incrementAndGet()

    val product: Product = repository.load(productId)

    var clicks by mutableIntStateOf(0)
        private set

    val debugLabel: String get() = "VM #$instanceId · $productId"

    init {
        Log.d(TAG, "created  $debugLabel")
    }

    fun onClick() {
        clicks++
    }

    override fun onCleared() {
        Log.d(TAG, "cleared  $debugLabel")
    }

    companion object {
        private const val TAG = "Nav3VMScope"
        private val NEXT_ID = AtomicInteger(0)
    }
}
