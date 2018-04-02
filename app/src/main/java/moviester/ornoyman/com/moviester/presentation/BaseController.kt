package moviester.ornoyman.com.moviester.presentation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bluelinelabs.conductor.Controller
import com.bluelinelabs.conductor.RestoreViewOnCreateController
import com.hannesdorfmann.mosby3.MviConductorDelegateCallback
import com.hannesdorfmann.mosby3.MviConductorLifecycleListener
import com.hannesdorfmann.mosby3.mvi.MviBasePresenter
import com.hannesdorfmann.mosby3.mvp.MvpView
import kotlinx.android.extensions.LayoutContainer
import timber.log.Timber


abstract class BaseController<V : MvpView, P : MviBasePresenter<V, *>>(args: Bundle? = null) :
        RestoreViewOnCreateController(args), MvpView, MviConductorDelegateCallback<V, P>, LayoutContainer {

    private var isRestoringViewState = false

    init {
        addLifecycleListener(getMosbyLifecycleListener())
        addLifecycleListener(object : LifecycleListener() {
            override fun postCreateView(controller: Controller, view: View) {
                onViewBound(view)
            }
        })
    }

    protected abstract fun onViewBound(view: View)

    final override fun onCreateView(inflater: LayoutInflater, container: ViewGroup, savedViewState: Bundle?): View =
            inflater.inflate(layoutResource, container, false)

    protected abstract val layoutResource: Int

    private fun getMosbyLifecycleListener(): Controller.LifecycleListener =
            MviConductorLifecycleListener<V, P>(this)

    override
    fun getMvpView(): V {
        try {
            return this as V
        } catch (e: ClassCastException) {
            val msg = "Couldn't cast the View to the corresponding View interface. Most likely you have forgot to add \"Controller implements YourMvpViewInterface\".\""
            Timber.e(this.toString(), msg)
            throw RuntimeException(msg, e)
        }
    }

    override fun setRestoringViewState(restoringViewState: Boolean) {
        isRestoringViewState = restoringViewState
    }

    fun isRestoringViewState() = isRestoringViewState

    override val containerView: View?
        get() = view
}