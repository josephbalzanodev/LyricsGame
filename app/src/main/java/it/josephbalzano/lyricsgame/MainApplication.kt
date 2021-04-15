package it.josephbalzano.lyricsgame

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        initFirebase()
    }

    private fun initFirebase() {
        FirebaseApp.initializeApp(applicationContext)

        FirebaseCrashlytics.getInstance()
            .setCrashlyticsCollectionEnabled(true)
    }
}