package com.pandulapeter.campfire.feature.shared

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

abstract class CampfireViewModel : ViewModel(), CoroutineScope {

    private val job = Job()
    override val coroutineContext = job + Dispatchers.Main
    var isUiBlocked = true

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    open fun subscribe() = Unit

    open fun unsubscribe() = Unit
}