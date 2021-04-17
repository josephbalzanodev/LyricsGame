package it.josephbalzano.lyricsgame.utils

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import it.josephbalzano.lyricsgame.R
import it.josephbalzano.lyricsgame.ui.model.QuizCard

object Extension {
    fun AppCompatActivity.setBlueNavigationBar() =
        this.apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                NavigationBar.changeColor(
                    this,
                    NavigationBar.NavBarIconColor.LIGHT,
                    getColor(R.color.colorPrimary)
                )
        }

    fun <T> List<T>.takeRandom(n: Int) = this.shuffled().take(n)
}