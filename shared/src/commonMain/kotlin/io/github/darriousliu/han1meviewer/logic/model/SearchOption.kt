package io.github.darriousliu.han1meviewer.logic.model

import io.github.darriousliu.han1meviewer.util.Parcelable
import androidx.compose.ui.text.intl.Locale
import io.github.darriousliu.utils.CHINESE
import io.github.darriousliu.utils.ENGLISH
import io.github.darriousliu.utils.JAPANESE
import io.github.darriousliu.utils.LanguageHelper
import io.github.darriousliu.utils.SIMPLIFIED_CHINESE
import io.github.darriousliu.han1meviewer.util.Parcelize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import io.github.darriousliu.han1meviewer.generated.resources.Res
import io.github.darriousliu.han1meviewer.generated.resources.appearance_and_figure
import io.github.darriousliu.han1meviewer.generated.resources.characteristics
import io.github.darriousliu.han1meviewer.generated.resources.relationship
import io.github.darriousliu.han1meviewer.generated.resources.sex_position
import io.github.darriousliu.han1meviewer.generated.resources.story_location
import io.github.darriousliu.han1meviewer.generated.resources.story_plot
import io.github.darriousliu.han1meviewer.generated.resources.video_attr
import org.jetbrains.compose.resources.StringResource

@Suppress("EqualsOrHashCode")
@Serializable
@Parcelize
data class SearchOption(
    @SerialName("lang")
    val lang: Language? = null,
    @SerialName("name")
    val name: String? = null,
    @SerialName("search_key")
    val searchKey: String? = null,
) : Parcelable {

    companion object {
        fun <K> Map<K, Set<SearchOption>>.flatten(): Set<String> = buildSet {
            values.forEach { options ->
                val res = options.mapNotNullTo(mutableSetOf()) { it.searchKey }
                addAll(res)
            }
        }

        operator fun Map<String, List<SearchOption>>.get(scopeNameRes: StringResource): List<SearchOption> {
            return when (scopeNameRes) {
                Res.string.video_attr -> this["video_attributes"].orEmpty()
                Res.string.relationship -> this["character_relationships"].orEmpty()
                Res.string.characteristics -> this["characteristics"].orEmpty()
                Res.string.appearance_and_figure -> this["appearance_and_figure"].orEmpty()
                Res.string.story_plot -> this["story_plot"].orEmpty()
                Res.string.story_location -> this["story_location"].orEmpty()
                Res.string.sex_position -> this["sex_positions"].orEmpty()
                else -> error("Unknown scope name res: $scopeNameRes")
            }
        }

    }

    @Serializable
    @Parcelize
    data class Language(
        @SerialName("zh-rCN")
        val zhrCN: String? = null,
        @SerialName("zh-rTW")
        val zhrTW: String? = null,
        @SerialName("en")
        val en: String? = null,
        @SerialName("ja")
        val ja: String? = null,
    ) : Parcelable

    override fun hashCode(): Int = searchKey.hashCode()

    val value: String
        get() = when {
            lang == null -> name.orEmpty()
            else -> LanguageHelper.preferredLanguage.let { pl ->
                when (pl.language) {
                    Locale.CHINESE.language -> when (pl.region) {
                        Locale.SIMPLIFIED_CHINESE.region -> lang.zhrCN
                        else -> lang.zhrTW
                    }

                    Locale.ENGLISH.language -> lang.en
                    Locale.JAPANESE.language -> lang.ja
                    else -> lang.zhrTW
                }
            } ?: lang.zhrTW.orEmpty()
        }
}
