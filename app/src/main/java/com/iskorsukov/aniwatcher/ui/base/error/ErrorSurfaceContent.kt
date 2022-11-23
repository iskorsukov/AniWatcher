package com.iskorsukov.aniwatcher.ui.base.error

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.iskorsukov.aniwatcher.ui.theme.ErrorButtonTextStyle
import com.iskorsukov.aniwatcher.ui.theme.LocalColors
import com.iskorsukov.aniwatcher.ui.theme.LocalTextStyles
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit

@Composable
fun ErrorSurfaceContent(
    errorItem: ErrorItem,
    onActionClicked: () -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    autoDismissSeconds: Long = 7L
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = LocalColors.current.error,
        modifier = modifier
    ) {
        ConstraintLayout(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            val (
                label,
                subLabel,
                actionButton,
                dismissButton
            ) = createRefs()
            val buttonsGuideline = createGuidelineFromEnd(0.3f)
            Text(
                text = stringResource(id = errorItem.labelResId),
                style = LocalTextStyles.current.errorLabel,
                modifier = Modifier.constrainAs(label) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(buttonsGuideline)

                    width = Dimension.fillToConstraints
                }
            )
            if (errorItem.subLabelResId != null) {
                Text(
                    text = stringResource(id = errorItem.subLabelResId),
                    style = LocalTextStyles.current.errorSubLabel,
                    modifier = Modifier.constrainAs(subLabel) {
                        top.linkTo(label.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(buttonsGuideline)

                        width = Dimension.fillToConstraints
                    }
                )
            }
            createVerticalChain(
                actionButton,
                dismissButton
            )
            if (errorItem.actionLabelResId != null) {
                Text(
                    text = stringResource(id = errorItem.actionLabelResId).uppercase(),
                    style = LocalTextStyles.current.errorButton,
                    modifier = Modifier
                        .clickable {
                            onActionClicked.invoke()
                            onDismissRequest.invoke()
                        }
                        .padding(8.dp)
                        .constrainAs(actionButton) {
                            top.linkTo(parent.top)
                            start.linkTo(buttonsGuideline)
                            end.linkTo(parent.end)

                            width = Dimension.fillToConstraints
                        }
                )
            }
            if (errorItem.dismissLabelResId != null) {
                Text(
                    text = stringResource(id = errorItem.dismissLabelResId).uppercase(),
                    style = ErrorButtonTextStyle,
                    modifier = Modifier
                        .clickable { onDismissRequest.invoke() }
                        .padding(8.dp)
                        .constrainAs(dismissButton) {
                            start.linkTo(buttonsGuideline)
                            end.linkTo(parent.end)

                            width = Dimension.fillToConstraints
                        }
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

@Composable
@Preview
private fun ErrorDialogPreview() {
    ErrorSurfaceContent(
        errorItem = ErrorItem.LoadingData,
        onActionClicked = { },
        onDismissRequest = { },
        autoDismissSeconds = TimeUnit.HOURS.toSeconds(1L)
    )
}