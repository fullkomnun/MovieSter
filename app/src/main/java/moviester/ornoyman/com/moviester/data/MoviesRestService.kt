package moviester.ornoyman.com.moviester.data

import io.reactivex.Single
import moviester.ornoyman.com.moviester.domain.models.MovieDetails
import moviester.ornoyman.com.moviester.domain.models.FetchMoviesResult
import org.threeten.bp.LocalDate
import org.threeten.bp.format.DateTimeFormatter
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MoviesRestService {
    @GET("discover/movie?sort_by=release_date.desc")
    fun fetchMovies(@Query("primary_release_date.lte") maxDate: String?,
                    @Query("primary_release_date.gte") minDate: String?,
                    @Query("page") page: Int): Single<FetchMoviesResult>

    @GET("movie/{movie_id}")
    fun fetchMovieDetails(@Path("movie_id") id: Int): Single<MovieDetails>
}

private val DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd")

fun MoviesRestService.fetchMovies(maxDate: LocalDate? = null,
                                  minDate: LocalDate? = null,
                                  page: Int = 1) =
        this.fetchMovies(maxDate = maxDate?.format(DATE_FORMATTER),
                minDate = minDate?.format(DATE_FORMATTER),
                page = page)