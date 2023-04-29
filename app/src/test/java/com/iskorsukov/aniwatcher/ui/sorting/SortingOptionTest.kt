package com.iskorsukov.aniwatcher.ui.sorting

import com.google.common.truth.Truth.assertThat
import com.iskorsukov.aniwatcher.test.*
import com.iskorsukov.aniwatcher.ui.base.sorting.SortingOption
import org.junit.Test

class SortingOptionTest {

    @Test
    fun airingAt_smaller() {
        val first = Pair(
            ModelTestDataCreator.mediaItem(id = 1),
            ModelTestDataCreator.airingScheduleItem(id = 1, airingAt = 1)
        )
        val second = Pair(
            ModelTestDataCreator.mediaItem(id = 2),
            ModelTestDataCreator.airingScheduleItem(id = 2, airingAt = 4)
        )

        val comparator = SortingOption.AIRING_AT.comparator

        assertThat(comparator.compare(first, second)).isEqualTo(-3)
    }

    @Test
    fun airingAt_equal() {
        val first = Pair(
            ModelTestDataCreator.mediaItem(id = 1),
            ModelTestDataCreator.airingScheduleItem(id = 1, airingAt = 1)
        )
        val second = Pair(
            ModelTestDataCreator.mediaItem(id = 2),
            ModelTestDataCreator.airingScheduleItem(id = 2, airingAt = 1)
        )

        val comparator = SortingOption.AIRING_AT.comparator

        assertThat(comparator.compare(first, second)).isEqualTo(-1)
    }

    @Test
    fun airingAt_bigger() {
        val first = Pair(
            ModelTestDataCreator.mediaItem(id = 1),
            ModelTestDataCreator.airingScheduleItem(id = 1, airingAt = 4)
        )
        val second = Pair(
            ModelTestDataCreator.mediaItem(id = 2),
            ModelTestDataCreator.airingScheduleItem(id = 2, airingAt = 1)
        )

        val comparator = SortingOption.AIRING_AT.comparator

        assertThat(comparator.compare(first, second)).isEqualTo(3)
    }

    @Test
    fun popularity_smaller() {
        val first = Pair(
            ModelTestDataCreator.mediaItem(id = 1, popularity = 1),
            ModelTestDataCreator.airingScheduleItem(id = 1)
        )
        val second = Pair(
            ModelTestDataCreator.mediaItem(id = 2, popularity = 4),
            ModelTestDataCreator.airingScheduleItem(id = 2)
        )

        val comparator = SortingOption.POPULARITY.comparator

        assertThat(comparator.compare(first, second)).isEqualTo(-3)
    }

    @Test
    fun popularity_equal() {
        val first = Pair(
            ModelTestDataCreator.mediaItem(id = 1, popularity = 1),
            ModelTestDataCreator.airingScheduleItem(id = 1)
        )
        val second = Pair(
            ModelTestDataCreator.mediaItem(id = 2, popularity = 1),
            ModelTestDataCreator.airingScheduleItem(id = 2)
        )

        val comparator = SortingOption.POPULARITY.comparator

        assertThat(comparator.compare(first, second)).isEqualTo(-1)
    }

    @Test
    fun popularity_bigger() {
        val first = Pair(
            ModelTestDataCreator.mediaItem(id = 1, popularity = 4),
            ModelTestDataCreator.airingScheduleItem(id = 1)
        )
        val second = Pair(
            ModelTestDataCreator.mediaItem(id = 2, popularity = 1),
            ModelTestDataCreator.airingScheduleItem(id = 2)
        )

        val comparator = SortingOption.POPULARITY.comparator

        assertThat(comparator.compare(first, second)).isEqualTo(3)
    }

    @Test
    fun meanScore_smaller() {
        val first = Pair(
            ModelTestDataCreator.mediaItem(id = 1, meanScore = 1),
            ModelTestDataCreator.airingScheduleItem(id = 1)
        )
        val second = Pair(
            ModelTestDataCreator.mediaItem(id = 2, meanScore = 4),
            ModelTestDataCreator.airingScheduleItem(id = 2)
        )

        val comparator = SortingOption.SCORE.comparator

        assertThat(comparator.compare(first, second)).isEqualTo(3)
    }

    @Test
    fun meanScore_equal() {
        val first = Pair(
            ModelTestDataCreator.mediaItem(id = 1, meanScore = 1),
            ModelTestDataCreator.airingScheduleItem(id = 1)
        )
        val second = Pair(
            ModelTestDataCreator.mediaItem(id = 2, meanScore = 1),
            ModelTestDataCreator.airingScheduleItem(id = 2)
        )

        val comparator = SortingOption.SCORE.comparator

        assertThat(comparator.compare(first, second)).isEqualTo(-1)
    }

    @Test
    fun meanScore_bigger() {
        val first = Pair(
            ModelTestDataCreator.mediaItem(id = 1, meanScore = 4),
            ModelTestDataCreator.airingScheduleItem(id = 1)
        )
        val second = Pair(
            ModelTestDataCreator.mediaItem(id = 2, meanScore = 1),
            ModelTestDataCreator.airingScheduleItem(id = 2)
        )

        val comparator = SortingOption.SCORE.comparator

        assertThat(comparator.compare(first, second)).isEqualTo(-3)
    }
}