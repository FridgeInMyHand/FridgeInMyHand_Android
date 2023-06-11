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

    var serverApiAddress: String
        get() = pref.getString(Key.serverApiAddress, "http://kykint.com:3939")!!
        set(value) = editor.putString(Key.serverApiAddress, value).apply()

    var aiApiAddress: String
        get() = pref.getString(Key.aiApiAddress, "http://kykint.com:3939")!!
        set(value) = editor.putString(Key.aiApiAddress, value).apply()


    fun registerPrefChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        pref.registerOnSharedPreferenceChangeListener(listener)
    }

    fun unregisterPrefChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) {
        pref.unregisterOnSharedPreferenceChangeListener(listener)
    }

    object Key {
        val UUID = "uuid"
        val serverApiAddress = "serverApiAddress"
        val aiApiAddress = "aiApiAddress"
    }
}