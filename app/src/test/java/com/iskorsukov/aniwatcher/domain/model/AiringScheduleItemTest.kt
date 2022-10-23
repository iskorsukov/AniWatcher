package com.iskorsukov.aniwatcher.domain.model

import com.google.common.truth.Truth.assertThat
import com.iskorsukov.aniwatcher.domain.model.ModelTestData.AIRING_SCHEDULE
import org.junit.Test

class AiringScheduleItemTest {

    @Test
    fun fromData() {
        val airingScheduleItem = AiringScheduleItem.fromData(AIRING_SCHEDULE)

        assertThat(airingScheduleItem.id).isEqualTo(AIRING_SCHEDULE.id)
        assertThat(airingScheduleItem.airingAt).isEqualTo(AIRING_SCHEDULE.airingAt)
        assertThat(airingScheduleItem.episode).isEqualTo(AIRING_SCHEDULE.episode)
        assertThat(airingScheduleItem.mediaItem).isEqualTo(MediaItem.fromData(AIRING_SCHEDULE.media!!))
    }
}