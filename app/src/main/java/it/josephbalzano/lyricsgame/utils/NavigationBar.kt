package it.josephbalzano.lyricsgame.utils

import android.app.Activity
import android.text.TextUtils
import android.view.View
import android.view.WindowManager
import androidx.annotation.RequiresApi

/**
 * NavigationBar class to change color
 */
object NavigationBar {
    // Forced ID to prevent edit from another caller
    private var forcedBy: String? = null

    /**
     * Change color of navigation bar
     *
     * @param activity  Caller Activity
     * @param iconColor Define button icon color
     * @param color     Define navigation bar color
     */
    @RequiresApi(api = 26)
    fun changeColor(activity: Activity, iconColor: NavBarIconColor, color: Int) {
        if (!TextUtils.isEmpty(forcedBy)) return
        privateChangeColor(activity, iconColor, color)
    }

    /**
     * Change color of navigation bar and lock the another changing from other activity
     *
     * @param forcedBy  Locking id. Calls to this method with another id it's ignored
     * @param activity  Caller Activity
     * @param iconColor Define button icon color
     * @param color     Define navigation bar color
     */
    @RequiresApi(api = 26)
    fun changeColor(
        forcedBy: String,
        activity: Activity,
        iconColor: NavBarIconColor,
        color: Int
    ) {
        if (!TextUtils.isEmpty(this.forcedBy) && this.forcedBy != forcedBy) return
        this.forcedBy = forcedBy
        privateChangeColor(activity, iconColor, color)
    }

    /**
     * Change color of navigation bar and prevent another call with the same parameter
     *
     * @param activity  Caller Activity
     * @param iconColor Define button icon color
     * @param color     Define navigation bar color
     */
    @RequiresApi(api = 26)
    private fun privateChangeColor(
        activity: Activity,
        iconColor: NavBarIconColor,
        color: Int
    ) {
        val window = activity.window
        window.navigationBarColor = color
        window.decorView.post {
            if (iconColor == NavBarIconColor.DARK) {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.decorView.systemUiVisibility = (
                        window.decorView.systemUiVisibility
                                or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR)
            } else window.decorView.systemUiVisibility = (
                    window.decorView.systemUiVisibility
                            and View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR.inv())
        }
    }

    /**
     * Remove lock from callers of this method
     *
     * @param forcedBy Force ID used for previus locked call
     */
    fun detachForce(forcedBy: String) {
        if (!TextUtils.isEmpty(this.forcedBy) && this.forcedBy == forcedBy) this.forcedBy =
            null
    }

    /**
     * Enum that define style of icon buttons on Navigation Bar
     *
     *
     * Possible values: LIGHT and DARK
     */
    enum class NavBarIconColor {
        LIGHT, DARK, NONE
    }
}