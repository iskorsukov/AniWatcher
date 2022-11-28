package com.iskorsukov.aniwatcher.ui.media

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.iskorsukov.aniwatcher.R
import com.iskorsukov.aniwatcher.domain.model.AiringScheduleItem
import com.iskorsukov.aniwatcher.test.ModelTestDataCreator
import com.iskorsukov.aniwatcher.ui.theme.LocalColors
import com.iskorsukov.aniwatcher.ui.theme.LocalTextStyles
import com.iskorsukov.aniwatcher.ui.util.getBackgroundColorForChip
import com.iskorsukov.aniwatcher.ui.util.getContrastTextColorForChip

@Composable
fun MediaItemImage(
    imageUrl: String?,
    modifier: Modifier
) {
    AsyncImage(
        model = imageUrl,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier.background(LocalColors.current.primary)
    )
}

@Composable
@Preview
private fun MediaItemImagePreview() {
    MediaItemImage(
        imageUrl = "https://s4.anilist.co/file/anilistcdn/media/anime/cover/large/bx142838-ECZSqfknAqAT.jpg",
        modifier = Modifier.size(42.dp)
    )
}

@Composable
@Preview
private fun MediaItemImageErrorPreview() {
    MediaItemImage(
        imageUrl = "https://s4.anilist.co/",
        modifier = Modifier.size(42.dp)
    )
}

@Composable
fun MediaItemAiringInfoColumn(
    airingScheduleItem: AiringScheduleItem?,
    modifier: Modifier = Modifier,
    timeInMinutes: Long,
) {
    Column(modifier = modifier) {
        if (airingScheduleItem != null) {
            val episodeAiringStr = if (airingScheduleItem.airingAt - timeInMinutes * 60 <= 0) {
                stringResource(id = R.string.episode_aired_label)
            } else {
                stringResource(id = R.string.episode_airing_label)
            }
            Text(
                text = String.format(episodeAiringStr, airingScheduleItem.episode),
                style = LocalTextStyles.current.contentSmallLarger
            )
            airingScheduleItem.getAiringInFormatted(timeInMinutes)?.let {
                Text(
                    text = it,
                    style = LocalTextStyles.current.contentSmallLargerEmphasis
                )
            }
            Text(
                text = String.format(
                    stringResource(id = R.string.episode_airing_at),
                    airingScheduleItem.getAiringAtDateTimeFormatted()
                ),
                style = LocalTextStyles.current.contentSmallLarger
            )
        }
    }
}

@Composable
@Preview
private fun MediaItemAiringInfoColumnPreview() {
    MediaItemAiringInfoColumn(
        airingScheduleItem = ModelTestDataCreator.baseAiringScheduleItem(),
        timeInMinutes = ModelTestDataCreator.TIME_IN_MINUTES
    )
}


@Composable
fun MediaItemFollowButton(
    isFollowing: Boolean,
    modifier: Modifier,
    onFollowClicked: () -> Unit
) {
    IconButton(
        modifier = modifier,
        onClick = onFollowClicked
    ) {
        if (isFollowing) {
            Icon(
                painter = painterResource(id = R.drawable.ic_baseline_remove_circle_outline_24_red),
                contentDescription = null,
                tint = LocalColors.current.error
            )
        } else {
            Icon(
                painter = painterResource(id = R.drawable.ic_baseline_add_circle_outline_24_gray),
                contentDescription = null,
                tint = LocalColors.current.text
            )
        }
    }
}

@Composable
@Preview
private fun MediaItemFollowButtonPreview() {
    val isFollowing = remember { mutableStateOf(false) }
    MediaItemFollowButton(
        isFollowing = isFollowing.value,
        modifier = Modifier.size(24.dp)
    ) {
        isFollowing.value = !isFollowing.value
    }
}

@Composable
fun MediaItemGenresFooter(
    genres: List<String>,
    modifier: Modifier = Modifier,
    colorStr: String? = null,
    onGenreChipClicked: ((String) -> Unit)? = null
) {
    val state = rememberLazyListState()
    val visibleChipsCount: State<Int> = remember {
        derivedStateOf {
            val layoutInfo = state.layoutInfo
            val visibleItemsInfo = layoutInfo.visibleItemsInfo
            visibleItemsInfo.count {
                it.offset + it.size < layoutInfo.viewportStartOffset + layoutInfo.viewportEndOffset
            }
        }
    }
    LazyRow(
        state = state,
        modifier = modifier,
        userScrollEnabled = false,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start,
    ) {
        genres.mapIndexed { index, genre ->
            item {
                GenreChip(
                    genre = genre,
                    colorStr = colorStr,
                    isVisible = index < visibleChipsCount.value,
                    onGenreChipClicked = onGenreChipClicked
                )
            }
        }
    }
}

@Composable
@Preview
private fun MediaItemGenresFooterPreview() {
    MediaItemGenresFooter(
        genres = listOf(
            "Action",
            "Comedy",
            "Supernatural",
            "Slice of life",
            "Paranormal",
            "Last"
        ),
        modifier = Modifier.width(200.dp)
    )
}

@Composable
private fun GenreChip(
    genre: String,
    colorStr: String?,
    isVisible: Boolean = true,
    onGenreChipClicked: ((String) -> Unit)? = null
) {
    val bgColor = if (isVisible) {
        getBackgroundColorForChip(bgColorStr = colorStr)
    } else {
        Color.Transparent
    }
    val textColor = getContrastTextColorForChip(bgColor = bgColor)
    Text(
        text = genre,
        color = textColor,
        fontSize = 10.sp,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .clickable {
                if (isVisible) {
                    onGenreChipClicked?.invoke(genre)
                }
            }
            .padding(
                top = 4.dp,
                bottom = 4.dp,
                start = 4.dp
            )
            .background(
                shape = RoundedCornerShape(10.dp),
                color = bgColor
            )
            .padding(4.dp)
    )
}

@Composable
@Preview
private fun GenreChipPreview() {
    GenreChip(genre = "Genre", colorStr = "#b3d0ff")
}

@Composable
fun MediaFormatText(
    text: String,
    modifier: Modifier = Modifier,
    isRounded: Boolean = true
) {
    Text(
        text = text,
        textAlign = TextAlign.Center,
        style = LocalTextStyles.current.contentSmallLargerWhite,
        modifier = modifier
            .background(
                LocalColors.current.attentionBackground,
                if (isRounded) RoundedCornerShape(8.dp) else RectangleShape
            )
            .padding(horizontal = 8.dp)
    )
}

@Composable
@Preview
private fun MediaFormatTextPreview() {
    MediaFormatText(text = "TV")
}

@Composable
fun MediaItemIndicatorWithText(
    @DrawableRes iconResId: Int,
    iconTint: Color,
    text: String,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        Icon(
            painter = painterResource(id = iconResId),
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            style = LocalTextStyles.current.contentSmallLarger
        )
    }
}


@Composable
@Preview
private fun MediaItemIndicatorWithTextPreview() {
    MediaItemIndicatorWithText(
        iconResId = R.drawable.ic_baseline_thumb_up_off_alt_24,
        iconTint = Color.Blue,
        text = "45%"
    )
}