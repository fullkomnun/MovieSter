package moviester.ornoyman.com.moviester.domain.api

import io.reactivex.Single
import moviester.ornoyman.com.moviester.domain.models.FetchMoviesResult
import moviester.ornoyman.com.moviester.domain.models.MovieDetails
import org.threeten.bp.LocalDate

interface MoviesProvider {
    fun fetchMovies(token: FetchMoviesToken): Single<FetchMoviesToken>

    fun fetchMovieDetails(id: Int): Single<MovieDetails>
}

data class FetchMoviesToken(val result: FetchMoviesResult? = null,
                            val page: Int = 1,
                            val maxDate: LocalDate? = null,
                            val minDate: LocalDate? = null)