package com.example.frontend_nhom1.util

import android.content.Context

class PreferencesHelper private constructor(private val ctx: Context) {
    private val prefs = ctx.getSharedPreferences("todo_prefs", Context.MODE_PRIVATE)

    fun saveToken(token: String) {
        prefs.edit().putString(KEY_TOKEN, token).apply()
    }

    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)

    fun clearToken() { prefs.edit().remove(KEY_TOKEN).apply() }

    fun getBaseUrl(): String = prefs.getString(KEY_BASE_URL, DEFAULT_BASE) ?: DEFAULT_BASE

    companion object {
        private const val KEY_TOKEN = "key_token"
        private const val KEY_BASE_URL = "key_base_url"
        private const val DEFAULT_BASE = "http://10.0.2.2:3000/"
        private var instance: PreferencesHelper? = null

        fun init(context: Context, baseUrl: String? = null) {
            val p = get(context)
            if (!baseUrl.isNullOrEmpty()) p.prefs.edit().putString(KEY_BASE_URL, baseUrl).apply()
        }

        fun get(context: Context): PreferencesHelper {
            if (instance == null) instance = PreferencesHelper(context.applicationContext)
            return instance!!
        }
    }
}
