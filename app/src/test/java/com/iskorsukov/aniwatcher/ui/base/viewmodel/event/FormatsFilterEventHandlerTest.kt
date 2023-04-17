package com.iskorsukov.aniwatcher.ui.base.viewmodel.event

import com.google.common.truth.Truth.assertThat
import com.iskorsukov.aniwatcher.domain.model.MediaItem
import com.iskorsukov.aniwatcher.ui.airing.AiringUiState
import org.junit.Test

class FormatsFilterEventHandlerTest {

    private val formatsFilterEventHandler: FormatsFilterEventHandler<AiringUiState> = FormatsFilterEventHandler()

    @Test
    fun handleEvent_formatsFilterSelectionUpdated() {
        val deselectedFormats = listOf(MediaItem.LocalFormat.TV, MediaItem.LocalFormat.MOVIE)
        val uiState = formatsFilterEventHandler.handleEvent(
            FormatsFilterSelectionUpdatedInputEvent(deselectedFormats),
            AiringUiState.DEFAULT
        )
        assertThat(uiState.deselectedFormats).isEqualTo(deselectedFormats)
    }
}