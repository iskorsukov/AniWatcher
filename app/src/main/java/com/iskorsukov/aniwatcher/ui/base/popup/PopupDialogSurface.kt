package com.iskorsukov.aniwatcher.ui.base.popup

import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.iskorsukov.aniwatcher.ui.theme.LocalColors
import com.iskorsukov.aniwatcher.ui.theme.LocalTextStyles
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit

@Composable
fun PopupDialogSurface(
    modifier: Modifier = Modifier,
    @StringRes labelResId: Int,
    @StringRes subLabelResId: Int? = null,
    @StringRes actionLabelResId: Int? = null,
    @StringRes dismissLabelResId: Int? = null,
    backgroundColor: Color = LocalColors.current.primary,
    onActionClicked: () -> Unit,
    onDismissRequest: () -> Unit,
    autoDismissSeconds: Long = 7L
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = backgroundColor,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.height(IntrinsicSize.Min).padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(7f)
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                Text(
                    text = stringResource(id = labelResId),
                    style = LocalTextStyles.current.popupMessageLabel,
                    modifier = Modifier
                )
                if (subLabelResId != null) {
                    Text(
                        text = stringResource(id = subLabelResId),
                        style = LocalTextStyles.current.popupMessageSubLabel,
                    )
                }
            }
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .weight(3f)
                    .fillMaxHeight()
            ) {
                if (actionLabelResId != null) {
                    Text(
                        text = stringResource(id = actionLabelResId).uppercase(),
                        style = LocalTextStyles.current.popupMessageButton,
                        modifier = Modifier
                            .clickable {
                                onActionClicked.invoke()
                                onDismissRequest.invoke()
                            }
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                }
                if (dismissLabelResId != null) {
                    Text(
                        text = stringResource(id = dismissLabelResId).uppercase(),
                        style = LocalTextStyles.current.popupMessageButton,
                        modifier = Modifier
                            .clickable { onDismissRequest.invoke() }
                            .fillMaxWidth()
                            .padding(8.dp)
                    )
                } else {
                    LaunchedEffect(Unit) {
                        delay(TimeUnit.SECONDS.toMillis(autoDismissSeconds))
                        onDismissRequest.invoke()
                    }
                }
            }
        }
    }
}