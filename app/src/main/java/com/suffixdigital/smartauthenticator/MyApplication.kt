package com.suffixdigital.smartauthenticator

import android.app.Application
import com.google.firebase.FirebaseApp
import dagger.hilt.android.HiltAndroidApp

/**
 * Created by Kirtikant Patadiya on 2025-04-15.
 */
@HiltAndroidApp
class MyApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}