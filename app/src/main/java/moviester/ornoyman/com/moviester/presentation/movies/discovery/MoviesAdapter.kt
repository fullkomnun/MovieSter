package moviester.ornoyman.com.moviester.presentation.movies.discovery

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.jakewharton.rxbinding2.view.clicks
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.movie_item.view.*
import moviester.ornoyman.com.moviester.R
import moviester.ornoyman.com.moviester.domain.BASE_IMAGE_URI
import moviester.ornoyman.com.moviester.domain.models.MovieItem
import timber.log.Timber

class MoviesAdapter(movies: List<MovieItem>) : RecyclerView.Adapter<MoviesAdapter.MovieViewHolder>() {

    init {
        setHasStableIds(true)
    }

    private val moviesSource: MutableList<MovieItem> = movies.toMutableList()
    private val movieItemClicked = PublishSubject.create<MovieItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.movie_item, parent, false)
        return MovieViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int){
        val clicked =  holder.bind(moviesSource[position])
        clicked.doOnNext { Timber.w("click on $it") }.subscribe(movieItemClicked)
    }

    override fun onViewRecycled(holder: MovieViewHolder) = holder.unbind()

    override fun getItemCount(): Int = moviesSource.size

    override fun getItemId(position: Int): Long =
            moviesSource[position].id?.toLong() ?: super.getItemId(position)

    fun appendMovies(newMovies: List<MovieItem>) {
        val positionStart = moviesSource.size
        moviesSource += newMovies
        notifyItemRangeInserted(positionStart, newMovies.size)
    }

    fun clear() {
        moviesSource.clear()
        notifyDataSetChanged()
    }

    fun movieClicked(): Observable<MovieItem> = movieItemClicked.hide()

    class MovieViewHolder(view: View) : RecyclerView.ViewHolder(view), LayoutContainer {
        private var isLoaded = false

        override val containerView: View?
            get() = itemView

        fun bind(movie: MovieItem): Observable<MovieItem> {
            unbind()
            itemView?.movie_title?.text = movie.title

            Picasso.get()
                    .load(BASE_IMAGE_URI + movie.posterPath)
                    .tag(hashCode())
                    .fit().centerCrop()
                    .into(itemView?.movie_poster, object : Callback {
                        override fun onSuccess() {
                            isLoaded = true
                        }

                        override fun onError(e: Exception) {
                            Timber.w(e, "failed to load poster for $movie")
                        }
                    })

            return itemView.clicks().filter { isLoaded }.map { movie }
        }

        fun unbind() {
            isLoaded = false
            itemView.setOnClickListener(null)
            Picasso.get().cancelTag(hashCode())
        }
    }
}