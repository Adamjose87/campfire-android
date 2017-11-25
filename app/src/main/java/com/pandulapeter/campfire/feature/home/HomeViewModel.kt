package com.pandulapeter.campfire.feature.home

import android.databinding.ObservableField
import android.util.Log
import com.pandulapeter.campfire.data.network.NetworkManager
import com.pandulapeter.campfire.util.enqueue

/**
 * Handles events and logic for [HomeActivity].
 */
class HomeViewModel(networkManager: NetworkManager) {

    val text = ObservableField("Loading...")

    init {
        networkManager.getService().getLibrary().enqueue(
            onSuccess = { songs ->
                var string = "Success\n"
                songs.forEach { song ->
                    string += "\n${song.title} from ${song.artist}"
                    networkManager.getService().getSong(song.id).enqueue(
                        {
                            Log.d("DEBUG","Song ${it.id} downloaded: ${it.song}}")
                        },
                        {
                          Log.d("DEBUG","Couldn't download song ${song.id}")
                        }
                    )

                }
                text.set(string)
            },
            onFailure = { text.set("Error") })
    }
}