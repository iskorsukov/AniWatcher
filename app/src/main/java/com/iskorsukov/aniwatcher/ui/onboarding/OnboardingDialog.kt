package com.iskorsukov.aniwatcher.ui.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.iskorsukov.aniwatcher.R
import com.iskorsukov.aniwatcher.domain.settings.DarkModeOption
import com.iskorsukov.aniwatcher.domain.settings.NamingScheme
import com.iskorsukov.aniwatcher.domain.settings.ScheduleType
import com.iskorsukov.aniwatcher.ui.theme.LocalColors
import com.iskorsukov.aniwatcher.ui.theme.LocalTextStyles

@Composable
fun OnboardingDialog(
    onDarkModeOptionSelected: (DarkModeOption) -> Unit,
    onScheduleTypeSelected: (ScheduleType) -> Unit,
    onNamingSchemeSelected: (NamingScheme) -> Unit,
    onDismissRequest: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(dismissOnClickOutside = false)
    ) {
        var screenCount by rememberSaveable {
            mutableStateOf(1)
        }
        Column(
            modifier = Modifier
                .padding(8.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = LocalColors.current.background
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(id = R.string.onboarding_title),
                        style = LocalTextStyles.current.contentMediumEmphasis,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = stringResource(id = R.string.onboarding_settings),
                        style = LocalTextStyles.current.contentMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            when (screenCount) {
                1 -> {
                    SelectAppThemeSurface(
                        onDarkModeOptionSelected = {
                            onDarkModeOptionSelected.invoke(it)
                            screenCount++
                        },
                        modifier = Modifier
                    )
                }
                2 -> {
                    SelectScheduleTypeSurface(
                        onScheduleTypeSelected = {
                            onScheduleTypeSelected.invoke(it)
                            screenCount++
                        },
                        modifier = Modifier
                    )
                }
                3 -> {
                    SelectNamingSchemeSurface(
                        onNamingSchemeSelected = {
                            onNamingSchemeSelected.invoke(it)
                            onDismissRequest.invoke()
                        },
                        modifier = Modifier
                    )
                }
                else -> {
                    onDismissRequest.invoke()
                }
            }
        }
    }
}

@Composable
private fun OnboardingContentSurface(
    modifier: Modifier = Modifier,
    title: String,
    content: @Composable (ColumnScope) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = LocalColors.current.background,
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(8.dp)
        ) {
            Text(
                text = title,
                style = LocalTextStyles.current.contentMediumEmphasis
            )
            content.invoke(this)
        }
    }
}

@Composable
private fun SelectAppThemeSurface(
    modifier: Modifier,
    onDarkModeOptionSelected: (DarkModeOption) -> Unit
) {
    OnboardingContentSurface(
        title = stringResource(id = R.string.onboarding_dark_theme_title),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            DarkModeOptionCard(
                modifier = Modifier
                    .weight(1f),
                label = stringResource(id = R.string.onboarding_dark_theme_light),
                imageResId = R.mipmap.light_screenshot,
                onClick = {
                    onDarkModeOptionSelected.invoke(DarkModeOption.LIGHT)
                }
            )
            DarkModeOptionCard(
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 8.dp),
                label = stringResource(id = R.string.onboarding_dark_theme_dark),
                imageResId = R.mipmap.dark_screenshot,
                onClick = {
                    onDarkModeOptionSelected.invoke(DarkModeOption.DARK)
                }
            )
            DarkModeOptionCard(
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 8.dp),
                label = stringResource(id = R.string.onboarding_dark_theme_system),
                imageResId = R.mipmap.system_screenshot,
                onClick = {
                    onDarkModeOptionSelected.invoke(DarkModeOption.SYSTEM)
                }
            )
        }
    }
}

@Composable
@Preview
private fun SelectAppThemeSurfacePreview() {
    SelectAppThemeSurface(
        modifier = Modifier,
        onDarkModeOptionSelected = { }
    )
}

@Composable
private fun SelectScheduleTypeSurface(
    modifier: Modifier,
    onScheduleTypeSelected: (ScheduleType) -> Unit
) {
    OnboardingContentSurface(
        title = stringResource(id = R.string.onboarding_schedule_type_title),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            ScheduleTypeCard(
                modifier = Modifier
                    .weight(1f),
                label = stringResource(id = R.string.onboarding_schedule_type_all),
                text = stringResource(id = R.string.onboarding_schedule_type_all_desc),
                caption = stringResource(id = R.string.onboarding_schedule_type_all_desc_secondary),
                onClick = {
                    onScheduleTypeSelected.invoke(ScheduleType.ALL)
                }
            )
            ScheduleTypeCard(
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 8.dp),
                label = stringResource(id = R.string.onboarding_schedule_type_season),
                text = stringResource(id = R.string.onboarding_schedule_type_season_desc),
                caption = stringResource(id = R.string.onboarding_schedule_type_season_desc_secondary),
                onClick = {
                    onScheduleTypeSelected.invoke(ScheduleType.SEASON)
                }
            )
        }
    }
}

