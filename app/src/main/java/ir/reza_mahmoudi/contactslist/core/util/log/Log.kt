package ir.reza_mahmoudi.contactslist.core.util.log

import android.util.Log
import ir.reza_mahmoudi.contactslist.BuildConfig


fun showLog(tag: String = "Test Log: ", msg: String) {
    if (BuildConfig.DEBUG) {
        Log.d(tag, msg)
    }
}

fun showLogError(tag: String = "Test Log: ", msg: String) {
    if (BuildConfig.DEBUG) {
        Log.e(tag, msg)
    }
}