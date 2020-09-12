package com.a99Spicy.a99spicy

import android.app.Application
import net.danlew.android.joda.JodaTimeAndroid
import timber.log.Timber

class MyApplication :Application(){
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        JodaTimeAndroid.init(this);
    }
}