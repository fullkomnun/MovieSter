package moviester.ornoyman.com.moviester.domain.models

import com.squareup.moshi.Json
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class ProductionCountriesItem(
        @Json(name = "iso_3166_1")
        val iso31661: String? = null,
        val name: String? = null
)
