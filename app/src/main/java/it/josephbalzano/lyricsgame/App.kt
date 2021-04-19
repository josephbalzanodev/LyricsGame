package it.josephbalzano.lyricsgame

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import it.josephbalzano.lyricsgame.utils.AnalyticsManager

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        initFirebase()
    }

    private fun initFirebase() {
        AnalyticsManager.init(applicationContext)

        FirebaseApp.initializeApp(applicationContext)

        FirebaseCrashlytics.getInstance()
            .setCrashlyticsCollectionEnabled(true)
    }
}