package moviester.ornoyman.com.moviester.presentation.movies.discovery

import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import io.reactivex.rxkotlin.ofType
import moviester.ornoyman.com.moviester.domain.api.FetchMoviesToken
import moviester.ornoyman.com.moviester.domain.api.MoviesProvider
import moviester.ornoyman.com.moviester.domain.models.MovieItem
import moviester.ornoyman.com.moviester.domain.rx.scanMap
import moviester.ornoyman.com.moviester.presentation.navigation.MoviesNavigator
import timber.log.Timber
import java.util.concurrent.TimeUnit

class MoviesPresenter(private val moviesProvider: MoviesProvider,
                      private val navigator: MoviesNavigator) :
        MviBasePresenter<MoviesView, MoviesViewState>(MoviesLoadingState) {

    override fun bindIntents() {
        val loadNextPageIntent = intent { it.loadNextPageIntent() }
                .throttleFirst(1, TimeUnit.SECONDS, AndroidSchedulers.mainThread())
                .filter(this::shouldFetchNextPage)
                .distinctUntilChanged()
                .map { Unit }

        val fetchMoviesMutation =
                intent { it.loadMoviesIntent() }
                        .switchMap {
                            Observable.just(Unit).concatWith(loadNextPageIntent)
                                    .fetchNextMovies(it)
                        }

        val showMovieDetailsMutation = intent { it.showMovieDetailsIntent() }
                .doOnNext { navigator.showMovieDetails(it) }
                .ofType<MoviesMutation>()

        val viewStates =
                fetchMoviesMutation
                        .mergeWith(showMovieDetailsMutation)
                        .scan(MoviesLoadingState as MoviesViewState, this::reduce)
                        .distinctUntilChanged()
                        .observeOn(AndroidSchedulers.mainThread())

        subscribeViewState(viewStates, MoviesView::render)
    }

    private fun Observable<Unit>.fetchNextMovies(loadMoviesIntent: LoadMoviesIntent): Observable<MoviesMutation> =
            this.scanMap(seedToken(loadMoviesIntent),
                    BiFunction { token, _ -> moviesProvider.fetchMovies(token).toObservable() })
                    .map {
                        MoviesLoadedMutation(it.result?.movies
                                ?: listOf()) as MoviesMutation
                    }
                    .startWith(MoviesLoadingMutation)
                    .doOnError { Timber.e(it, "failed to fetch movies") }
                    .onErrorReturnItem(MoviesErrorMutation)

    private fun seedToken(it: LoadMoviesIntent) =
            Observable.just(FetchMoviesToken(maxDate = it.filter?.endDate, minDate = it.filter?.startDate))

    private fun reduce(oldState: MoviesViewState, mutation: MoviesMutation): MoviesViewState =
            when (mutation) {
                MoviesLoadingMutation -> MoviesLoadingState
                MoviesErrorMutation -> MoviesErrorState
                is MoviesLoadedMutation -> oldState.reduce(mutation)
            }

    private fun shouldFetchNextPage(it: LoadNextPageIntent) =
            it.itemsTotal > 0 && it.lastVisibleItem.toFloat() / it.itemsTotal >= 0.75


    private fun MoviesViewState.reduce(mutation: MoviesLoadedMutation): MoviesViewState =
            when (this) {
                MoviesLoadingState -> MoviesLoadedState(movies = mutation.movies)
                MoviesErrorState -> MoviesLoadedState(movies = mutation.movies)
                is MoviesLoadedState -> this.copy(movies = this.movies + mutation.movies, newMovies = mutation.movies)
            }
}

sealed class MoviesMutation
object MoviesLoadingMutation : MoviesMutation()
object MoviesErrorMutation : MoviesMutation()
data class MoviesLoadedMutation(val movies: List<MovieItem>) : MoviesMutation()