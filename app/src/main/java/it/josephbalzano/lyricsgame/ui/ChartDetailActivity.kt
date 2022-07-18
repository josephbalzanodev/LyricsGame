package it.josephbalzano.lyricsgame.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import it.josephbalzano.lyricsgame.R
import it.josephbalzano.lyricsgame.ui.adapter.ChartDetailAdapter
import it.josephbalzano.lyricsgame.utils.Extension.setBlueNavigationBar
import kotlinx.android.synthetic.main.activity_chart_detail.*

class ChartDetailActivity : AppCompatActivity() {
    companion object {
        const val NAME = "username"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chart_detail)

        setBlueNavigationBar()

        val user = intent.getStringExtra(NAME)

        username.text = user

        plays.layoutManager = LinearLayoutManager(baseContext, LinearLayoutManager.VERTICAL, false)
        plays.adapter =
                ChartDetailAdapter(
                        ShareData.chartsList
                                .filter { it.name == user })
    }
}