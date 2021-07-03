package com.shivam.letstalk

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.shivam.letstalk.util.Constants

class MyApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
    }

    companion object{
        var appContext: Context? = null
        private set

        val sharedPreferences: SharedPreferences by lazy {
            appContext!!.getSharedPreferences(
                Constants.KEY_PREFERENCE_NAME,
                Context.MODE_PRIVATE
            )
        }
    }
}