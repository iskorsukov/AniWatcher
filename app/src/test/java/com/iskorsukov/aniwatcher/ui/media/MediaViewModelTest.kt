package com.iskorsukov.aniwatcher.ui.media

import com.google.common.truth.Truth
import com.iskorsukov.aniwatcher.domain.airing.AiringRepository
import com.iskorsukov.aniwatcher.domain.model.AiringScheduleItem
import com.iskorsukov.aniwatcher.domain.model.MediaItem
import com.iskorsukov.aniwatcher.test.*
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class MediaViewModelTest {
    private val airingRepository: AiringRepository = mockk(relaxed = true)

    private val viewModel = MediaViewModel(airingRepository)

    @Test
    fun mediaFlow() = runTest {
        coEvery { airingRepository.mediaWithSchedulesFlow } returns flow {
            emit(
                mapOf(
                    ModelTestDataCreator.baseMediaItem() to
                            ModelTestDataCreator.baseAiringScheduleItemList()
                )
            )
        }

        var result: Map<MediaItem, AiringScheduleItem?>? = null
        viewModel.mediaFlow.collectLatest { result = it }

        Truth.assertThat(result).isNotNull()
        Truth.assertThat(result!!.size).isEqualTo(1)
        Truth.assertThat(result!!.keys).containsExactly(
            ModelTestDataCreator.baseMediaItem())
        Truth.assertThat(result!!.values).containsExactly(
            ModelTestDataCreator.baseAiringScheduleItem().id(4).episode(4).airingAt(1667029460))
    }
}