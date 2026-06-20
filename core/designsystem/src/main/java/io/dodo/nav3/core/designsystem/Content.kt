package io.dodo.nav3.core.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * A deliberately trivial "screen": a colored surface with a title and an optional content slot.
 *
 * Every demo screen in this repo is built from these so that nothing distracts from the
 * navigation code. The colors mirror the ones used in the official Navigation 3 docs samples.
 */
@Composable
fun Content(
    title: String,
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    body: @Composable ColumnScope.() -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(24.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )
        Spacer(Modifier.height(16.dp))
        body()
    }
}

@Composable
fun ContentGreen(title: String, modifier: Modifier = Modifier, body: @Composable ColumnScope.() -> Unit = {}) =
    Content(title, Color(0xFFC8E6C9), modifier, body)

@Composable
fun ContentBlue(title: String, modifier: Modifier = Modifier, body: @Composable ColumnScope.() -> Unit = {}) =
    Content(title, Color(0xFFBBDEFB), modifier, body)

@Composable
fun ContentOrange(title: String, modifier: Modifier = Modifier, body: @Composable ColumnScope.() -> Unit = {}) =
    Content(title, Color(0xFFFFE0B2), modifier, body)

@Composable
fun ContentMauve(title: String, modifier: Modifier = Modifier, body: @Composable ColumnScope.() -> Unit = {}) =
    Content(title, Color(0xFFE1BEE7), modifier, body)

@Composable
fun ContentRed(title: String, modifier: Modifier = Modifier, body: @Composable ColumnScope.() -> Unit = {}) =
    Content(title, Color(0xFFFFCDD2), modifier, body)

@Composable
fun ContentYellow(title: String, modifier: Modifier = Modifier, body: @Composable ColumnScope.() -> Unit = {}) =
    Content(title, Color(0xFFFFF9C4), modifier, body)

@Composable
fun ContentGray(title: String, modifier: Modifier = Modifier, body: @Composable ColumnScope.() -> Unit = {}) =
    Content(title, Color(0xFFECEFF1), modifier, body)
