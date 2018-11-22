package com.pandulapeter.campfire.feature.shared

import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.TextView
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.ViewDataBinding
import com.pandulapeter.campfire.feature.detail.DetailFragment
import com.pandulapeter.campfire.feature.main.songs.SongsFragment


abstract class TopLevelFragment<B : ViewDataBinding, out VM : OldCampfireViewModel>(@LayoutRes layoutResourceId: Int) : OldCampfireFragment<B, VM>(layoutResourceId) {

    protected val defaultToolbar by lazy { AppCompatTextView(context).apply { gravity = Gravity.CENTER_VERTICAL } }
    protected open val appBarView: View? = null
    protected var toolbarWidth = 0
    open val shouldShowAppBar = true

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        getCampfireActivity()!!.onScreenChanged()
        if (this !is DetailFragment) {
            getCampfireActivity()!!.updateToolbarTitleView(inflateToolbarTitle(getCampfireActivity()!!.toolbarContext), toolbarWidth)
        }
        if (savedInstanceState == null || this !is SongsFragment) {
            getCampfireActivity()!!.updateAppBarView(appBarView, savedInstanceState != null)
        }
    }

    open fun onDrawerStateChanged(state: Int) = Unit

    open fun onFloatingActionButtonPressed() = Unit

    protected open fun inflateToolbarTitle(context: Context): View = defaultToolbar

    protected fun TextView.updateToolbarTitle(@StringRes titleRes: Int, subtitle: String? = null) = updateToolbarTitle(context.getString(titleRes), subtitle)

    protected fun TextView.updateToolbarTitle(title: String, subtitle: String? = null) = setTitleSubtitle(this, title, subtitle)
}