package com.shivam.letstalk.custom

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.shivam.letstalk.BuildConfig

inline fun <reified T : Any> Context.launchActivity(
    options: Bundle? = null,
    noinline init: Intent.() -> Unit = {}
) {
    val intent = newIntent<T>(this)
    intent.init()
    startActivity(intent, options)
}

inline fun <reified T : Any> newIntent(context: Context): Intent =
    Intent(context, T::class.java)

fun log(tag: String, message: String = "__") {
    if (BuildConfig.DEBUG) {
        Log.d(tag, message)
    }
}

/*fun log(tag: Any, message: Any?) {
    if (BuildConfig.DEBUG) {
        Log.i(tag.toString(), message?.toJson() ?: "___")
    }
}
val gson = Gson()
fun Any.toJson() = gson.toJson(this)*/
