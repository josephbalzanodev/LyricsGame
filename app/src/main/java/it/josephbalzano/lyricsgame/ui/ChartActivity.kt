package it.josephbalzano.lyricsgame.ui

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import it.josephbalzano.lyricsgame.R
import it.josephbalzano.lyricsgame.ui.ShareData.chartsList
import it.josephbalzano.lyricsgame.ui.adapter.ChartAdapter
import it.josephbalzano.lyricsgame.ui.model.ChartItem
import it.josephbalzano.lyricsgame.utils.NavigationBar
import kotlinx.android.synthetic.main.activity_chart.*

class ChartActivity : AppCompatActivity() {
    private val adapter = ChartAdapter(chartsList.sortedByDescending { it.score }
        .mapIndexed { index, chartItem ->
            ChartItem(
                when (index) {
                    0 -> "\uD83E\uDD47 " + chartItem.name
                    1 -> "\uD83E\uDD48 " + chartItem.name
                    2 -> "\uD83E\uDD49 " + chartItem.name
                    else -> chartItem.name
                }, chartItem.score
            )
        })

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
}