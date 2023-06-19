package com.stevedenheyer.scriptassistant.audioeditor.components

import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Constraints

@Composable
fun HorizontalSlider(modifier: Modifier, threshold: Float, onValueChanged: (Float) -> Unit, onValueChangedFinished: () -> Unit) {
    Slider(
        modifier = modifier
            .graphicsLayer {
                rotationZ = 270f
                transformOrigin = TransformOrigin(0f, 0f)
            }
            .layout { measurable, constraints ->
                val placeable = measurable.measure(
                    Constraints(
                        minWidth = constraints.minHeight,
                        maxWidth = constraints.maxHeight,
                        minHeight = constraints.minWidth,
                        maxHeight = constraints.maxWidth,
                    )
                )
                layout(placeable.height, placeable.width) {
                    placeable.placeRelative(-placeable.width, 0)
                }

            }
        ,
        value = threshold, onValueChange = { threshold -> onValueChanged(threshold) }, onValueChangeFinished = onValueChangedFinished, valueRange = 0f..100f
    )
}
