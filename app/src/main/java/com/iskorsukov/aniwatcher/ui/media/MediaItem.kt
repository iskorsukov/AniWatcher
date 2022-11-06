package com.iskorsukov.aniwatcher.ui.media

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil.compose.AsyncImage
import com.iskorsukov.aniwatcher.R
import com.iskorsukov.aniwatcher.domain.model.AiringScheduleItem
import com.iskorsukov.aniwatcher.domain.model.MediaItem
import com.iskorsukov.aniwatcher.ui.theme.CardFooterBackgroundColor
import com.iskorsukov.aniwatcher.ui.theme.CardTextColorLight
import com.iskorsukov.aniwatcher.ui.theme.TitleOverlayColor
import com.iskorsukov.aniwatcher.ui.util.getBackgroundColorForChip
import com.iskorsukov.aniwatcher.ui.util.getContrastTextColorForChip

@Composable
fun MediaItemCardExtended(
    mediaItem: MediaItem, airingScheduleItem: AiringScheduleItem?, timeInMinutes: Long,  onFollowClicked: (MediaItem) -> Unit
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .height(200.dp)
            .fillMaxWidth(),
        elevation = 10.dp
    ) {
        ConstraintLayout {
            val (image, titleOverlay, cardContent, genresFooter, followButton) = createRefs()
            val imageEndGuideline = createGuidelineFromStart(0.35f)
            val titleOverlayTopGuideline = createGuidelineFromBottom(0.25f)
            val genresFooterTopGuideline = createGuidelineFromBottom(0.15f)

            AsyncImage(
                model = mediaItem.coverImageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.constrainAs(image) {
                    start.linkTo(parent.start)
                    end.linkTo(imageEndGuideline)

                    height = Dimension.matchParent
                    width = Dimension.fillToConstraints
                }
            )

            Column(
                modifier = Modifier
                    .background(color = TitleOverlayColor)
                    .padding(4.dp)
                    .constrainAs(titleOverlay) {
                        top.linkTo(titleOverlayTopGuideline)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(image.start)
                        end.linkTo(image.end)

                        width = Dimension.fillToConstraints
                        height = Dimension.fillToConstraints
                    },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = mediaItem.title.baseText(),
                    color = Color.White,
                    fontSize = 12.sp,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .constrainAs(cardContent) {
                        top.linkTo(parent.top)
                        bottom.linkTo(genresFooter.top)
                        start.linkTo(image.end)
                        end.linkTo(parent.end)

                        width = Dimension.fillToConstraints
                        height = Dimension.fillToConstraints
                    }
            ) {
                if (airingScheduleItem != null) {
                    Text(text = "Episode ${airingScheduleItem.episode} airing in", color = CardTextColorLight, fontSize = 10.sp)
                    Text(text = airingScheduleItem.getAiringInFormatted(timeInMinutes), color = CardTextColorLight, fontSize = 12.sp)
                    Text(text = "at ${airingScheduleItem.getAiringAtFormatted()}", color = CardTextColorLight, fontSize = 10.sp)
                    Spacer(modifier = Modifier.height(2.dp))
                }
                Text(text = mediaItem.description.orEmpty(), color = CardTextColorLight, fontSize = 10.sp, overflow = TextOverflow.Ellipsis)
            }
            LazyRow(
                modifier = Modifier
                    .background(color = CardFooterBackgroundColor)
                    .constrainAs(genresFooter) {
                        top.linkTo(genresFooterTopGuideline)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(image.end)
                        end.linkTo(parent.end)

                        width = Dimension.fillToConstraints
                        height = Dimension.fillToConstraints
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                mediaItem.genres.take(3).map {
                    item {
                        GenreChip(genre = it, colorStr = mediaItem.colorStr)
                    }
                }
            }
            IconButton(
                modifier = Modifier
                    .padding(4.dp)
                    .size(22.dp)
                    .constrainAs(followButton) {
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    },
                onClick = { onFollowClicked(mediaItem) }
            ) {
                if (mediaItem.isFollowing) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_remove_circle_outline_24_red),
                        contentDescription = null,
                        tint = Color.Red
                    )
                } else {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_add_circle_outline_24_gray),
                        contentDescription = null,
                        tint = CardTextColorLight
                    )
                }
            }
        }
    }
}

@Composable
fun MediaItemCardCollapsed(
    airingScheduleItem: AiringScheduleItem, timeInMinutes: Long, onFollowClicked: (MediaItem) -> Unit
) {
    val mediaItem = airingScheduleItem.mediaItem
    Card(
        modifier = Modifier
            .height(100.dp)
            .fillMaxWidth()
            .padding(8.dp),
        elevation = 10.dp
    ) {
        ConstraintLayout {
            val (image, cardContent, followButton) = createRefs()
            val imageEndGuideline = createGuidelineFromStart(0.2f)

            AsyncImage(
                model = mediaItem.coverImageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.constrainAs(image) {
                    start.linkTo(parent.start)
                    end.linkTo(imageEndGuideline)
                    height = Dimension.matchParent
                    width = Dimension.fillToConstraints
                }
            )
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .constrainAs(cardContent) {
                        start.linkTo(imageEndGuideline)
                        end.linkTo(parent.end)
                        width = Dimension.fillToConstraints
                        height = Dimension.matchParent
                    }
            ) {
                Text(
                    text = mediaItem.title.baseText(),
                    color = CardTextColorLight,
                    fontSize = 12.sp,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
                Text(
                    text = "Episode ${airingScheduleItem.episode} airing in",
                    color = CardTextColorLight,
                    fontSize = 10.sp
                )
                Text(
                    text = airingScheduleItem.getAiringInFormatted(timeInMinutes),
                    color = CardTextColorLight,
                    fontSize = 12.sp
                )
                Text(
                    text = "at ${airingScheduleItem.getAiringAtFormatted()}",
                    color = CardTextColorLight,
                    fontSize = 10.sp
                )
            }

            IconButton(modifier = Modifier
                .padding(8.dp)
                .size(22.dp)
                .constrainAs(followButton) {
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                }, onClick = { onFollowClicked(mediaItem) }) {
                if (mediaItem.isFollowing) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_remove_circle_outline_24_red),
                        contentDescription = null,
                        tint = Color.Red
                    )
                } else {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_baseline_add_circle_outline_24_gray),
                        contentDescription = null,
                        tint = CardTextColorLight
                    )
                }
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