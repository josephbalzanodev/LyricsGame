package it.josephbalzano.lyricsgame.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import it.josephbalzano.lyricsgame.R
import it.josephbalzano.lyricsgame.ui.ChartDetailActivity.Companion.NAME
import it.josephbalzano.lyricsgame.ui.ShareData.chartsList
import it.josephbalzano.lyricsgame.ui.adapter.ChartAdapter
import it.josephbalzano.lyricsgame.utils.NavigationBar
import kotlinx.android.synthetic.main.activity_chart.*

class ChartActivity : AppCompatActivity(), ChartAdapter.ViewHolder.ChartItemListener {
    private val adapter = ChartAdapter(chartsList.sortedByDescending { it.score }, this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chart)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            NavigationBar.changeColor(
                this,
                NavigationBar.NavBarIconColor.LIGHT,
                getColor(R.color.colorPrimary)
            )

        chart.layoutManager = LinearLayoutManager(baseContext, LinearLayoutManager.VERTICAL, false)
        chart.adapter = adapter
    }

    override fun onClick(username: String) =
        startActivity(
            Intent(this, ChartDetailActivity::class.java).apply {
                putExtra(NAME, username)
            })
}