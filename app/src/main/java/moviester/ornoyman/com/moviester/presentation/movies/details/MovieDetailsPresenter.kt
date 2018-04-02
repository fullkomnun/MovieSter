package moviester.ornoyman.com.moviester.presentation.movies.details

import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import moviester.ornoyman.com.moviester.domain.api.MoviesProvider
import timber.log.Timber

class MovieDetailsPresenter(private val moviesProvider: MoviesProvider) :
        MviBasePresenter<MovieDetailsView, MovieDetailsViewState>() {

    override fun bindIntents() {
        val viewStates =
                intent { it.loadMovieDetailsIntent() }
                        .switchMap { movie ->
                            moviesProvider.fetchMovieDetails(movie.id!!)
                                    .toObservable()
                                    .map { details -> MovieDetailsLoaded(movie, details) as MovieDetailsViewState }
                                    .startWith(MovieDetailsLoading(movie))
                                    .doOnError { Timber.e(it, "error while fetching movie details") }
                                    .onErrorReturnItem(MovieDetailsError(movie))
                        }.distinctUntilChanged()
                        .observeOn(AndroidSchedulers.mainThread())

        subscribeViewState(viewStates, MovieDetailsView::render)
    }

}