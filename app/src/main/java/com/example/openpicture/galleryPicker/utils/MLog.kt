package com.example.openpicture.galleryPicker.utils

import android.util.Log

/**
 * Created by deepan-5901 on 03/04/18.
 */
object MLog {
    var canLog = true
    fun e(tag: String, message: String?) {
        if (canLog) message?.let { Log.e(tag, it) }
    }

    fun d(tag: String, message: String?) {
        if (canLog) message?.let { Log.d(tag, it) }
    }

    fun v(tag: String, message: String?) {
        if (canLog) message?.let { Log.v(tag, it) }
    }
}