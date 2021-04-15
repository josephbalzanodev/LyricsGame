package it.josephbalzano.lyricsgame.network

import it.josephbalzano.lyricsgame.network.api.ArtistAPI
import it.josephbalzano.lyricsgame.network.api.LyricsAPI
import it.josephbalzano.lyricsgame.network.api.TrackAPI
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object Client {
    private val client = OkHttpClient
        .Builder()
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    fun lyrics(): LyricsAPI = retrofit.create(LyricsAPI::class.java)

    fun track(): TrackAPI = retrofit.create(TrackAPI::class.java)

    fun artist(): ArtistAPI = retrofit.create(ArtistAPI::class.java)
}