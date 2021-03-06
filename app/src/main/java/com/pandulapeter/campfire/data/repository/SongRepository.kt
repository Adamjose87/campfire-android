package com.pandulapeter.campfire.data.repository

import com.pandulapeter.campfire.data.model.local.Language
import com.pandulapeter.campfire.data.model.remote.Song
import com.pandulapeter.campfire.data.networking.NetworkManager
import com.pandulapeter.campfire.data.persistence.Database
import com.pandulapeter.campfire.data.persistence.PreferenceDatabase
import com.pandulapeter.campfire.data.repository.shared.BaseRepository
import com.pandulapeter.campfire.util.UI
import com.pandulapeter.campfire.util.WORKER
import com.pandulapeter.campfire.util.enqueueCall
import com.pandulapeter.campfire.util.swap
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SongRepository(
    private val preferenceDatabase: PreferenceDatabase,
    private val networkManager: NetworkManager,
    private val database: Database
) : BaseRepository<SongRepository.Subscriber>() {

    companion object {
        private const val UPDATE_LIMIT = 24 * 60 * 60 * 1000
    }

    private val data = mutableListOf<Song>()
    val languages = mutableListOf<Language>()
    private var isCacheLoaded = false
    private var isLoading = true
        set(value) {
            if (field != value) {
                field = value
                GlobalScope.launch(UI) { subscribers.forEach { it.onSongRepositoryLoadingStateChanged(value) } }
            }
        }

    init {
        GlobalScope.launch(WORKER) {
            data.swap(database.songDao().getAll())
            updateLanguages()
            if (System.currentTimeMillis() - preferenceDatabase.lastSongsUpdateTimestamp > UPDATE_LIMIT) {
                updateData()
            } else {
                isLoading = false
            }
            isCacheLoaded = true
            if (data.isNotEmpty()) {
                notifyDataChanged()
            }
        }
    }

    override fun subscribe(subscriber: Subscriber) {
        super.subscribe(subscriber)
        if (!isLoading) {
            subscriber.onSongRepositoryDataUpdated(data)
        }
        subscriber.onSongRepositoryLoadingStateChanged(isLoading)
        if (!isLoading && data.isEmpty()) {
            subscriber.onSongRepositoryUpdateError()
        }
    }

    fun isCacheLoaded() = isCacheLoaded

    fun updateData() {
        isLoading = true
        networkManager.service.getSongs().enqueueCall(
            onSuccess = { newData ->
                GlobalScope.launch(WORKER) {
                    if (data.isNotEmpty()) {
                        newData.forEach { song ->
                            if (data.find { it.id == song.id } == null) {
                                song.isNew = true
                            }
                        }
                    }
                    data.swap(newData)
                    updateLanguages()
                    database.songDao().updateAll(data)
                    notifyDataChanged()
                    isLoading = false
                    preferenceDatabase.lastSongsUpdateTimestamp = System.currentTimeMillis()

                }
            },
            onFailure = {
                isLoading = false
                subscribers.forEach { it.onSongRepositoryUpdateError() }
            })
    }

    fun onSongOpened(songId: String) {
        data.find { it.id == songId }?.let {
            if (it.isNew) {
                it.isNew = false
                notifyDataChanged()
            }
        }
    }

    private fun updateLanguages() {
        languages.swap(data
            .map {
                when (it.language) {
                    Language.SupportedLanguages.ENGLISH.id -> Language.Known.English
                    Language.SupportedLanguages.SPANISH.id -> Language.Known.Spanish
                    Language.SupportedLanguages.HUNGARIAN.id -> Language.Known.Hungarian
                    Language.SupportedLanguages.ROMANIAN.id -> Language.Known.Romanian
                    else -> Language.Unknown
                }
            }
            .distinct()
            .sortedBy { it.nameResource }
        )
    }

    private fun notifyDataChanged() = GlobalScope.launch(UI) { subscribers.forEach { it.onSongRepositoryDataUpdated(data) } }

    interface Subscriber {

        fun onSongRepositoryDataUpdated(data: List<Song>)

        fun onSongRepositoryLoadingStateChanged(isLoading: Boolean)

        fun onSongRepositoryUpdateError()
    }
}