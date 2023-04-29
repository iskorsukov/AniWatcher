package com.iskorsukov.aniwatcher.data.entity

import com.google.common.truth.Truth.assertThat
import com.iskorsukov.aniwatcher.data.entity.base.MediaItemEntity
import com.iskorsukov.aniwatcher.test.EntityTestDataCreator
import com.iskorsukov.aniwatcher.test.QueryTestDataCreator
import org.junit.Test

class MediaItemEntityTest {

    @Test
    fun fromData_season_id() {
        val data = QueryTestDataCreator.seasonAiringDataMedium(
            id = 1
        )

        val entity = MediaItemEntity.fromData(data)

        assertThat(entity).isEqualTo(
            EntityTestDataCreator.mediaItemEntity(
                mediaId = 1
            )
        )
    }

    @Test
    fun fromData_season_title() {
        val data = QueryTestDataCreator.seasonAiringDataMedium(
            id = 1,
            title = QueryTestDataCreator.seasonAiringDataTitle(
                romaji = "Romaji title",
                native = ""
            )
        )

        val entity = MediaItemEntity.fromData(data)

        assertThat(entity).isEqualTo(
            EntityTestDataCreator.mediaItemEntity(
                mediaId = 1,
                title = EntityTestDataCreator.mediaItemEntityTitle(
                    romaji = "Romaji title",
                    native = ""
                )
            )
        )
    }

    @Test
    fun fromData_season_coverImage() {
        val data = QueryTestDataCreator.seasonAiringDataMedium(
            id = 1,
            coverImage = QueryTestDataCreator.seasonAiringDataCoverImage(
                large = "large image",
                color = ""
            )
        )

        val entity = MediaItemEntity.fromData(data)

        assertThat(entity).isEqualTo(
            EntityTestDataCreator.mediaItemEntity(
                mediaId = 1,
                coverImageUrl = "large image",
                colorStr = ""
            )
        )
    }

    @Test
    fun fromData_season_mainStudio() {
        val data = QueryTestDataCreator.seasonAiringDataMedium(
            id = 1,
            studios = QueryTestDataCreator.seasonAiringDataStudios(
                listOf("First", "Second")
            )
        )

        val entity = MediaItemEntity.fromData(data)

        assertThat(entity).isEqualTo(
            EntityTestDataCreator.mediaItemEntity(
                mediaId = 1,
                mainStudio = "First"
            )
        )
    }

    @Test
    fun fromData_season_genres() {
        val data = QueryTestDataCreator.seasonAiringDataMedium(
            id = 1,
            genres = listOf("Action", "Comedy", "Drama")
        )

        val entity = MediaItemEntity.fromData(data)

        assertThat(entity).isEqualTo(
            EntityTestDataCreator.mediaItemEntity(
                mediaId = 1,
                genresCommaSeparated = "Action,Comedy,Drama"
            )
        )
    }

    @Test
    fun fromData_season_genres_empty() {
        val data = QueryTestDataCreator.seasonAiringDataMedium(
            id = 1,
            genres = listOf()
        )

        val entity = MediaItemEntity.fromData(data)

        assertThat(entity).isEqualTo(
            EntityTestDataCreator.mediaItemEntity(
                mediaId = 1,
                genresCommaSeparated = ""
            )
        )
    }

    @Test
    fun fromData_range_id() {
        val data = QueryTestDataCreator.rangeAiringDataMedia(
            id = 1
        )

        val entity = MediaItemEntity.fromData(data)

        assertThat(entity).isEqualTo(
            EntityTestDataCreator.mediaItemEntity(
                mediaId = 1
            )
        )
    }

    @Test
    fun fromData_range_title() {
        val data = QueryTestDataCreator.rangeAiringDataMedia(
            id = 1,
            title = QueryTestDataCreator.rangeAiringDataTitle(
                romaji = "Romaji title",
                native = ""
            )
        )

        val entity = MediaItemEntity.fromData(data)

        assertThat(entity).isEqualTo(
            EntityTestDataCreator.mediaItemEntity(
                mediaId = 1,
                title = EntityTestDataCreator.mediaItemEntityTitle(
                    romaji = "Romaji title",
                    native = ""
                )
            )
        )
    }

    @Test
    fun fromData_range_coverImage() {
        val data = QueryTestDataCreator.rangeAiringDataMedia(
            id = 1,
            coverImage = QueryTestDataCreator.rangeAiringDataCoverImage(
                large = "large image",
                color = ""
            )
        )

        val entity = MediaItemEntity.fromData(data)

        assertThat(entity).isEqualTo(
            EntityTestDataCreator.mediaItemEntity(
                mediaId = 1,
                coverImageUrl = "large image",
                colorStr = ""
            )
        )
    }

    @Test
    fun fromData_range_mainStudio() {
        val data = QueryTestDataCreator.rangeAiringDataMedia(
            id = 1,
            studios = QueryTestDataCreator.rangeAiringDataStudios(
                listOf("First", "Second")
            )
        )

        val entity = MediaItemEntity.fromData(data)

        assertThat(entity).isEqualTo(
            EntityTestDataCreator.mediaItemEntity(
                mediaId = 1,
                mainStudio = "First"
            )
        )
    }

    @Test
    fun fromData_range_genres() {
        val data = QueryTestDataCreator.rangeAiringDataMedia(
            id = 1,
            genres = listOf("Action", "Comedy", "Drama")
        )

        val entity = MediaItemEntity.fromData(data)

        assertThat(entity).isEqualTo(
            EntityTestDataCreator.mediaItemEntity(
                mediaId = 1,
                genresCommaSeparated = "Action,Comedy,Drama"
            )
        )
    }

    @Test
    fun fromData_range_genres_empty() {
        val data = QueryTestDataCreator.rangeAiringDataMedia(
            id = 1,
            genres = listOf()
        )

        val entity = MediaItemEntity.fromData(data)

        assertThat(entity).isEqualTo(
            EntityTestDataCreator.mediaItemEntity(
                mediaId = 1,
                genresCommaSeparated = ""
            )
        )
    }
}