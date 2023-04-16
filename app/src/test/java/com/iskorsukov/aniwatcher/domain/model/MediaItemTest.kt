package com.iskorsukov.aniwatcher.domain.model

import com.google.common.truth.Truth.assertThat
import com.iskorsukov.aniwatcher.domain.settings.NamingScheme
import com.iskorsukov.aniwatcher.test.*
import org.junit.Test

class MediaItemTest {

    @Test
    fun fromEntity() {
        val mediaItemEntity = EntityTestDataCreator.baseMediaItemEntity()

        val mediaItem = MediaItem.fromEntity(mediaItemEntity, true)

        assertThat(mediaItem).isEqualTo(ModelTestDataCreator.baseMediaItem.isFollowing(true))
    }

    @Test
    fun fromEntity_noFollowingEntity() {
        val mediaItemEntity = EntityTestDataCreator.baseMediaItemEntity()

        val mediaItem = MediaItem.fromEntity(mediaItemEntity, false)

        assertThat(mediaItem).isEqualTo(ModelTestDataCreator.baseMediaItem)
    }

    @Test
    fun baseText_english() {
        var mediaItem = ModelTestDataCreator.baseMediaItem
        assertThat(mediaItem.title.baseText()).isEqualTo(mediaItem.title.english)
        mediaItem = ModelTestDataCreator.baseMediaItem.title(mediaItem.title.copy(english = null))
        assertThat(mediaItem.title.baseText()).isEqualTo(mediaItem.title.romaji)
        mediaItem = ModelTestDataCreator.baseMediaItem.title(mediaItem.title.copy(english = null, romaji = null))
        assertThat(mediaItem.title.baseText()).isEqualTo(mediaItem.title.native)
    }

    @Test
    fun baseText_romaji() {
        var mediaItem = ModelTestDataCreator.baseMediaItem
        assertThat(mediaItem.title.baseText(NamingScheme.ROMAJI)).isEqualTo(mediaItem.title.romaji)
        mediaItem = ModelTestDataCreator.baseMediaItem.title(mediaItem.title.copy(romaji = null))
        assertThat(mediaItem.title.baseText(NamingScheme.ROMAJI)).isEqualTo(mediaItem.title.english)
        mediaItem = ModelTestDataCreator.baseMediaItem.title(mediaItem.title.copy(english = null, romaji = null))
        assertThat(mediaItem.title.baseText(NamingScheme.ROMAJI)).isEqualTo(mediaItem.title.native)
    }

    @Test
    fun baseText_native() {
        var mediaItem = ModelTestDataCreator.baseMediaItem
        assertThat(mediaItem.title.baseText(NamingScheme.NATIVE)).isEqualTo(mediaItem.title.native)
        mediaItem = ModelTestDataCreator.baseMediaItem.title(mediaItem.title.copy(native = null))
        assertThat(mediaItem.title.baseText(NamingScheme.NATIVE)).isEqualTo(mediaItem.title.romaji)
        mediaItem = ModelTestDataCreator.baseMediaItem.title(mediaItem.title.copy(native = null, romaji = null))
        assertThat(mediaItem.title.baseText(NamingScheme.NATIVE)).isEqualTo(mediaItem.title.english)
    }

    @Test
    fun subText_english() {
        var mediaItem = ModelTestDataCreator.baseMediaItem
        assertThat(mediaItem.title.subText(NamingScheme.ENGLISH)).isEqualTo(mediaItem.title.romaji)
        mediaItem = ModelTestDataCreator.baseMediaItem.title(mediaItem.title.copy(romaji = null))
        assertThat(mediaItem.title.subText(NamingScheme.ENGLISH)).isEqualTo(mediaItem.title.native)
    }

    @Test
    fun subText_romaji() {
        var mediaItem = ModelTestDataCreator.baseMediaItem
        assertThat(mediaItem.title.subText(NamingScheme.ROMAJI)).isEqualTo(mediaItem.title.english)
        mediaItem = ModelTestDataCreator.baseMediaItem.title(mediaItem.title.copy(english = null))
        assertThat(mediaItem.title.subText(NamingScheme.ROMAJI)).isEqualTo(mediaItem.title.native)
    }

    @Test
    fun subText_native() {
        var mediaItem = ModelTestDataCreator.baseMediaItem
        assertThat(mediaItem.title.subText(NamingScheme.NATIVE)).isEqualTo(mediaItem.title.romaji)
        mediaItem = ModelTestDataCreator.baseMediaItem.title(mediaItem.title.copy(romaji = null))
        assertThat(mediaItem.title.subText(NamingScheme.NATIVE)).isEqualTo(mediaItem.title.english)
    }

    @Test
    fun titleContainsIgnoreCase() {
        val mediaItem = ModelTestDataCreator.baseMediaItem
        assertThat(mediaItem.title.containsIgnoreCase(mediaItem.title.english!!.uppercase())).isTrue()
        assertThat(mediaItem.title.containsIgnoreCase(mediaItem.title.romaji!!.uppercase())).isTrue()
        assertThat(mediaItem.title.containsIgnoreCase(mediaItem.title.native!!.uppercase())).isTrue()
    }
}