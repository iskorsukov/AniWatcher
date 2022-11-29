package com.iskorsukov.aniwatcher.ui.base.effects

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.unit.Dp
import com.iskorsukov.aniwatcher.ui.theme.LocalColors
import kotlin.math.abs

fun Modifier.verticalFadingEdge(
    lazyListState: LazyListState,
    length: Dp,
    edgeColor: Color? = null,
    topEdge: Boolean = true,
    bottomEdge: Boolean = true
) = composed(
    debugInspectorInfo {
        name = "length"
        value = length
    }
) {
    val color = edgeColor ?: LocalColors.current.background

    drawWithContent {
        val topFadingEdgeStrength by derivedStateOf {
            lazyListState.layoutInfo.run {
                val firstItem = visibleItemsInfo.first()
                when {
                    visibleItemsInfo.size in 0..1 -> 0f
                    firstItem.index > 0 -> 1f
                    firstItem.offset == viewportStartOffset -> 0f
                    firstItem.offset < viewportStartOffset -> firstItem.run {
                        abs(offset) / size.toFloat()
                    }
                    else -> 1f
                }
            }.coerceAtMost(1f) * length.value
        }
        val bottomFadingEdgeStrength by derivedStateOf {
            lazyListState.layoutInfo.run {
                val lastItem = visibleItemsInfo.last()
                when {
                    visibleItemsInfo.size in 0..1 -> 0f
                    lastItem.index < totalItemsCount - 1 -> 1f
                    lastItem.offset + lastItem.size <= viewportEndOffset -> 0f
                    lastItem.offset + lastItem.size > viewportEndOffset -> lastItem.run {
                        (size - (viewportEndOffset - offset)) / size.toFloat()
                    }
                    else -> 1f
                }
            }.coerceAtMost(1f) * length.value
        }

        drawContent()

        if (topEdge) {
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        color,
                        Color.Transparent,
                    ),
                    startY = 0f,
                    endY = topFadingEdgeStrength,
                ),
                size = Size(
                    this.size.width,
                    topFadingEdgeStrength
                ),
            )
        }

        if (bottomEdge) {
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        color,
                    ),
                    startY = size.height - bottomFadingEdgeStrength,
                    endY = size.height,
                ),
                topLeft = Offset(x = 0f, y = size.height - bottomFadingEdgeStrength),
            )
        }
    }
}

fun Modifier.horizontalFadingEdge(
    lazyListState: LazyListState,
    length: Dp,
    edgeColor: Color? = null,
) = composed(
    debugInspectorInfo {
        name = "length"
        value = length
    }
) {
    val color = edgeColor ?: LocalColors.current.background

    drawWithContent {
        val startFadingEdgeStrength by derivedStateOf {
            lazyListState.layoutInfo.run {
                val firstItem = visibleItemsInfo.first()
                when {
                    visibleItemsInfo.size in 0..1 -> 0f
                    firstItem.index > 0 -> 1f
                    firstItem.offset == viewportStartOffset -> 0f
                    firstItem.offset < viewportStartOffset -> firstItem.run {
                        abs(offset) / size.toFloat()
                    }
                    else -> 1f
                }
            }.coerceAtMost(1f) * length.value
        }
        val endFadingEdgeStrength by derivedStateOf {
            lazyListState.layoutInfo.run {
                val lastItem = visibleItemsInfo.last()
                when {
                    visibleItemsInfo.size in 0..1 -> 0f
                    lastItem.index < totalItemsCount - 1 -> 1f
                    lastItem.offset + lastItem.size <= viewportEndOffset -> 0f
                    lastItem.offset + lastItem.size > viewportEndOffset -> lastItem.run {
                        (size - (viewportEndOffset - offset)) / size.toFloat()
                    }
                    else -> 1f
                }
            }.coerceAtMost(1f) * length.value
        }

        drawContent()

        drawRect(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    color,
                    Color.Transparent,
                ),
                startX = 0f,
                endX = startFadingEdgeStrength,
            ),
            size = Size(
                startFadingEdgeStrength,
                this.size.height
            ),
        )

        drawRect(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    Color.Transparent,
                    color,
                ),
                startX = size.width - endFadingEdgeStrength,
                endX = size.width,
            ),
            topLeft = Offset(x = size.width - endFadingEdgeStrength, y = 0f),
        )
    }
}