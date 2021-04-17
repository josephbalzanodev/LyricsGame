package it.josephbalzano.lyricsgame.viewmodel

import android.os.CountDownTimer
import android.text.Editable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*


class PlayViewModel : ViewModel() {
    val database: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val remainTime = MutableLiveData(10)
    private val score = MutableLiveData(0)
    var currentPosQuiz = 0

    private var countdown: CountDownTimer =
        object : CountDownTimer(10000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                remainTime.postValue((millisUntilFinished / 1000).toInt())
            }

            override fun onFinish() {
                remainTime.postValue(0)
            }
        }

    fun restartCountDown() =
        countdown.apply {
            cancel()
            start()
        }

    fun stopTime() = countdown.apply { cancel() }

    fun getRemainTime() = remainTime

    fun observeScore() = score

    fun addCorrectResponse() {
        val time = remainTime.value
        val scoring = score.value

        score.postValue(((time ?: 0) * 2) + (scoring ?: 0))
    }

    fun saveData(text: Editable?): MutableLiveData<Boolean> {
        val saved = MutableLiveData(false)
        val newScoring = hashMapOf(
            "name" to text.toString(),
            "score" to score.value,
            "date" to Timestamp(Date())
        )

        val charts = database.collection("chart")
        charts.add(newScoring)
            .addOnSuccessListener { documentReference ->
                saved.postValue(true)
            }
            .addOnFailureListener { e ->
                saved.postValue(true)
            }
        return saved
    }

}