package com.iskorsukov.aniwatcher.domain.model

import androidx.annotation.StringRes
import com.iskorsukov.aniwatcher.R
import com.iskorsukov.aniwatcher.data.entity.base.MediaItemEntity
import com.iskorsukov.aniwatcher.domain.settings.NamingScheme
import java.io.Serializable

data class MediaItem(
    val id: Int,
    val title: Title,
    val description: String?,
    val coverImageUrl: String?,
    val colorStr: String?,
    val bannerImageUrl: String?,
    val mainStudio: String?,
    val popularity: Int?,
    val meanScore: Int?,
    val genres: List<String>,
    val siteUrl: String?,
    val status: LocalStatus?,
    val format: LocalFormat?,
    val season: String?,
    val year: Int?,
    val isFollowing: Boolean
): Serializable {
    data class Title(
        val romaji: String?,
        val english: String?,
        val native: String?
    ): Serializable {
        fun baseText(namingScheme: NamingScheme = NamingScheme.ENGLISH): String {
            val orderedTitles = when (namingScheme) {
                NamingScheme.ENGLISH -> listOf(english, romaji, native)
                NamingScheme.ROMAJI -> listOf(romaji, english, native)
                NamingScheme.NATIVE -> listOf(native, romaji, english)
            }
            val filteredTitles = orderedTitles.filterNotNull()
            return filteredTitles.getOrNull(0) ?: ""
        }

        fun subText(namingScheme: NamingScheme = NamingScheme.ENGLISH): String {
            val orderedTitles = when (namingScheme) {
                NamingScheme.ENGLISH -> listOf(english, romaji, native)
                NamingScheme.ROMAJI -> listOf(romaji, english, native)
                NamingScheme.NATIVE -> listOf(native, romaji, english)
            }
            val filteredTitles = orderedTitles.filterNotNull()
            return filteredTitles.getOrNull(1) ?: ""
        }

        fun containsIgnoreCase(str: String): Boolean {
            return romaji?.contains(str, true) == true ||
                    english?.contains(str, true) == true ||
                    native?.contains(str, true) == true
        }
    }

    enum class LocalFormat(@StringRes val labelResId: Int) {
        TV(R.string.format_tv),
        TV_SHORT(R.string.format_tv_short),
        OVA(R.string.format_ova),
        MOVIE(R.string.format_movie),
        SPECIAL(R.string.format_special),
        UNKNOWN(R.string.format_unknown);

        companion object {
            fun of(formatStr: String): LocalFormat {
                return try {
                    valueOf(formatStr)
                } catch (e: Exception) {
                    UNKNOWN
                }
            }
        }
    }

    enum class LocalStatus(@StringRes val labelResId: Int) {
        FINISHED(R.string.status_finished),
        RELEASING(R.string.status_releasing),
        NOT_YET_RELEASED(R.string.status_not_yet_released),
        CANCELLED(R.string.status_cancelled),
        UNKNOWN(R.string.status_unknown);

        companion object {
            fun of(statusStr: String): LocalStatus {
                return try {
                    valueOf(statusStr)
                } catch (e: Exception) {
                    UNKNOWN
                }
            }
        }
    }

    companion object {
        fun fromEntity(mediaItemEntity: MediaItemEntity, isFollowing: Boolean = false): MediaItem {
            return mediaItemEntity.run {
                MediaItem(
                    id = mediaId,
                    title = Title(
                        title.titleRomaji,
                        title.titleEnglish,
                        title.titleNative
                    ),
                    description = description,
                    coverImageUrl = coverImageUrl,
                    colorStr = colorStr,
                    bannerImageUrl = bannerImageUrl,
                    mainStudio = mainStudio,
                    popularity = popularity,
                    meanScore = meanScore,
                    genres = genresCommaSeparated?.split(",")
                        ?.filter { it.isNotEmpty() } ?: emptyList(),
                    siteUrl = siteUrl,
                    status = status?.let { LocalStatus.of(it) },
                    format = format?.let { LocalFormat.of(it) },
                    season = season,
                    year = year,
                    isFollowing = isFollowing
                )
            }
        }
    }
}
