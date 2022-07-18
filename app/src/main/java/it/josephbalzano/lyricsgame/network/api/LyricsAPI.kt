package it.josephbalzano.lyricsgame.network.api

import io.reactivex.Single
import it.josephbalzano.lyricsgame.network.API_KEY
import it.josephbalzano.lyricsgame.network.model.ResponseLyricsGet
import retrofit2.http.GET
import retrofit2.http.Query

interface LyricsAPI {
    @GET("track.lyrics.get")
    fun getLyrics(
            @Query("apikey") apiKey: String = API_KEY,
            @Query("format") format: String = "json",
            @Query("callback") callback: String = "callback",
            @Query("track_id") track_id: String
    ): Single<ResponseLyricsGet>
}