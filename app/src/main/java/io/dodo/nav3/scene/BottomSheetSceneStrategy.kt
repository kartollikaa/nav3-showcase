package io.dodo.nav3.scene

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.rememberLifecycleOwner
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavMetadataKey
import androidx.navigation3.runtime.get
import androidx.navigation3.runtime.metadata
import androidx.navigation3.scene.OverlayScene
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneStrategy
import androidx.navigation3.scene.SceneStrategyScope

/**
 * Bottom-sheet support is NOT in the core Navigation 3 library — it is an official "recipe" you copy
 * into your project (android/nav3-recipes). This file is that recipe.
 *
 * A [SceneStrategy] inspects the back stack and decides HOW the top entries should be laid out. This
 * one says: "if the top entry asked to be a bottom sheet (via metadata), render it in a
 * [ModalBottomSheet] floating over the entries beneath it." Because it produces an [OverlayScene],
 * the content underneath stays composed and visible.
 */
@OptIn(ExperimentalMaterial3Api::class)
internal data class BottomSheetScene<T : Any>(
    override val key: T,
    override val previousEntries: List<NavEntry<T>>,
    override val overlaidEntries: List<NavEntry<T>>,
    private val entry: NavEntry<T>,
    private val modalBottomSheetProperties: ModalBottomSheetProperties,
    private val onBack: () -> Unit,
) : OverlayScene<T> {

    override val entries: List<NavEntry<T>> = listOf(entry)

    override val content: @Composable (() -> Unit) = {
        val lifecycleOwner = rememberLifecycleOwner()
        ModalBottomSheet(
            onDismissRequest = onBack,
            properties = modalBottomSheetProperties,
        ) {
            CompositionLocalProvider(LocalLifecycleOwner provides lifecycleOwner) {
                entry.Content()
            }
        }
    }
}

/**
 * Displays any entry whose metadata contains [bottomSheet] inside a [ModalBottomSheet].
 *
 * Overlay strategies should be listed BEFORE non-overlay strategies in `NavDisplay(sceneStrategies = …)`.
 */
@OptIn(ExperimentalMaterial3Api::class)
class BottomSheetSceneStrategy<T : Any> : SceneStrategy<T> {

    override fun SceneStrategyScope<T>.calculateScene(entries: List<NavEntry<T>>): Scene<T>? {
        val lastEntry = entries.lastOrNull() ?: return null
        val bottomSheetProperties = lastEntry.metadata[BottomSheetKey] ?: return null
        @Suppress("UNCHECKED_CAST")
        return BottomSheetScene(
            key = lastEntry.contentKey as T,
            previousEntries = entries.dropLast(1),
            overlaidEntries = entries.dropLast(1),
            entry = lastEntry,
            modalBottomSheetProperties = bottomSheetProperties,
            onBack = onBack,
        )
    }

    companion object {
        /** Add this to an entry's metadata to make it render as a bottom sheet. */
        fun bottomSheet(
            modalBottomSheetProperties: ModalBottomSheetProperties = ModalBottomSheetProperties(),
        ) = metadata {
            put(BottomSheetKey, modalBottomSheetProperties)
        }

        object BottomSheetKey : NavMetadataKey<ModalBottomSheetProperties>
    }
}
