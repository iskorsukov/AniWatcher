package com.iskorsukov.aniwatcher.ui.base.text

import android.graphics.Typeface
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.text.*
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.TextUnit
import androidx.core.text.HtmlCompat

@Composable
fun HtmlText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle(),
    overflow: TextOverflow = TextOverflow.Ellipsis,
) {
    Text(
        text = HtmlCompat.fromHtml(
            text,
            HtmlCompat.FROM_HTML_MODE_COMPACT
        ).toAnnotatedString(),
        style = style,
        overflow = overflow,
        modifier = modifier
    )
}

@Composable
fun TextEllipsisFixed(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    onTextLayout: (TextLayoutResult) -> Unit = { },
    style: TextStyle = LocalTextStyle.current,
) {
    SubcomposeLayout(modifier = modifier) { constraints ->
        var slotId = 0
        fun placeText(
            text: AnnotatedString,
            onTextLayout: (TextLayoutResult) -> Unit,
            constraints: Constraints,
            maxLines: Int,
        ) = subcompose(slotId++) {
            Text(
                text = text,
                color = color,
                fontSize = fontSize,
                fontStyle = fontStyle,
                fontWeight = fontWeight,
                fontFamily = fontFamily,
                letterSpacing = letterSpacing,
                textDecoration = textDecoration,
                textAlign = textAlign,
                lineHeight = lineHeight,
                softWrap = softWrap,
                onTextLayout = onTextLayout,
                style = style,
                overflow = TextOverflow.Ellipsis,
                maxLines = maxLines,
            )
        }[0].measure(constraints)
        var textLayoutResult: TextLayoutResult? = null
        val initialPlaceable = placeText(
            text = text,
            constraints = constraints,
            onTextLayout = {
                textLayoutResult = it
            },
            maxLines = maxLines,
        )
        val finalPlaceable = textLayoutResult?.let { layoutResult ->
            if (!layoutResult.didOverflowHeight) return@let initialPlaceable
            val lastVisibleLine = (0 until layoutResult.lineCount)
                .last {
                    layoutResult.getLineBottom(it) <= layoutResult.size.height
                }
            placeText(
                text = text,
                constraints = constraints,
                onTextLayout = onTextLayout,
                maxLines = lastVisibleLine + 1,
            )
        } ?: initialPlaceable

        layout(
            width = finalPlaceable.width,
            height = finalPlaceable.height
        ) {
            finalPlaceable.place(0, 0)
        }
    }
}

private fun Spanned.toAnnotatedString(): AnnotatedString = buildAnnotatedString {
    val spanned = this@toAnnotatedString
    append(spanned.toString())
    getSpans(0, spanned.length, Any::class.java).forEach { span ->
        val start = getSpanStart(span)
        val end = getSpanEnd(span)
        when (span) {
            is StyleSpan -> when (span.style) {
                Typeface.BOLD -> addStyle(SpanStyle(fontWeight = FontWeight.Bold), start, end)
                Typeface.ITALIC -> addStyle(SpanStyle(fontStyle = FontStyle.Italic), start, end)
                Typeface.BOLD_ITALIC -> addStyle(
                    SpanStyle(
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic
                    ), start, end
                )
            }
            is UnderlineSpan -> addStyle(
                SpanStyle(textDecoration = TextDecoration.Underline),
                start,
                end
            )
            is ForegroundColorSpan -> addStyle(
                SpanStyle(color = Color(span.foregroundColor)),
                start,
                end
            )
        }
    }
}