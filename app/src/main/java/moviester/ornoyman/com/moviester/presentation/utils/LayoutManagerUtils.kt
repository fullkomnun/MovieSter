package moviester.ornoyman.com.moviester.presentation.utils

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager


fun RecyclerView.LayoutManager.findLastVisibleItemPosition(): Int =
        if (this is StaggeredGridLayoutManager) {
            findLastVisibleItemPositions(null)[0]
        } else {
            (this as LinearLayoutManager).findLastVisibleItemPosition()
        }