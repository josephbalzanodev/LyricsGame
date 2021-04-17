package it.josephbalzano.lyricsgame.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import it.josephbalzano.lyricsgame.network.Client
import it.josephbalzano.lyricsgame.network.model.Artist
import it.josephbalzano.lyricsgame.network.model.Track
import it.josephbalzano.lyricsgame.ui.ShareData.chartsList
import it.josephbalzano.lyricsgame.ui.ShareData.tracksMap
import it.josephbalzano.lyricsgame.ui.model.ChartItem
import it.josephbalzano.lyricsgame.ui.model.QuizCard
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel : ViewModel() {
    val database: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val thereIsChartValues: MutableLiveData<Boolean> = MutableLiveData(false)
    private val loadedGame: MutableLiveData<Boolean> = MutableLiveData(false)

    fun loadGame(): MutableLiveData<Boolean> {
        Client.artist().getChartArtist()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMapCompletable { artists ->
                Client.track().getTrackChart(page = "1", page_size = "10")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .flatMapCompletable { tracks ->
                        Completable.merge(
                            tracks.message.body.track_list
                                .map {
                                    it.saveLyrics(
                                        artists.message.body.artist_list.shuffled().take(2)
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
            .map { trackLyrics ->
                tracksMap.add(
                    QuizCard(
                        this.track.artist_name,
                        list.map { it.artist.artist_name }
                            .toMutableList()
                            .apply { add(this@saveLyrics.track.artist_name) },
                        trackLyrics.message.body.lyrics.lyrics_body
                            .split("\n")
                            .filter {
                                it != "" &&
                                        it != "..." &&
                                        it != "******* This Lyrics is NOT for Commercial use *******"
                            }
                            .dropLast(1)
                    )
                )
            }.toCompletable()
}