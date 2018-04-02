package moviester.ornoyman.com.moviester.presentation.movies.details

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import io.reactivex.Observable
import kotlinx.android.synthetic.main.controller_movie_details.view.*
import moviester.ornoyman.com.moviester.R
import moviester.ornoyman.com.moviester.di.moviesProvider
import moviester.ornoyman.com.moviester.domain.BASE_IMAGE_URI
import moviester.ornoyman.com.moviester.domain.models.GenresItem
import moviester.ornoyman.com.moviester.domain.models.MovieDetails
import moviester.ornoyman.com.moviester.domain.models.MovieItem
import moviester.ornoyman.com.moviester.presentation.BaseController
import timber.log.Timber

private const val MOVIE_ITEM_KEY = "MOVIE_ITEM_KEY"

class MovieDetailsController(args: Bundle) :
        BaseController<MovieDetailsView, MovieDetailsPresenter>(args), MovieDetailsView {

    private val movieItem by lazy { args[MOVIE_ITEM_KEY] as MovieItem }

    constructor(movie: MovieItem) :
            this(Bundle().apply { putParcelable(MOVIE_ITEM_KEY, movie) })

    override fun createPresenter(): MovieDetailsPresenter = MovieDetailsPresenter(moviesProvider)

    override val layoutResource: Int
        get() = R.layout.controller_movie_details

    override fun onViewBound(view: View) {
        setupToolbar()
    }

    private fun setupToolbar() {
        view?.movie_details_collapsible_toolbar_layout?.title = movieItem.title
        Picasso.get()
                .load(BASE_IMAGE_URI + movieItem.posterPath)
                .tag(hashCode())
                .fit().centerCrop()
                .into(view?.backdrop, object : Callback {
                    override fun onSuccess() {
                    }

                    override fun onError(e: Exception) {
                        Timber.w(e, "failed to load poster for $movieItem")
                    }
                })
    }

    override fun onDestroyView(view: View) {
        Picasso.get().cancelTag(hashCode())
        super.onDestroyView(view)
    }

    override fun loadMovieDetailsIntent(): Observable<MovieItem> =
            if (isRestoringViewState()) Observable.never()
            else Observable.just(movieItem).concatWith(Observable.never())

    override fun render(viewState: MovieDetailsViewState) {
        when (viewState) {
            is MovieDetailsLoaded -> renderDetails(viewState.movieDetails)
            else -> hideDetails()
        }
        if (viewState is MovieDetailsError) {
            view?.let { Snackbar.make(it, resources!!.getString(R.string.fetch_movie_details_error_msg), Snackbar.LENGTH_LONG) }
        }
    }

    private fun renderDetails(movieDetails: MovieDetails) {
        view?.movie_detail_genres_label?.visibility = VISIBLE
        view?.movie_detail_genres_content?.text = movieDetails.toGenresDisplay()
        view?.movie_detail_genres_content?.visibility = VISIBLE
        view?.movie_detail_overview_label?.visibility = VISIBLE
        view?.movie_detail_overview_content?.text = movieDetails.toOverviewDisplay()
        view?.movie_detail_overview_content?.visibility = VISIBLE
    }

    private fun hideDetails() {
        view?.movie_detail_genres_label?.visibility = GONE
        view?.movie_detail_genres_content?.visibility = GONE
        view?.movie_detail_overview_label?.visibility = GONE
        view?.movie_detail_overview_content?.visibility = GONE
    }

    private fun MovieDetails.toGenresDisplay(): String =
            this.genres?.takeIf { it.isNotEmpty() }?.mapNotNull(GenresItem::name)?.joinToString()
                    ?: "Unknown"

    private fun MovieDetails.toOverviewDisplay(): String =
            this.overview.takeUnless { it.isNullOrBlank() } ?: "Unknown"
}