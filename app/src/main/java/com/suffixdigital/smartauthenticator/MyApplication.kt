package com.suffixdigital.smartauthenticator

import android.app.Application
import com.facebook.FacebookSdk
import com.facebook.LoggingBehavior
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
        FacebookSdk.setIsDebugEnabled(true)
        FacebookSdk.setAdvertiserIDCollectionEnabled(true)
        FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS)

    }
}