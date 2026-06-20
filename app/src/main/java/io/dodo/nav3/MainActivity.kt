package io.dodo.nav3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.dodo.nav3.core.designsystem.theme.Nav3ShowcaseTheme

/**
 * `main` branch entry point. There is no navigation here on purpose — `main` only carries the
 * shared scaffold (build setup + design system). Each concept lives on its own branch.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            Nav3ShowcaseTheme {
                Scaffold { padding ->
                    Column(Modifier.padding(padding).padding(24.dp)) {
                        Text("Navigation 3 Showcase", style = MaterialTheme.typography.headlineMedium)
                        Spacer(Modifier.height(12.dp))
                        Text(
                            "You're on `main` (shared scaffold only).\n\n" +
                                "Intro flow (complexity rising):\n" +
                                "  • flow1/01-basics\n" +
                                "  • flow1/02-scenes\n" +
                                "  • flow1/03-viewmodel-decorator\n\n" +
                                "Migration flow (Nav2 → Nav3):\n" +
                                "  • flow2/01-nav2-baseline\n" +
                                "  • flow2/02-nav3-bottomsheet\n" +
                                "  • flow2/03-nav3-auth-flow\n" +
                                "  • flow2/04-unified",
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }
                }
            }
        }
    }
}
