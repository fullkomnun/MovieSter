package moviester.ornoyman.com.moviester.domain.models

import com.squareup.moshi.Json
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class SpokenLanguagesItem(
        val name: String? = null,
        @Json(name = "iso_3166_1")
        val iso6391: String? = null
)
