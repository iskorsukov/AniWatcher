package com.iskorsukov.aniwatcher.ui.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.material.Text
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
                .fillMaxSize()
                .padding(16.dp),
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = LocalColors.current.background,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().weight(1f).padding(8.dp),
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
            if (screenCount == 1) {
                SelectAppThemeSurface(
                    onDarkModeOptionSelected = {
                        onDarkModeOptionSelected.invoke(it)
                        screenCount++
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                )
            } else if (screenCount == 2) {
                SelectScheduleTypeSurface(
                    onScheduleTypeSelected = {
                        onScheduleTypeSelected.invoke(it)
                        screenCount++
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                )
            } else if (screenCount == 3) {
                SelectNamingSchemeSurface(
                    onNamingSchemeSelected = {
                        onNamingSchemeSelected.invoke(it)
                        screenCount++
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                )
            } else {
                onDismissRequest.invoke()
            }
        }
    }
}

@Composable
fun SelectAppThemeSurface(
    modifier: Modifier,
    onDarkModeOptionSelected: (DarkModeOption) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = LocalColors.current.background,
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(
                text = stringResource(id = R.string.onboarding_dark_theme_title),
                style = LocalTextStyles.current.contentMediumEmphasis
            )
            Column(modifier = Modifier.padding(8.dp)) {
                DarkModeOptionCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    label = stringResource(id = R.string.onboarding_dark_theme_light),
                    imageResId = R.mipmap.light_screenshot,
                    onClick = {
                        onDarkModeOptionSelected.invoke(DarkModeOption.LIGHT)
                    }
                )
                DarkModeOptionCard(
                    modifier = Modifier
                        .fillMaxWidth()
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
                        .fillMaxWidth()
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
}

@Composable
fun SelectScheduleTypeSurface(
    modifier: Modifier,
    onScheduleTypeSelected: (ScheduleType) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = LocalColors.current.background,
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(
                text = stringResource(id = R.string.onboarding_schedule_type_title),
                style = LocalTextStyles.current.contentMediumEmphasis
            )
            Column {
                ScheduleTypeCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, start = 8.dp, end = 8.dp),
                    label = stringResource(id = R.string.onboarding_schedule_type_all),
                    text = stringResource(id = R.string.onboarding_schedule_type_all_desc),
                    onClick = {
                        onScheduleTypeSelected.invoke(ScheduleType.ALL)
                    }
                )
                ScheduleTypeCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    label = stringResource(id = R.string.onboarding_schedule_type_season),
                    text = stringResource(id = R.string.onboarding_schedule_type_season_desc),
                    onClick = {
                        onScheduleTypeSelected.invoke(ScheduleType.SEASON)
                    }
                )
            }
        }
    }
}

@Composable
fun SelectNamingSchemeSurface(
    modifier: Modifier,
    onNamingSchemeSelected: (NamingScheme) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = LocalColors.current.background,
        modifier = modifier
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(
                text = stringResource(id = R.string.onboarding_naming_preference_title),
                style = LocalTextStyles.current.contentMediumEmphasis
            )
            Column {
                NamingSchemeCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(vertical = 8.dp),
                    label = stringResource(id = R.string.onboarding_naming_preference_english),
                    imageResId = R.mipmap.naming_scheme_english,
                    onClick = {
                        onNamingSchemeSelected.invoke(NamingScheme.ENGLISH)
                    }
                )
                NamingSchemeCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(vertical = 4.dp),
                    label = stringResource(id = R.string.onboarding_naming_preference_romaji),
                    imageResId = R.mipmap.naming_scheme_romaji,
                    onClick = {
                        onNamingSchemeSelected.invoke(NamingScheme.ROMAJI)
                    }
                )
                NamingSchemeCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(vertical = 8.dp),
                    label = stringResource(id = R.string.onboarding_naming_preference_native),
                    imageResId = R.mipmap.naming_scheme_native,
                    onClick = {
                        onNamingSchemeSelected.invoke(NamingScheme.NATIVE)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun DarkModeOptionCard(
    modifier: Modifier,
    label: String,
    imageResId: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier,
        elevation = 4.dp,
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun ScheduleTypeCard(
    modifier: Modifier,
    label: String,
    text: String,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier,
        elevation = 4.dp,
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
                style = LocalTextStyles.current.contentMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NamingSchemeCard(
    modifier: Modifier,
    label: String,
    imageResId: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier,
        elevation = 4.dp,
        backgroundColor = LocalColors.current.cardBackground,
        onClick = {
            onClick.invoke()
        }
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = label,
                style = LocalTextStyles.current.contentMediumEmphasis,
            )
            Image(
                painter = painterResource(id = imageResId),
                contentScale = ContentScale.Crop,
                contentDescription = null
            )
        }
    }
}

@Composable
@Preview
fun OnboardingDialogPreview() {
    OnboardingDialog(
        { },
        { },
        { },
        { }
    )
}