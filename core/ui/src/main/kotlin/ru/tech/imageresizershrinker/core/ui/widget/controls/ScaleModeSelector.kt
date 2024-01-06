package ru.tech.imageresizershrinker.core.ui.widget.controls

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.core.domain.ImageScaleMode
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.theme.outlineVariant
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container
import ru.tech.imageresizershrinker.core.ui.widget.utils.LocalSettingsState

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ScaleModeSelector(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp),
    shape: Shape = RoundedCornerShape(24.dp),
    enableItemsCardBackground: Boolean = true,
    value: ImageScaleMode,
    onValueChange: (ImageScaleMode) -> Unit,
    title: @Composable ColumnScope.() -> Unit = {
        Text(
            text = stringResource(R.string.scale_mode),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium
        )
    }
) {
    val haptics = LocalHapticFeedback.current
    val items = remember {
        ImageScaleMode.entries
    }
    val settingsState = LocalSettingsState.current

    LaunchedEffect(settingsState) {
        if (value != settingsState.defaultImageScaleMode) {
            onValueChange(settingsState.defaultImageScaleMode)
        }
    }

    Column(
        modifier = modifier
            .container(
                shape = shape,
                color = backgroundColor
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        title()
        Spacer(modifier = Modifier.height(8.dp))

        FlowRow(
            verticalArrangement = Arrangement.spacedBy(
                8.dp,
                Alignment.CenterVertically
            ),
            horizontalArrangement = Arrangement.spacedBy(
                8.dp,
                Alignment.CenterHorizontally
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .then(
                    if (enableItemsCardBackground) {
                        Modifier
                            .container()
                            .padding(horizontal = 8.dp, vertical = 12.dp)
                    } else Modifier
                )
        ) {
            items.forEach {
                Chip(
                    onClick = {
                        haptics.performHapticFeedback(
                            HapticFeedbackType.LongPress
                        )
                        onValueChange(it)
                    },
                    selected = it == value,
                    label = {
                        Text(text = it.title)
                    }
                )
            }
        }
    }
}

@Composable
private fun Chip(
    selected: Boolean,
    onClick: () -> Unit,
    label: @Composable () -> Unit
) {
    val color by animateColorAsState(
        if (selected) MaterialTheme.colorScheme.tertiary
        else MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f)
    )

    CompositionLocalProvider(
        LocalTextStyle provides MaterialTheme.typography.labelLarge.copy(
            fontWeight = FontWeight.SemiBold,
            color = if (selected) MaterialTheme.colorScheme.onTertiary
            else MaterialTheme.colorScheme.onSurface
        ),
    ) {
        Box(
            modifier = Modifier
                .container(
                    color = color,
                    resultPadding = 0.dp,
                    borderColor = if (!selected) MaterialTheme.colorScheme.outlineVariant()
                    else MaterialTheme.colorScheme.tertiary
                        .copy(alpha = 0.9f)
                        .compositeOver(Color.Black),
                    shape = MaterialTheme.shapes.small,
                    autoShadowElevation = 0.5.dp
                )
                .clickable(onClick = onClick),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                label()
            }
        }
    }
}

private val ImageScaleMode.title: String
    @Composable
    get() = when (this) {
        ImageScaleMode.Bilinear -> stringResource(id = R.string.bilinear)
        ImageScaleMode.CatmullRom -> stringResource(id = R.string.catmul_rom)
        ImageScaleMode.Cubic -> stringResource(id = R.string.cubic)
        ImageScaleMode.Hann -> stringResource(id = R.string.hann)
        ImageScaleMode.Hermite -> stringResource(id = R.string.hermite)
        ImageScaleMode.Lanczos -> stringResource(id = R.string.lanczos)
        ImageScaleMode.Mitchell -> stringResource(id = R.string.mitchell)
        ImageScaleMode.Nearest -> stringResource(id = R.string.nearest)
        ImageScaleMode.Spline -> stringResource(id = R.string.spline)
        else -> stringResource(id = R.string.basic)
    }