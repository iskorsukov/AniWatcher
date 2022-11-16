package com.iskorsukov.aniwatcher.ui.media

import android.graphics.Typeface
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.core.text.HtmlCompat
import coil.compose.AsyncImage
import com.iskorsukov.aniwatcher.R
import com.iskorsukov.aniwatcher.domain.model.AiringScheduleItem
import com.iskorsukov.aniwatcher.domain.model.MediaItem
import com.iskorsukov.aniwatcher.domain.settings.NamingScheme
import com.iskorsukov.aniwatcher.test.ModelTestDataCreator
import com.iskorsukov.aniwatcher.test.description
import com.iskorsukov.aniwatcher.test.nullMeanScore
import com.iskorsukov.aniwatcher.test.nullRanking
import com.iskorsukov.aniwatcher.ui.theme.*
import com.iskorsukov.aniwatcher.ui.util.getBackgroundColorForChip
import com.iskorsukov.aniwatcher.ui.util.getContrastTextColorForChip

@Composable
fun MediaItemCardExtended(
    mediaItem: MediaItem,
    airingScheduleItem: AiringScheduleItem?,
    timeInMinutes: Long,
    onFollowClicked: (MediaItem) -> Unit,
    onMediaClicked: ((Int) -> Unit)?,
    preferredNamingScheme: NamingScheme = NamingScheme.ENGLISH
) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .height(200.dp)
            .fillMaxWidth()
            .clickable { onMediaClicked?.invoke(mediaItem.id) },
        elevation = 10.dp
    ) {
        ConstraintLayout {
            val (
                image,
                titleOverlay,
                cardContent,
                genresFooter,
                followButton,
                rankScore,
                description
            ) = createRefs()
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
                    text = mediaItem.title.baseText(preferredNamingScheme),
                    color = Color.White,
                    fontSize = 12.sp,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Column(
                modifier = Modifier
                    .padding(start = 8.dp, top = 8.dp, end = 8.dp)
                    .constrainAs(cardContent) {
                        top.linkTo(parent.top)
                        start.linkTo(imageEndGuideline)
                        end.linkTo(rankScore.start)

                        width = Dimension.fillToConstraints
                    }
            ) {
                if (airingScheduleItem != null) {
                    val episodeAiringStr = if (airingScheduleItem.airingAt - (timeInMinutes / 60) < 0) {
                        stringResource(id = R.string.episode_aired_label)
                    } else {
                        stringResource(id = R.string.episode_airing_label)
                    }
                    Text(
                        text = String.format(episodeAiringStr, airingScheduleItem.episode),
                        color = CardTextColorLight,
                        fontSize = 10.sp
                    )
                    airingScheduleItem.getAiringInFormatted(timeInMinutes)?.let {
                        Text(
                            text = it,
                            color = CardTextColorLight,
                            fontSize = 12.sp
                        )
                    }
                    Text(
                        text = "at ${airingScheduleItem.getAiringAtDateTimeFormatted()}",
                        color = CardTextColorLight,
                        fontSize = 10.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                }
            }
            Text(
                text = HtmlCompat.fromHtml(
                    mediaItem.description.orEmpty(),
                    HtmlCompat.FROM_HTML_MODE_COMPACT
                ).toAnnotatedString(),
                color = CardTextColorLight,
                fontSize = 10.sp,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .padding(start = 8.dp, bottom = 8.dp, end = 8.dp)
                    .constrainAs(description) {
                        bottom.linkTo(genresFooter.top)
                        start.linkTo(imageEndGuideline)

                        if (airingScheduleItem == null && mediaItem.seasonRanking == null && mediaItem.meanScore == null) {
                            top.linkTo(cardContent.bottom)
                            end.linkTo(parent.end)
                        } else if (airingScheduleItem != null) {
                            top.linkTo(cardContent.bottom)
                            end.linkTo(parent.end)
                        } else {
                            top.linkTo(cardContent.bottom)
                            end.linkTo(rankScore.start)
                        }

                        width = Dimension.fillToConstraints
                        height = Dimension.fillToConstraints
                    }
            )
            if (mediaItem.seasonRanking != null || mediaItem.meanScore != null) {
                Column(modifier = Modifier
                    .padding(top = 8.dp, end = 8.dp)
                    .constrainAs(rankScore) {
                        top.linkTo(parent.top)
                        end.linkTo(parent.end)
                    }) {
                    if (mediaItem.meanScore != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val faceTint = if (mediaItem.meanScore <= 33) {
                                CardIndicatorColorRed
                            } else if (mediaItem.meanScore <= 66) {
                                CardIndicatorColorOrange
                            } else {
                                CardIndicatorColorGreen
                            }
                            Icon(
                                painter = painterResource(id = R.drawable.ic_outline_tag_faces_24_black),
                                contentDescription = null,
                                tint = faceTint,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${mediaItem.meanScore}%",
                                color = CardTextColorLight,
                                fontSize = 12.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    if (mediaItem.seasonRanking != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_outline_favorite_border_24_white),
                                contentDescription = null,
                                tint = CardIndicatorColorRed,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "#${mediaItem.seasonRanking.rank}",
                                color = CardTextColorLight,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
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
@Preview
fun MediaItemCardExtendedPreview() {
    val timeInMinutes = 27785711L

    MediaItemCardExtended(
        mediaItem = ModelTestDataCreator.baseMediaItem().description("Word ".repeat(50)),
        airingScheduleItem = ModelTestDataCreator.baseAiringScheduleItem(),
        timeInMinutes = timeInMinutes,
        onFollowClicked = {},
        onMediaClicked = {}
    )
}

@Composable
@Preview
fun MediaItemCardExtendedPreview_noAiringSchedule() {
    val timeInMinutes = 27785711L

    MediaItemCardExtended(
        mediaItem = ModelTestDataCreator.baseMediaItem().description("Word ".repeat(50)),
        airingScheduleItem = null,
        timeInMinutes = timeInMinutes,
        onFollowClicked = {},
        onMediaClicked = {}
    )
}

@Composable
@Preview
fun MediaItemCardExtendedPreview_noRankScore() {
    val timeInMinutes = 27785711L

    MediaItemCardExtended(
        mediaItem = ModelTestDataCreator.baseMediaItem().description("Word ".repeat(50))
            .nullRanking().nullMeanScore(),
        airingScheduleItem = ModelTestDataCreator.baseAiringScheduleItem(),
        timeInMinutes = timeInMinutes,
        onFollowClicked = {},
        onMediaClicked = {}
    )
}

@Composable
@Preview
fun MediaItemCardExtendedPreview_noAiringSchedule_noRankScore() {
    val timeInMinutes = 27785711L

    MediaItemCardExtended(
        mediaItem = ModelTestDataCreator.baseMediaItem().description("Word ".repeat(50))
            .nullRanking().nullMeanScore(),
        airingScheduleItem = null,
        timeInMinutes = timeInMinutes,
        onFollowClicked = {},
        onMediaClicked = {}
    )
}

@Composable
fun MediaItemCardCollapsed(
    airingScheduleItem: AiringScheduleItem,
    timeInMinutes: Long,
    onFollowClicked: (MediaItem) -> Unit,
    onMediaClicked: ((Int) -> Unit)?,
    preferredNamingScheme: NamingScheme = NamingScheme.ENGLISH
) {
    val mediaItem = airingScheduleItem.mediaItem
    Card(
        modifier = Modifier
            .height(100.dp)
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onMediaClicked?.invoke(mediaItem.id) },
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
                    text = mediaItem.title.baseText(preferredNamingScheme),
                    color = CardTextColorLight,
                    fontSize = 12.sp,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
                val episodeAiringStr = if (airingScheduleItem.airingAt - timeInMinutes * 60 <= 0) {
                    stringResource(id = R.string.episode_aired_label)
                } else {
                    stringResource(id = R.string.episode_airing_label)
                }
                Text(
                    text = String.format(episodeAiringStr, airingScheduleItem.episode),
                    color = CardTextColorLight,
                    fontSize = 10.sp
                )
                airingScheduleItem.getAiringInFormatted(timeInMinutes)?.let {
                    Text(
                        text = it,
                        color = CardTextColorLight,
                        fontSize = 12.sp
                    )
                }
                Text(
                    text = "at ${airingScheduleItem.getAiringAtDateTimeFormatted()}",
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

fun Spanned.toAnnotatedString(): AnnotatedString = buildAnnotatedString {
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