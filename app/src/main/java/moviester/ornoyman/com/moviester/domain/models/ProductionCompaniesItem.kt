package moviester.ornoyman.com.moviester.domain.models

import com.squareup.moshi.Json
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class ProductionCompaniesItem(
        @Json(name = "logo_path")
        val logoPath: String? = null,
        val name: String? = null,
        val id: Int? = null,
        @Json(name = "origin_country")
        val originCountry: String? = null
)
