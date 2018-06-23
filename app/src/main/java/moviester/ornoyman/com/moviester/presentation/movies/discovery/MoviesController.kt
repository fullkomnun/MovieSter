package moviester.ornoyman.com.moviester.presentation.movies.discovery

import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import com.borax12.materialdaterangepicker.date.DatePickerDialog
import com.jakewharton.rxbinding2.support.v7.widget.scrollEvents
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.controller_movies.view.*
import moviester.ornoyman.com.moviester.R
import moviester.ornoyman.com.moviester.di.moviesProvider
import moviester.ornoyman.com.moviester.domain.models.MovieItem
import moviester.ornoyman.com.moviester.presentation.BaseController
import moviester.ornoyman.com.moviester.presentation.navigation.DefaultMoviesNavigator
import moviester.ornoyman.com.moviester.presentation.utils.findLastVisibleItemPosition
import moviester.ornoyman.com.moviester.presentation.utils.safeUpdate
import org.threeten.bp.LocalDate
import java.util.*

class MoviesController : BaseController<MoviesView, MoviesPresenter>(), MoviesView, DatePickerDialog.OnDateSetListener {

    private var adapter: MoviesAdapter? = null
    private val adapterReady = BehaviorSubject.create<MoviesAdapter>()
    private val filterChanged = PublishSubject.create<MoviesFilter>()

    override fun createPresenter(): MoviesPresenter =
            MoviesPresenter(moviesProvider, DefaultMoviesNavigator(router))

    override val layoutResource: Int
        get() = R.layout.controller_movies

    override fun onViewBound(view: View) {
        setupRecyclerView()
        setupToolbar()
    }

    private fun setupRecyclerView() {
        if (adapter == null) {
            view?.movies_list?.setHasFixedSize(true)
            view?.movies_list?.layoutManager = LinearLayoutManager(activity)
            adapter = MoviesAdapter(listOf())
            view?.movies_list?.adapter = adapter
            adapterReady.onNext(adapter!!)
        }
    }

    private fun setupToolbar() {
        (activity as? AppCompatActivity)?.setSupportActionBar(view?.movies_toolbar)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) =
            inflater.inflate(R.menu.movies_menu, menu)

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_filter_movies) {
            showDateRangePicker()
            return true
        }
        return false
    }

    private fun showDateRangePicker() {
        val now = Calendar.getInstance()
        val dpd = DatePickerDialog.newInstance(
                this@MoviesController,
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        )
        dpd.show(activity?.fragmentManager, "Datepickerdialog")
    }

    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int,
                           dayOfMonth: Int, yearEnd: Int, monthOfYearEnd: Int, dayOfMonthEnd: Int) {
        val startDate = LocalDate.of(year, monthOfYear + 1, dayOfMonth)
        val endDate = LocalDate.of(yearEnd, monthOfYearEnd + 1, dayOfMonthEnd)
        filterChanged.onNext(MoviesFilter(startDate = startDate, endDate = endDate))
    }

    override fun loadMoviesIntent(): Observable<LoadMoviesIntent> {
        val loadIntent = if (isRestoringViewState()) Observable.never()
        else Observable.just(LoadMoviesIntent())
        return loadIntent.mergeWith(filterChanged.map { filter -> LoadMoviesIntent(filter) })
    }

    override fun loadNextPageIntent(): Observable<LoadNextPageIntent> =
            view?.movies_list?.let { recyclerView ->
                recyclerView.scrollEvents()
                        .filter { it.dx() != 0 || it.dy() != 0 }
                        .map {
                            LoadNextPageIntent(lastVisibleItem = recyclerView.layoutManager.findLastVisibleItemPosition(),
                                    itemsTotal = recyclerView.layoutManager.itemCount)
                        }
            } ?: Observable.never()

    override fun showMovieDetailsIntent(): Observable<MovieItem> =
            adapterReady.flatMap { it.movieClicked() }

    override fun render(viewState: MoviesViewState) {
        if (isRestoringViewState()) {
            setupRecyclerView()
        }
        view?.movies_list?.safeUpdate {
            renderMovies(viewState)
        }

        if (viewState === MoviesErrorState) {
            view?.let { Snackbar.make(it, resources!!.getString(R.string.fetch_movies_error_msg), Snackbar.LENGTH_LONG) }
        }
    }

    private fun renderMovies(viewState: MoviesViewState) {
        if (viewState is MoviesLoadedState) {
            if (!isRestoringViewState()) {
                adapter?.appendMovies(viewState.newMovies ?: listOf())
            } else {
                adapter?.appendMovies(viewState.movies)
            }
        }
        if (viewState === MoviesLoadingState || viewState === MoviesErrorState) {
            adapter?.clear()
        }
    }

    override fun onDestroyView(view: View) {
        adapter = null
        super.onDestroyView(view)
    }
}