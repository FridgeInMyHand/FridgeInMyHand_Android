package com.kykint.fridgeinmyhand.utils

import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.kykint.fridgeinmyhand.App
import java.util.UUID

object Prefs {
    private val pref = PreferenceManager.getDefaultSharedPreferences(App.context)
    private val editor = pref.edit()

    init {
        editor.apply()
    }

    val uuid: String
        get() {
            return if (pref.contains(Key.UUID)) {
                pref.getString(Key.UUID, null)!!
            } else {
                val newUuid = UUID.randomUUID().toString()
                editor.putString(Key.UUID, newUuid).commit()
                newUuid
            }
        }


    fun registerPrefChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        pref.registerOnSharedPreferenceChangeListener(listener)
    }

    fun unregisterPrefChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        pref.unregisterOnSharedPreferenceChangeListener(listener)
    }

    object Key {
        val UUID = "uuid"
    }
}