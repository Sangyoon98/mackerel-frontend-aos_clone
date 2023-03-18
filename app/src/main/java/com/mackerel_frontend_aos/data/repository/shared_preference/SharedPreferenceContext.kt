package com.mackerel_frontend_aos.data.repository.shared_preference

import android.app.Application

class SharedPreferenceContext : Application() {
    companion object {
        lateinit var preference: SharedPreference
    }

    override fun onCreate() {
        preference = SharedPreference(applicationContext)
        super.onCreate()
    }
}