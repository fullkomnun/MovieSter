package moviester.ornoyman.com.moviester.presentation.movies.discovery

import com.hannesdorfmann.mosby3.mvp.MvpView
import io.reactivex.Observable
import moviester.ornoyman.com.moviester.domain.models.MovieItem
import org.threeten.bp.LocalDate

interface MoviesView : MvpView {

    fun loadMoviesIntent(): Observable<LoadMoviesIntent>

    fun loadNextPageIntent(): Observable<LoadNextPageIntent>

    fun showMovieDetailsIntent(): Observable<MovieItem>

    fun render(viewState: MoviesViewState)
}

data class LoadMoviesIntent(val filter: MoviesFilter? = null)
data class MoviesFilter(val startDate: LocalDate, val endDate: LocalDate)

data class LoadNextPageIntent(val lastVisibleItem: Int, val itemsTotal: Int)

sealed class MoviesViewState
object MoviesLoadingState : MoviesViewState()
object MoviesErrorState : MoviesViewState()
data class MoviesLoadedState(val movies: List<MovieItem>, val newMovies: List<MovieItem>? = null) : MoviesViewState()