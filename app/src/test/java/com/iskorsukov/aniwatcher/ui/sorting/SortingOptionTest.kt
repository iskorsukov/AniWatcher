package com.iskorsukov.aniwatcher.ui.sorting

import com.google.common.truth.Truth.assertThat
import com.iskorsukov.aniwatcher.test.ModelTestDataCreator
import com.iskorsukov.aniwatcher.test.meanScore
import com.iskorsukov.aniwatcher.test.nextEpisodeAiringAt
import com.iskorsukov.aniwatcher.test.popularity
import org.junit.Test

class SortingOptionTest {

    @Test
    fun airingAt_smaller() {
        val first = ModelTestDataCreator.baseMediaItem().nextEpisodeAiringAt(1)
        val second = ModelTestDataCreator.baseMediaItem().nextEpisodeAiringAt(4)

        val comparator = SortingOption.AIRING_AT.comparator

        assertThat(comparator.compare(first, second)).isEqualTo(-3)
    }

    @Test
    fun airingAt_equal() {
        val first = ModelTestDataCreator.baseMediaItem().nextEpisodeAiringAt(1)
        val second = ModelTestDataCreator.baseMediaItem().nextEpisodeAiringAt(1)

        val comparator = SortingOption.AIRING_AT.comparator

        assertThat(comparator.compare(first, second)).isEqualTo(-1)
    }

    @Test
    fun airingAt_bigger() {
        val first = ModelTestDataCreator.baseMediaItem().nextEpisodeAiringAt(4)
        val second = ModelTestDataCreator.baseMediaItem().nextEpisodeAiringAt(1)

        val comparator = SortingOption.AIRING_AT.comparator

        assertThat(comparator.compare(first, second)).isEqualTo(3)
    }

    @Test
    fun popularity_smaller() {
        val first = ModelTestDataCreator.baseMediaItem().popularity(1)
        val second = ModelTestDataCreator.baseMediaItem().popularity(4)

        val comparator = SortingOption.POPULARITY.comparator

        assertThat(comparator.compare(first, second)).isEqualTo(-3)
    }

    @Test
    fun popularity_equal() {
        val first = ModelTestDataCreator.baseMediaItem().popularity(1)
        val second = ModelTestDataCreator.baseMediaItem().popularity(1)

        val comparator = SortingOption.POPULARITY.comparator

        assertThat(comparator.compare(first, second)).isEqualTo(-1)
    }

    @Test
    fun popularity_bigger() {
        val first = ModelTestDataCreator.baseMediaItem().popularity(4)
        val second = ModelTestDataCreator.baseMediaItem().popularity(1)

        val comparator = SortingOption.POPULARITY.comparator

        assertThat(comparator.compare(first, second)).isEqualTo(3)
    }

    @Test
    fun meanScore_smaller() {
        val first = ModelTestDataCreator.baseMediaItem().meanScore(1)
        val second = ModelTestDataCreator.baseMediaItem().meanScore(4)

        val comparator = SortingOption.SCORE.comparator

        assertThat(comparator.compare(first, second)).isEqualTo(3)
    }

    @Test
    fun meanScore_equal() {
        val first = ModelTestDataCreator.baseMediaItem().meanScore(1)
        val second = ModelTestDataCreator.baseMediaItem().meanScore(1)

        val comparator = SortingOption.SCORE.comparator

        assertThat(comparator.compare(first, second)).isEqualTo(-1)
    }

    @Test
    fun meanScore_bigger() {
        val first = ModelTestDataCreator.baseMediaItem().meanScore(4)
        val second = ModelTestDataCreator.baseMediaItem().meanScore(1)

        val comparator = SortingOption.SCORE.comparator

        assertThat(comparator.compare(first, second)).isEqualTo(-3)
    }
}