package com.a99Spicy.a99spicy

import android.app.Application
import com.onesignal.OneSignal
import net.danlew.android.joda.JodaTimeAndroid
import timber.log.Timber

class MyApplication :Application(){
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        JodaTimeAndroid.init(this)
        // Logging set to help debug issues, remove before releasing your app.
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);

        // OneSignal Initialization
        OneSignal.startInit(this)
            .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
            .unsubscribeWhenNotificationsAreDisabled(true)
            .init()
    }
}