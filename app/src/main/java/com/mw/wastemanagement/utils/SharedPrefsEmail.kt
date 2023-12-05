package com.mw.wastemanagement.utils
import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit


class SharedPrefsEmail(context: Context) {

    private var mPrefs: SharedPreferences =
        context.getSharedPreferences("UserDetails", Context.MODE_PRIVATE)


    var email: String
        set(value) {
            mPrefs.edit { putString("userEmail", value) }
        }
        get() = mPrefs.getString("userEmail", "")!!


}