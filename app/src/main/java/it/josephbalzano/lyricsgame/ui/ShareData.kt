package it.josephbalzano.lyricsgame.ui

import android.content.Context
import it.josephbalzano.lyricsgame.ui.model.ChartItem
import it.josephbalzano.lyricsgame.ui.model.QuizCard

object ShareData {
    val tracksMap = mutableListOf<QuizCard>()

    val chartsList = mutableListOf<ChartItem>()

    var appContext: Context? = null
}