@Composable
@Preview
private fun SelectScheduleTypeSurfacePreview() {
    SelectScheduleTypeSurface(
        modifier = Modifier,
        onScheduleTypeSelected = { }
    )
}

@Composable
private fun SelectNamingSchemeSurface(
    modifier: Modifier,
    onNamingSchemeSelected: (NamingScheme) -> Unit
) {
    OnboardingContentSurface(
        title = stringResource(id = R.string.onboarding_naming_preference_title),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            NamingSchemeCard(
                modifier = Modifier
                    .weight(1f),
                label = stringResource(id = R.string.onboarding_naming_preference_english),
                imageResId = R.mipmap.naming_scheme_english,
                onClick = {
                    onNamingSchemeSelected.invoke(NamingScheme.ENGLISH)
                }
            )
            NamingSchemeCard(
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 8.dp),
                label = stringResource(id = R.string.onboarding_naming_preference_romaji),
                imageResId = R.mipmap.naming_scheme_romaji,
                onClick = {
                    onNamingSchemeSelected.invoke(NamingScheme.ROMAJI)
                }
            )
            NamingSchemeCard(
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 8.dp),
                label = stringResource(id = R.string.onboarding_naming_preference_native),
                imageResId = R.mipmap.naming_scheme_native,
                onClick = {
                    onNamingSchemeSelected.invoke(NamingScheme.NATIVE)
                }
            )
        }
    }
}

@Composable
@Preview
private fun SelectNamingSchemeSurfacePreview() {
    SelectNamingSchemeSurface(
        modifier = Modifier,
        onNamingSchemeSelected = { }
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun DarkModeOptionCard(
    modifier: Modifier,
    label: String,
    imageResId: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier,
        elevation = 8.dp,
        backgroundColor = LocalColors.current.cardBackground,
        onClick = {
            onClick.invoke()
        }
    ) {
        Box(
            contentAlignment = Alignment.TopCenter
        ) {
            Image(
                painter = painterResource(id = imageResId),
                contentScale = ContentScale.Crop,
                contentDescription = null
            )
            Text(
                text = label,
                style = LocalTextStyles.current.contentMediumEmphasisWhite,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .background(
                        color = LocalColors.current.attentionBackground,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(vertical = 4.dp, horizontal = 8.dp)
            )
        }
    }
}

@Composable
@Preview
private fun DarkModeOptionCardPreview() {
    DarkModeOptionCard(
        modifier = Modifier,
        label = stringResource(id = R.string.onboarding_dark_theme_light),
        imageResId = R.mipmap.light_screenshot,
        onClick = { }
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun ScheduleTypeCard(
    modifier: Modifier,
    label: String,
    text: String,
    caption: String,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier,
        elevation = 8.dp,
        backgroundColor = LocalColors.current.cardBackground,
        onClick = {
            onClick.invoke()
        }
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = LocalTextStyles.current.contentMediumEmphasis,
                textAlign = TextAlign.Center
            )
            Text(
                text = text,
                style = LocalTextStyles.current.contentSmallLarger,
                textAlign = TextAlign.Center
            )
            Text(
                text = caption,
                style = LocalTextStyles.current.contentSmallLargerEmphasis,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
@Preview
private fun ScheduleTypeCardPreview() {
    ScheduleTypeCard(
        modifier = Modifier,
        label = stringResource(id = R.string.onboarding_schedule_type_all),
        text = stringResource(id = R.string.onboarding_schedule_type_all_desc),
        caption = stringResource(id = R.string.onboarding_schedule_type_all_desc_secondary),
        onClick = { }
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun NamingSchemeCard(
    modifier: Modifier,
    label: String,
    imageResId: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier,
        elevation = 8.dp,
        backgroundColor = LocalColors.current.cardBackground,
        onClick = {
            onClick.invoke()
        }
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = label,
                style = LocalTextStyles.current.contentMediumEmphasis,
                modifier = Modifier.padding(horizontal = 6.dp)
            )
            Image(
                painter = painterResource(id = imageResId),
                contentScale = ContentScale.Crop,
                alignment = Alignment.TopCenter,
                contentDescription = null
            )
        }
    }
}

@Composable
@Preview
private fun NamingSchemeCardPreview() {
    NamingSchemeCard(
        modifier = Modifier,
        label = stringResource(id = R.string.onboarding_naming_preference_english),
        imageResId = R.mipmap.naming_scheme_english,
        onClick = { }
    )
}

@Composable
@Preview
private fun OnboardingDialogPreview() {
    OnboardingDialog(
        { },
        { },
        { },
        { }
    )
}