package it.josephbalzano.lyricsgame.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import it.josephbalzano.lyricsgame.R
import it.josephbalzano.lyricsgame.utils.NavigationBar
import it.josephbalzano.lyricsgame.utils.NavigationBar.changeColor
import it.josephbalzano.lyricsgame.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    val model: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            changeColor(
                this,
                NavigationBar.NavBarIconColor.LIGHT,
                getColor(R.color.colorPrimary)
            )

        initButtons()

        model.loadGame().observe(
            this,
            Observer { play.isEnabled = it }
        )
    }

    override fun onResume() {
        super.onResume()

        model.loadChart().observe(
            this,
            Observer { chart.isEnabled = it }
        )
    }

    private fun initButtons() {
        play.setOnClickListener {
            startActivity(Intent(this@MainActivity, PlayActivity::class.java))
        }
        chart.setOnClickListener {
            startActivity(Intent(this@MainActivity, ChartActivity::class.java))
        }
    }
}