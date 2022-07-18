package it.josephbalzano.lyricsgame.network

import com.google.gson.Gson
import it.josephbalzano.lyricsgame.network.api.ArtistAPI
import it.josephbalzano.lyricsgame.network.api.LyricsAPI
import it.josephbalzano.lyricsgame.network.api.TrackAPI
import it.josephbalzano.lyricsgame.network.model.GeneralResponse
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object Client {
    var logInterceptor: HttpLoggingInterceptor =
        HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

    private val interceptor = object : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val response: Response?
            try {
                response = chain.proceed(chain.request())
            } catch (e: Exception) {
                return Response.Builder()
                    .request(chain.request())
                    .code(500)
                    .build()
            } 

            val header = Gson().fromJson<GeneralResponse>(
                response?.peekBody(50000)?.string() ?: "",
                GeneralResponse::class.java
            )
            return if (header.message.header.status_code >= 400)
                Response.Builder().code(400).build()
            else response!!
        }
    }

    private val client = OkHttpClient
        .Builder()
        .addInterceptor(logInterceptor)
        .addInterceptor(interceptor)
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