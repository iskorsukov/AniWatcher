package com.iskorsukov.aniwatcher.domain.model

import com.google.common.truth.Truth.assertThat
import com.iskorsukov.aniwatcher.domain.model.ModelTestData.MEDIA_ITEM
import com.iskorsukov.aniwatcher.domain.model.ModelTestData.MEDIA_ITEM_FILTER_RANKING
import org.junit.Test

class MediaItemTest {

    @Test
    fun fromData() {
        val mediaItem = MediaItem.fromData(MEDIA_ITEM)

        assertThat(mediaItem.id).isEqualTo(MEDIA_ITEM.id)
        assertThat(mediaItem.title.romaji).isEqualTo(MEDIA_ITEM.title?.romaji)
        assertThat(mediaItem.title.english).isEqualTo(MEDIA_ITEM.title?.english)
        assertThat(mediaItem.title.native).isEqualTo(MEDIA_ITEM.title?.native)
        assertThat(mediaItem.description).isEqualTo(MEDIA_ITEM.description)
        assertThat(mediaItem.coverImageUrl).isEqualTo(MEDIA_ITEM.coverImage?.medium)
        assertThat(mediaItem.colorStr).isEqualTo(MEDIA_ITEM.coverImage?.color)
        assertThat(mediaItem.seasonRanking?.rank).isEqualTo(MEDIA_ITEM.rankings?.get(0)?.rank)
        assertThat(mediaItem.seasonRanking?.season).isEqualTo(MEDIA_ITEM.rankings?.get(0)?.season?.name)
        assertThat(mediaItem.siteUrl).isEqualTo(MEDIA_ITEM.siteUrl)
    }

    @Test
    fun fromData_filteredRanking() {
        val mediaItem = MediaItem.fromData(MEDIA_ITEM_FILTER_RANKING)

        assertThat(mediaItem.seasonRanking).isNull()
    }
}