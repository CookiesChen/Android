package studio.xmatrix.coffee.data.store

import android.preference.PreferenceManager
import studio.xmatrix.coffee.App

object DefaultSharedPref {

    private lateinit var app: App

    private const val KeyUserId = "id"

    enum class Key {
        UserId
    }

    fun init(app: App) {
        this.app = app
    }

    fun get(key: Key): String {
        val pref = PreferenceManager.getDefaultSharedPreferences(app)
        return when (key) {
            Key.UserId -> pref.getString(KeyUserId, "") ?: ""
        }
    }

    fun set(key: Key, value: String) {
        val editor = PreferenceManager.getDefaultSharedPreferences(app).edit()
        when (key) {
            Key.UserId -> editor.putString(KeyUserId, value)
        }
        editor.apply()
    }

    fun remove(key: Key) {
        val editor = PreferenceManager.getDefaultSharedPreferences(app).edit()
        when (key) {
            Key.UserId -> editor.remove(KeyUserId)
        }
        editor.apply()
    }
}
