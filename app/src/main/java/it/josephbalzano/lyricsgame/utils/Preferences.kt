package it.josephbalzano.lyricsgame.utils

import android.app.Activity
import android.content.Context

object Preferences {
    fun get(act: Activity, key: String, default: String): String? {
        val sharedPref = act.getPreferences(Context.MODE_PRIVATE) ?: return default
        return sharedPref.getString(key, default)
    }

    fun get(act: Activity, key: String, default: Int): Int {
        val sharedPref = act.getPreferences(Context.MODE_PRIVATE) ?: return default
        return sharedPref.getInt(key, default)
    }

    fun get(act: Activity, key: String, default: Boolean): Boolean {
        val sharedPref = act.getPreferences(Context.MODE_PRIVATE) ?: return default
        return sharedPref.getBoolean(key, default)
    }

    fun put(act: Activity, key: String, str: String) {
        val sharedPref = act.getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putString(key, str)
            apply()
        }
    }

    fun put(act: Activity, key: String, value: Int) {
        val sharedPref = act.getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putInt(key, value)
            apply()
        }
    }

    fun put(act: Activity, key: String, value: Boolean) {
        val sharedPref = act.getPreferences(Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putBoolean(key, value)
            apply()
        }
    }
}