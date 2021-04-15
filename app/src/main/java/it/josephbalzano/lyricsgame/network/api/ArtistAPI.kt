package it.josephbalzano.lyricsgame.network.api

import io.reactivex.Single
import it.josephbalzano.lyricsgame.network.API_KEY
import it.josephbalzano.lyricsgame.network.model.ResponseChartArtistGet
import retrofit2.http.GET
import retrofit2.http.Query

interface ArtistAPI {
    @GET("chart.artists.get")
    fun getChartArtist(
        @Query("apikey") apiKey: String = API_KEY,
        @Query("format") format: String = "json",
        @Query("callback") callback: String = "callback",
        @Query("page") page: String = "1",
        @Query("page_size") page_size: String = "10",
        @Query("country") country: String = "it"
    ): Single<ResponseChartArtistGet>
}