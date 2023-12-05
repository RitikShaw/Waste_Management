package com.mw.utills
import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit


class SharedPrefs(context: Context) {

    private var mPrefs: SharedPreferences =
        context.getSharedPreferences("UserDetails", Context.MODE_PRIVATE)

    var token: String
        set(value) {
            mPrefs.edit { putString("token", value) }
        }
        get() = mPrefs.getString("token", "")!!

    var email: String
        set(value) {
            mPrefs.edit { putString("userEmail", value) }
        }
        get() = mPrefs.getString("userEmail", "")!!

    var category: String
        set(value) {
            mPrefs.edit { putString("cat", value) }
        }
        get() = mPrefs.getString("cat", "")!!

    var sub_category: String
        set(value) {
            mPrefs.edit { putString("subcat", value) }
        }
        get() = mPrefs.getString("subcat", "")!!

    var others: String
        set(value) {
            mPrefs.edit { putString("others", value) }
        }
        get() = mPrefs.getString("others", "")!!

}