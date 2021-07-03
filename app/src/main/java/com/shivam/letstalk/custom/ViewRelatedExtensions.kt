package com.shivam.letstalk.util

import android.widget.EditText
import android.widget.TextView

fun TextView.getString(): String {
    return this.text.toString()
}

fun EditText.getString(): String {
    return this.text.toString()
}