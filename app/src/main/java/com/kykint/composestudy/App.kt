package com.kykint.composestudy

import android.app.Application
import android.content.Context
import com.jakewharton.threetenabp.AndroidThreeTen

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        application = this

        AndroidThreeTen.init(this)
    }

    companion object {
        private lateinit var application: Application

        val context: Context
            get() = application.applicationContext
    }
}