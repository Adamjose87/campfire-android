package com.pandulapeter.campfire.old.data.repository

import com.pandulapeter.campfire.old.data.storage.PreferenceStorageManager
import kotlin.properties.Delegates
import kotlin.reflect.KProperty

/**
 * Wraps caching and updating of the first time user experience state.
 */
class FirstTimeUserExperienceRepository(private val preferenceStorageManager: PreferenceStorageManager) {
    var shouldShowHistoryHint by Delegates.observable(preferenceStorageManager.shouldShowHistoryHint) { _: KProperty<*>, old: Boolean, new: Boolean ->
        if (old != new) {
            preferenceStorageManager.shouldShowHistoryHint = new
        }
    }
    var shouldShowPlaylistHint by Delegates.observable(preferenceStorageManager.shouldShowPlaylistHint) { _: KProperty<*>, old: Boolean, new: Boolean ->
        if (old != new) {
            preferenceStorageManager.shouldShowPlaylistHint = new
        }
    }
    var shouldShowManagePlaylistsHint by Delegates.observable(preferenceStorageManager.shouldShowManagePlaylistsHint) { _: KProperty<*>, old: Boolean, new: Boolean ->
        if (old != new) {
            preferenceStorageManager.shouldShowManagePlaylistsHint = new
        }
    }
    var shouldShowManageDownloadsHint by Delegates.observable(preferenceStorageManager.shouldShowManageDownloadsHint) { _: KProperty<*>, old: Boolean, new: Boolean ->
        if (old != new) {
            preferenceStorageManager.shouldShowManageDownloadsHint = new
        }
    }
    var shouldShowDetailSwipeHint by Delegates.observable(preferenceStorageManager.shouldShowDetailSwipeHint) { _: KProperty<*>, old: Boolean, new: Boolean ->
        if (old != new) {
            preferenceStorageManager.shouldShowDetailSwipeHint = new
        }
    }

    fun resetAll() {
        shouldShowHistoryHint = true
        shouldShowPlaylistHint = true
        shouldShowManagePlaylistsHint = true
        shouldShowManageDownloadsHint = true
        shouldShowDetailSwipeHint = true
    }
}