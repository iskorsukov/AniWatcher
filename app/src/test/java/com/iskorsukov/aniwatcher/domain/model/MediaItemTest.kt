package com.iskorsukov.aniwatcher.domain.model

import com.google.common.truth.Truth.assertThat
import com.iskorsukov.aniwatcher.domain.settings.NamingScheme
import com.iskorsukov.aniwatcher.test.*
import org.junit.Test

class MediaItemTest {

    @Test
    fun fromEntity_genresNull() {
        val mediaItemEntity = EntityTestDataCreator.mediaItemEntity(
            mediaId = 1,
            genresCommaSeparated = null
        )

        val mediaItem = MediaItem.fromEntity(mediaItemEntity)

        assertThat(mediaItem.genres).isEmpty()
    }

    @Test
    fun fromEntity_genresEmptyString() {
        val mediaItemEntity = EntityTestDataCreator.mediaItemEntity(
            mediaId = 1,
            genresCommaSeparated = ""
        )

        val mediaItem = MediaItem.fromEntity(mediaItemEntity)

        assertThat(mediaItem.genres).isEmpty()
    }

    @Test
    fun fromEntity_genres() {
        val mediaItemEntity = EntityTestDataCreator.mediaItemEntity(
            mediaId = 1,
            genresCommaSeparated = "Action,Drama,Thriller"
        )

        val mediaItem = MediaItem.fromEntity(mediaItemEntity)

        assertThat(mediaItem.genres).containsExactly("Action", "Drama", "Thriller")
    }

    @Test
    fun fromEntity_statusUnknown() {
        val mediaItemEntity = EntityTestDataCreator.mediaItemEntity(
            mediaId = 1,
            status = "Unrecognizable"
        )

        val mediaItem = MediaItem.fromEntity(mediaItemEntity)

        assertThat(mediaItem.status).isEqualTo(MediaItem.LocalStatus.UNKNOWN)
    }

    @Test
    fun fromEntity_formatUnknown() {
        val mediaItemEntity = EntityTestDataCreator.mediaItemEntity(
            mediaId = 1,
            format = "Unrecognizable"
        )

        val mediaItem = MediaItem.fromEntity(mediaItemEntity)

        assertThat(mediaItem.format).isEqualTo(MediaItem.LocalFormat.UNKNOWN)
    }

    @Test
    fun baseText_english() {
        var mediaItemTitle = ModelTestDataCreator.mediaItemTitle(
            romaji = "Romaji",
            english = "English",
            native = "Native"
        )
        assertThat(mediaItemTitle.baseText()).isEqualTo(mediaItemTitle.english)
        mediaItemTitle = mediaItemTitle.copy(english = null)
        assertThat(mediaItemTitle.baseText()).isEqualTo(mediaItemTitle.romaji)
        mediaItemTitle = mediaItemTitle.copy(romaji = null)
        assertThat(mediaItemTitle.baseText()).isEqualTo(mediaItemTitle.native)
    }

    @Test
    fun baseText_romaji() {
        var mediaItemTitle = ModelTestDataCreator.mediaItemTitle(
            romaji = "Romaji",
            english = "English",
            native = "Native"
        )
        assertThat(mediaItemTitle.baseText(NamingScheme.ROMAJI)).isEqualTo(mediaItemTitle.romaji)
        mediaItemTitle = mediaItemTitle.copy(romaji = null)
        assertThat(mediaItemTitle.baseText(NamingScheme.ROMAJI)).isEqualTo(mediaItemTitle.english)
        mediaItemTitle = mediaItemTitle.copy(english = null)
        assertThat(mediaItemTitle.baseText(NamingScheme.ROMAJI)).isEqualTo(mediaItemTitle.native)
    }

    @Test
    fun baseText_native() {
        var mediaItemTitle = ModelTestDataCreator.mediaItemTitle(
            romaji = "Romaji",
            english = "English",
            native = "Native"
        )
        assertThat(mediaItemTitle.baseText(NamingScheme.NATIVE)).isEqualTo(mediaItemTitle.native)
        mediaItemTitle = mediaItemTitle.copy(native = null)
        assertThat(mediaItemTitle.baseText(NamingScheme.NATIVE)).isEqualTo(mediaItemTitle.romaji)
        mediaItemTitle = mediaItemTitle.copy(romaji = null)
        assertThat(mediaItemTitle.baseText(NamingScheme.NATIVE)).isEqualTo(mediaItemTitle.english)
    }

    @Test
    fun subText_english() {
        var mediaItemTitle = ModelTestDataCreator.mediaItemTitle(
            romaji = "Romaji",
            english = "English",
            native = "Native"
        )
        assertThat(mediaItemTitle.subText(NamingScheme.ENGLISH)).isEqualTo(mediaItemTitle.romaji)
        mediaItemTitle = mediaItemTitle.copy(romaji = null)
        assertThat(mediaItemTitle.subText(NamingScheme.ENGLISH)).isEqualTo(mediaItemTitle.native)
    }

    @Test
    fun subText_romaji() {
        var mediaItemTitle = ModelTestDataCreator.mediaItemTitle(
            romaji = "Romaji",
            english = "English",
            native = "Native"
        )
        assertThat(mediaItemTitle.subText(NamingScheme.ROMAJI)).isEqualTo(mediaItemTitle.english)
        mediaItemTitle = mediaItemTitle.copy(english = null)
        assertThat(mediaItemTitle.subText(NamingScheme.ROMAJI)).isEqualTo(mediaItemTitle.native)
    }

    @Test
    fun subText_native() {
        var mediaItemTitle = ModelTestDataCreator.mediaItemTitle(
            romaji = "Romaji",
            english = "English",
            native = "Native"
        )
        assertThat(mediaItemTitle.subText(NamingScheme.NATIVE)).isEqualTo(mediaItemTitle.romaji)
        mediaItemTitle = mediaItemTitle.copy(romaji = null)
        assertThat(mediaItemTitle.subText(NamingScheme.NATIVE)).isEqualTo(mediaItemTitle.english)
    }

    @Test
    fun titleContainsIgnoreCase() {
        val mediaItemTitle = ModelTestDataCreator.mediaItemTitle(
            romaji = "Romaji",
            english = "English",
            native = "Native"
        )
        assertThat(mediaItemTitle.containsIgnoreCase(mediaItemTitle.english!!.uppercase())).isTrue()
        assertThat(mediaItemTitle.containsIgnoreCase(mediaItemTitle.romaji!!.uppercase())).isTrue()
        assertThat(mediaItemTitle.containsIgnoreCase(mediaItemTitle.native!!.uppercase())).isTrue()
    }
}