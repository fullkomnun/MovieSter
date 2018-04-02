package moviester.ornoyman.com.moviester.presentation.movies.details

import com.hannesdorfmann.mosby3.mvp.MvpView
import io.reactivex.Observable
import moviester.ornoyman.com.moviester.domain.models.MovieDetails
import moviester.ornoyman.com.moviester.domain.models.MovieItem

interface MovieDetailsView : MvpView {
    fun loadMovieDetailsIntent(): Observable<MovieItem>

    fun render(viewState: MovieDetailsViewState)
}

sealed class MovieDetailsViewState(open val movie: MovieItem)
data class MovieDetailsLoading(override val movie: MovieItem) : MovieDetailsViewState(movie)
data class MovieDetailsError(override val movie: MovieItem) : MovieDetailsViewState(movie)
data class MovieDetailsLoaded(override val movie: MovieItem,
                              val movieDetails: MovieDetails) : MovieDetailsViewState(movie)