package ru.tech.imageresizershrinker.core.ui.widget.controls

import android.os.Build
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
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
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ru.tech.imageresizershrinker.core.domain.model.ImageFormat
import ru.tech.imageresizershrinker.core.resources.R
import ru.tech.imageresizershrinker.core.ui.theme.outlineVariant
import ru.tech.imageresizershrinker.core.ui.widget.modifier.container

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExtensionGroup(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp),
    enabled: Boolean,
    entries: List<ImageFormat> = ImageFormat.entries,
    value: ImageFormat,
    onValueChange: (ImageFormat) -> Unit
) {
    val disColor = MaterialTheme.colorScheme.onSurface
        .copy(alpha = 0.38f)
        .compositeOver(MaterialTheme.colorScheme.surface)

    LaunchedEffect(value, entries) {
        if (value !in entries) onValueChange(ImageFormat.Png)
    }

    val haptics = LocalHapticFeedback.current

    ProvideTextStyle(
        value = TextStyle(
            color = if (!enabled) disColor
            else Color.Unspecified
        )
    ) {
        Column(
            modifier = modifier
                .container(
                    shape = RoundedCornerShape(24.dp),
                    color = backgroundColor
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(R.string.extension),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))

            AnimatedContent(
                targetState = entries.filtered(),
                transitionSpec = {
                    fadeIn().togetherWith(fadeOut()).using(SizeTransform(false))
                }
            ) { items ->
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
                        .container()
                        .padding(horizontal = 8.dp, vertical = 12.dp)
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
    }
}

@Composable
private fun List<ImageFormat>.filtered(): List<ImageFormat> = remember(this) {
    if (Build.VERSION.SDK_INT <= 24) toMutableList().apply {
        removeAll(ImageFormat.highLevelFormats)
    }
    else this
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