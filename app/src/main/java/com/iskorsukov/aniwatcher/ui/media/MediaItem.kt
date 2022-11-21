package com.iskorsukov.aniwatcher.ui.media

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.iskorsukov.aniwatcher.R
import com.iskorsukov.aniwatcher.domain.model.AiringScheduleItem
import com.iskorsukov.aniwatcher.domain.model.MediaItem
import com.iskorsukov.aniwatcher.ui.theme.ContentTextStyleSmall
import com.iskorsukov.aniwatcher.ui.theme.ContentTextStyleSmallLarger
import com.iskorsukov.aniwatcher.ui.theme.TextColorLight
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
        modifier = modifier
    )
}

@Composable
fun MediaItemAiringInfoColumn(
    airingScheduleItem: AiringScheduleItem?,
    modifier: Modifier = Modifier,
    timeInMinutes: Long = 0L,
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
                style = ContentTextStyleSmall
            )
            airingScheduleItem.getAiringInFormatted(timeInMinutes)?.let {
                Text(
                    text = it,
                    style = ContentTextStyleSmallLarger
                )
            }
            Text(
                text = "at ${airingScheduleItem.getAiringAtDateTimeFormatted()}",
                style = ContentTextStyleSmall
            )
        }
    }
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
                tint = Color.Red
            )
        } else {
            Icon(
                painter = painterResource(id = R.drawable.ic_baseline_add_circle_outline_24_gray),
                contentDescription = null,
                tint = TextColorLight
            )
        }
    }
}

@Composable
fun MediaItemGenresFooter(
    genres: List<String>,
    colorStr: String?,
    modifier: Modifier
) {
    LazyRow(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
    ) {
        genres.take(3).map {
            item {
                GenreChip(genre = it, colorStr = colorStr)
            }
        }
    }
}

@Composable
fun GenreChip(genre: String, colorStr: String?) {
    val bgColor = getBackgroundColorForChip(bgColorStr = colorStr)
    val textColor = getContrastTextColorForChip(bgColor = bgColor)
    Text(
        text = genre,
        color = textColor,
        fontSize = 8.sp,
        modifier = Modifier
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