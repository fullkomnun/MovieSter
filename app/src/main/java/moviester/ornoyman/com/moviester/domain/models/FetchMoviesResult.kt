package moviester.ornoyman.com.moviester.domain.models

import com.squareup.moshi.Json
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class FetchMoviesResult(

        @Json(name = "page")
        val page: Int? = null,

        @Json(name = "total_pages")
        val totalPages: Int? = null,

        @Json(name = "results")
        val movies: List<MovieItem>? = null,

        @Json(name = "total_results")
        val totalResults: Int? = null
)