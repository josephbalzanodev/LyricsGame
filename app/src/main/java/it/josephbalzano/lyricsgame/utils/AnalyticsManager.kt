package it.josephbalzano.lyricsgame.utils

import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics

object AnalyticsManager {
    private var instance: FirebaseAnalytics? = null

    const val NETWORK_ARTIST_ERROR = "networkArtistError"
    const val NETWORK_SONGS_ERROR = "networkSongsError"
    const val NETWORK_LYRICS_ERROR = "networkLyricsError"

    fun init(context: Context) = context.apply { instance = FirebaseAnalytics.getInstance(this) }

    fun logEvent(name: String) = instance?.logEvent(name, null)

    fun logEvent(name: String, params: Bundle) = instance?.logEvent(name, params)
}