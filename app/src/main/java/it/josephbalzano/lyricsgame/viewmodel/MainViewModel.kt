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

    fun loadChart(): MutableLiveData<Boolean> {
        val charts = database.collection("chart")
        charts.get()
            .addOnSuccessListener { document ->
                thereIsChartValues.postValue(document.documents.isNotEmpty())
                document.documents.forEach {
                    if (it.getString("name")?.isNotBlank() == true)
                        chartsList.add(
                            ChartItem(
                                it.getString("name") ?: "",
                                it.getDouble("score")?.toInt() ?: 0
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