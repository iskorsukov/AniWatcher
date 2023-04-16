package com.iskorsukov.aniwatcher.ui.base.permission

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.iskorsukov.aniwatcher.R
import com.iskorsukov.aniwatcher.ui.theme.LocalColors
import com.iskorsukov.aniwatcher.ui.theme.LocalTextStyles

@Composable
fun PermissionRationaleDialog(
    @StringRes titleResId: Int,
    @StringRes textResId: Int,
    @StringRes grantTextResId: Int = R.string.permission_grant,
    @StringRes denyTextResId: Int = R.string.permission_deny,
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit,
    onDismissRequest: () -> Unit
) {
    val properties = DialogProperties(dismissOnClickOutside = false)
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = properties
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = LocalColors.current.background
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(8.dp)
            ) {
                Text(
                    text = stringResource(id = titleResId),
                    style = LocalTextStyles.current.headlineEmphasis,
                )
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = stringResource(id = textResId),
                    style = LocalTextStyles.current.contentMedium,
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(id = grantTextResId).uppercase(),
                    style = LocalTextStyles.current.contentMediumEmphasis,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .clickable {
                            onPermissionGranted.invoke()
                            onDismissRequest.invoke()
                        }
                        .fillMaxWidth()
                        .padding(8.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(id = denyTextResId).uppercase(),
                    style = LocalTextStyles.current.contentMediumEmphasis,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .clickable {
                            onPermissionDenied.invoke()
                            onDismissRequest.invoke()
                        }
                        .fillMaxWidth()
                        .padding(8.dp)
                )
            }
        }
    }
}

@Preview
@Composable
fun PermissionRationaleDialogPreview() {
    PermissionRationaleDialog(
        titleResId = R.string.notifications_permission_title,
        textResId = R.string.notifications_permission_text,
        onPermissionGranted = { },
        onPermissionDenied = { },
        onDismissRequest = { },
    )
}