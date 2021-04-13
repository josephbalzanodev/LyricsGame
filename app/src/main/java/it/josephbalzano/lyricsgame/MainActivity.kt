package it.josephbalzano.lyricsgame

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import it.josephbalzano.lyricsgame.utils.NavigationBar
import it.josephbalzano.lyricsgame.utils.NavigationBar.changeColor

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            changeColor(this, NavigationBar.NavBarIconColor.LIGHT, Color.parseColor("#d97e00"))
    }
}