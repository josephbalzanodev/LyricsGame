package it.josephbalzano.lyricsgame.viewmodel

import android.os.Bundle
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import it.josephbalzano.lyricsgame.network.Client
import it.josephbalzano.lyricsgame.network.model.*
import it.josephbalzano.lyricsgame.ui.ShareData.chartsList
import it.josephbalzano.lyricsgame.ui.ShareData.tracksMap
import it.josephbalzano.lyricsgame.ui.model.ChartItem
import it.josephbalzano.lyricsgame.ui.model.QuizCard
import it.josephbalzano.lyricsgame.utils.AnalyticsManager
import it.josephbalzano.lyricsgame.utils.AnalyticsManager.NETWORK_ARTIST_ERROR
import it.josephbalzano.lyricsgame.utils.AnalyticsManager.NETWORK_LYRICS_ERROR
import it.josephbalzano.lyricsgame.utils.AnalyticsManager.NETWORK_SONGS_ERROR
import it.josephbalzano.lyricsgame.utils.Extension.takeRandom
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel : ViewModel() {
    private val database: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val thereIsChartValues: MutableLiveData<Boolean> = MutableLiveData(false)
    private val loadedGame: MutableLiveData<Boolean> = MutableLiveData(false)
    val networkError: MutableLiveData<String> = MutableLiveData("")

    fun loadGame(): MutableLiveData<Boolean> {
        Client.artist().getChartArtist()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorReturnItem(ResponseChartArtistGet(null))
            .flatMapCompletable { artists ->
                if (artists.message == null) {
                    networkError.postValue("Error on get Artist List :( Retry again")
                    loadedGame.postValue(false)
                    AnalyticsManager.logEvent(NETWORK_ARTIST_ERROR)
                    Completable.complete()
                } else
                    Client.track().getTrackChart(page = "1", page_size = "10")
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .onErrorReturnItem(ResponseChartTrackGet(null))
                        .flatMapCompletable { tracks ->
                            if (tracks.message == null) {
                                networkError.postValue("Error on retrive charted song :( Retry again")
                                loadedGame.postValue(false)
                                AnalyticsManager.logEvent(NETWORK_SONGS_ERROR)
                                Completable.complete()
                            } else
                                Completable.merge(
                                    tracks.message.body.track_list
                                        .map {
                                            it.saveLyrics(
                                                artists.message
                                                    .body
                                                    .artist_list
                                                    .takeRandom(2)
                                            )
                                        })
                                    .andThen { loadedGame.postValue(true) }
                        }
            }
            .subscribe()
        return loadedGame
    }

    fun loadChart(): MutableLiveData<Boolean> {
        val charts = database.collection("chart")
        charts.get()
            .addOnSuccessListener { document ->
                thereIsChartValues.postValue(document.documents.isNotEmpty())
                document.documents.forEach { doc ->
                    if (doc.getString("name")?.isNotBlank() == true)
                        if (!chartsList.any { it.id == doc.id })
                            chartsList.add(
                                ChartItem(
                                    doc.id,
                                    doc.getString("name") ?: "",
                                    doc.getDouble("score")?.toInt() ?: 0,
                                    doc.getTimestamp("date")?.getDateTime() ?: ""
                                )
                            )
                }
            }
        return thereIsChartValues
    }

    /**
     * Transform Timestamp to Date with pattern dd/MM/yyyy
     */
    private fun Timestamp.getDateTime(): String =
        try {
            val sdf = SimpleDateFormat("dd/MM/yyyy")
            val netDate = Date(this.seconds * 1000)
            sdf.format(netDate)
        } catch (e: Exception) {
            e.toString()
        }

    /**
     * Get and save lyrics
     */
    private fun Track.saveLyrics(list: List<Artist>) =
        Client.lyrics()
            .getLyrics(track_id = this.track.track_id.toString())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .onErrorReturnItem(ResponseLyricsGet(null))
            .map { trackLyrics ->
                if (trackLyrics.message == null) {
                    AnalyticsManager.logEvent(
                        NETWORK_LYRICS_ERROR,
                        Bundle().apply {
                            putString(
                                "trackId",
                                this@saveLyrics.track.track_id.toString()
                            )
                        })
                    Log.e(
                        "MainViewModel",
                        "Error on saveLyrics with trackId: ${this.track.track_id}"
                    )
                } else
                    tracksMap.add(
                        QuizCard(
                            this.track.artist_name,
                            list.map { it.artist.artist_name }
                                .toMutableList()
                                .apply { add(this@saveLyrics.track.artist_name) },
                            trackLyrics.message.body?.lyrics?.lyrics_body
                                ?.split("\n")
                                ?.filter {
                                    it != "" &&
                                            it != "..." &&
                                            it != "******* This Lyrics is NOT for Commercial use *******"
                                }
                                ?.dropLast(1) ?: listOf()
                        )
                    )
            }.toCompletable()
}