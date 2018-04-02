package moviester.ornoyman.com.moviester.presentation.navigation

import com.bluelinelabs.conductor.Router
import com.bluelinelabs.conductor.RouterTransaction
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler
import moviester.ornoyman.com.moviester.domain.models.MovieItem
import moviester.ornoyman.com.moviester.presentation.movies.details.MovieDetailsController

interface MoviesNavigator {
    fun showMovieDetails(movie: MovieItem)
}

class DefaultMoviesNavigator(private val router: Router) : MoviesNavigator {
    override fun showMovieDetails(movie: MovieItem) =
            router.pushController(RouterTransaction.with(MovieDetailsController(movie))
                    .pushChangeHandler(HorizontalChangeHandler())
                    .popChangeHandler(HorizontalChangeHandler()))
}