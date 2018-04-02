package moviester.ornoyman.com.moviester.data

import io.reactivex.Single
import moviester.ornoyman.com.moviester.domain.api.FetchMoviesToken
import moviester.ornoyman.com.moviester.domain.api.MoviesProvider
import moviester.ornoyman.com.moviester.domain.models.FetchMoviesResult
import moviester.ornoyman.com.moviester.domain.models.MovieDetails

class MoviesRepository(private val moviesRestService: MoviesRestService) : MoviesProvider {

    override fun fetchMovies(token: FetchMoviesToken): Single<FetchMoviesToken> {
        return moviesRestService.fetchMovies(page = token.page, maxDate = token.maxDate, minDate = token.minDate)
                .keepOnlyWithPosters() // dropping movies with no posters
                .toFetchMoviesToken(token)
    }

    private fun Single<FetchMoviesResult>.keepOnlyWithPosters(): Single<FetchMoviesResult> =
            this.map { result -> keepOnlyWithPosters(result) }

    private fun keepOnlyWithPosters(result: FetchMoviesResult) =
            result.copy(movies = result.movies?.filter { movie -> !movie.posterPath.isNullOrBlank() })

    private fun Single<FetchMoviesResult>.toFetchMoviesToken(token: FetchMoviesToken): Single<FetchMoviesToken> =
            this.map { token.copy(result = it, page = token.page + 1) }

    override fun fetchMovieDetails(id: Int): Single<MovieDetails> =
            moviesRestService.fetchMovieDetails(id)
}