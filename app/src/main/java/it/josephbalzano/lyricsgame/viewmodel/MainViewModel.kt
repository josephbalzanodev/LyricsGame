package it.josephbalzano.lyricsgame.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import it.josephbalzano.lyricsgame.network.Client
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
        Client.track().getTrackChart(page = "1", page_size = "10")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .flatMapCompletable { resp ->
                Completable.merge(
                    resp.message.body.track_list
                        .map { it.saveLyrics() })
                    .andThen { loadedGame.postValue(true) }
            }.subscribe()
        return loadedGame
    }

    fun getDateTime(l: Long): String =
        try {
            val sdf = SimpleDateFormat("dd/MM/yyyy")
            val netDate = Date(l * 1000)
            sdf.format(netDate)
        } catch (e: Exception) {
            e.toString()
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
                                    getDateTime(doc.getTimestamp("date")?.seconds ?: 0)
                                )
                            )
                }
            }
        return thereIsChartValues
    }

    /**
     * Get and save lyrics
     */
    private fun Track.saveLyrics() =
        Client.lyrics()
            .getLyrics(track_id = this.track.track_id.toString())
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .map { trackLyrics ->
                tracksMap.add(
                    QuizCard(
                        this.track.artist_name,
                        trackLyrics.message.body.lyrics.lyrics_body.split("\n")
                    )
                )
            }.toCompletable()
}