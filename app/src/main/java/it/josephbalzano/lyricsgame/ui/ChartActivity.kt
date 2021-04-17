package it.josephbalzano.lyricsgame.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import it.josephbalzano.lyricsgame.R
import it.josephbalzano.lyricsgame.ui.ChartDetailActivity.Companion.NAME
import it.josephbalzano.lyricsgame.ui.ShareData.chartsList
import it.josephbalzano.lyricsgame.ui.adapter.ChartAdapter
import it.josephbalzano.lyricsgame.utils.Extension.setBlueNavigationBar
import kotlinx.android.synthetic.main.activity_chart.*

class ChartActivity : AppCompatActivity(), ChartAdapter.ViewHolder.ChartItemListener {
    private val chartAdapter =
        ChartAdapter(
            chartsList
                .sortedByDescending { it.score }
                .distinctBy { it.name },
            this
        )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chart)

        setBlueNavigationBar()

        chart.apply {
            layoutManager = LinearLayoutManager(
                baseContext,
                LinearLayoutManager.VERTICAL,
                false
            )
            adapter = this@ChartActivity.chartAdapter
        }
    }

    override fun onClick(username: String) =
        startActivity(
            Intent(this, ChartDetailActivity::class.java).apply {
                putExtra(NAME, username)
            })
}