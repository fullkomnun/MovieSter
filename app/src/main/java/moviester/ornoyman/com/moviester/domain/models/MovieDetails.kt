package moviester.ornoyman.com.moviester.domain.models

import com.squareup.moshi.Json
import se.ansman.kotshi.JsonSerializable

@JsonSerializable
data class MovieDetails(
        @Json(name = "original_language")
        val originalLanguage: String? = null,
        @Json(name = "imdb_id")
        val imdbId: String? = null,
        val video: Boolean? = null,
        val title: String? = null,
        @Json(name = "backdrop_path")
        val backdropPath: String? = null,
        val revenue: Int? = null,
        val genres: List<GenresItem>? = null,
        val popularity: Double? = null,
        @Json(name = "production_countries")
        val productionCountries: List<ProductionCountriesItem>? = null,
        val id: Int? = null,
        @Json(name = "vote_count")
        val voteCount: Int? = null,
        val budget: Int? = null,
        val overview: String? = null,
        @Json(name = "original_title")
        val originalTitle: String? = null,
        val runtime: Int? = null,
        @Json(name = "poster_path")
        val posterPath: Any? = null,
        @Json(name = "spoken_languages")
        val spokenLanguages: List<SpokenLanguagesItem>? = null,
        @Json(name = "production_companies")
        val productionCompanies: List<ProductionCompaniesItem>? = null,
        @Json(name = "release_date")
        val releaseDate: String? = null,
        @Json(name = "vote_average")
        val voteAverage: Double? = null,
        @Json(name = "belongs_to_collection")
        val belongsToCollection: Any? = null,
        val tagline: String? = null,
        val adult: Boolean? = null,
        val homepage: String? = null,
        val status: String? = null
)
