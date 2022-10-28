package com.iskorsukov.aniwatcher.domain.model

import com.google.common.truth.Truth.assertThat
import com.iskorsukov.aniwatcher.*
import org.junit.Test

class MediaItemTest {

    @Test
    fun fromEntity() {
        val mediaItemEntity = EntityTestDataCreator.baseMediaItemEntity()
        val followingEntity = EntityTestDataCreator.baseFollowingEntity()

        val mediaItem = MediaItem.fromEntity(mediaItemEntity, followingEntity)

        assertThat(mediaItem).isEqualTo(ModelTestDataCreator.baseMediaItem().isFollowing(true))
    }

    @Test
    fun fromEntity_noFollowingEntity() {
        val mediaItemEntity = EntityTestDataCreator.baseMediaItemEntity()

        val mediaItem = MediaItem.fromEntity(mediaItemEntity, null)

        assertThat(mediaItem).isEqualTo(ModelTestDataCreator.baseMediaItem())
    }

    @Test
    fun fromEntity_emptyTitle() {
        val mediaItemEntity = EntityTestDataCreator.baseMediaItemEntity().emptyTitle()

        val mediaItem = MediaItem.fromEntity(mediaItemEntity, null)

        assertThat(mediaItem).isEqualTo(ModelTestDataCreator.baseMediaItem().emptyTitle())
    }

    @Test
    fun fromEntity_nullDescription() {
        val mediaItemEntity = EntityTestDataCreator.baseMediaItemEntity().nullDescription()

        val mediaItem = MediaItem.fromEntity(mediaItemEntity, null)

        assertThat(mediaItem).isEqualTo(ModelTestDataCreator.baseMediaItem().nullDescription())
    }

    @Test
    fun fromEntity_nullRanking() {
        val mediaItemEntity = EntityTestDataCreator.baseMediaItemEntity().nullRanking()

        val mediaItem = MediaItem.fromEntity(mediaItemEntity, null)

        assertThat(mediaItem).isEqualTo(ModelTestDataCreator.baseMediaItem().nullRanking())
    }

    @Test
    fun fromEntity_nullGenres() {
        val mediaItemEntity = EntityTestDataCreator.baseMediaItemEntity().nullGenres()

        val mediaItem = MediaItem.fromEntity(mediaItemEntity, null)

        assertThat(mediaItem).isEqualTo(ModelTestDataCreator.baseMediaItem().emptyGenres())
    }

    @Test
    fun fromEntity_nullCoverImageUrl() {
        val mediaItemEntity = EntityTestDataCreator.baseMediaItemEntity().nullCoverImageUrl()

        val mediaItem = MediaItem.fromEntity(mediaItemEntity, null)

        assertThat(mediaItem).isEqualTo(ModelTestDataCreator.baseMediaItem().nullCoverImageUrl())

    }

    @Test
    fun fromEntity_nullColorStr() {
        val mediaItemEntity = EntityTestDataCreator.baseMediaItemEntity().nullColorStr()

        val mediaItem = MediaItem.fromEntity(mediaItemEntity, null)

        assertThat(mediaItem).isEqualTo(ModelTestDataCreator.baseMediaItem().nullColorStr())

    }

    @Test
    fun fromEntity_nullMeanScore() {
        val mediaItemEntity = EntityTestDataCreator.baseMediaItemEntity().nullMeanScore()

        val mediaItem = MediaItem.fromEntity(mediaItemEntity, null)

        assertThat(mediaItem).isEqualTo(ModelTestDataCreator.baseMediaItem().nullMeanScore())

    }

    @Test
    fun fromEntity_nullSiteUrl() {
        val mediaItemEntity = EntityTestDataCreator.baseMediaItemEntity().nullSiteUrl()

        val mediaItem = MediaItem.fromEntity(mediaItemEntity, null)

        assertThat(mediaItem).isEqualTo(ModelTestDataCreator.baseMediaItem().nullSiteUrl())
    }
}