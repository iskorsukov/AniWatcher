package com.iskorsukov.aniwatcher.ui.base.viewmodel.event

import com.google.common.truth.Truth.assertThat
import com.iskorsukov.aniwatcher.domain.model.MediaItem
import com.iskorsukov.aniwatcher.ui.airing.AiringUiState
import org.junit.Test

class ResetStateEventHandlerTest {

    private val resetStateEventHandler: ResetStateEventHandler<AiringUiState> = ResetStateEventHandler()

    @Test
    fun handleEvent_resetStateTriggered() {
        val deselectedFormats = listOf(MediaItem.LocalFormat.TV, MediaItem.LocalFormat.MOVIE)

        val uiState = resetStateEventHandler.handleEvent(
            ResetStateTriggeredInputEvent,
            AiringUiState.DEFAULT.copy(deselectedFormats = deselectedFormats)
        )
        assertThat(uiState).isEqualTo(AiringUiState.DEFAULT)
    }
}