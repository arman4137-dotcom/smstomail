package com.example.smstomail

import android.content.Context
import android.content.SharedPreferences

class PreferencesManager(context: Context) {

    private val preferences: SharedPreferences = context.getSharedPreferences(
        "sms_to_mail_prefs",
        Context.MODE_PRIVATE
    )

    companion object {
        private const val KEY_ENABLED = "forwarding_enabled"
        private const val KEY_EMAIL_ONE = "email_one"
        private const val KEY_EMAIL_TWO = "email_two"
        private const val KEY_EMAIL_PASSWORD = "email_password"
        private const val KEY_SENDER_EMAIL = "sender_email"
    }

    fun isForwardingEnabled(): Boolean {
        return preferences.getBoolean(KEY_ENABLED, false)
    }

    fun setForwardingEnabled(enabled: Boolean) {
        preferences.edit().putBoolean(KEY_ENABLED, enabled).apply()
    }

    fun getEmailOne(): String {
        return preferences.getString(KEY_EMAIL_ONE, "") ?: ""
    }

    fun setEmailOne(email: String) {
        preferences.edit().putString(KEY_EMAIL_ONE, email).apply()
    }

    fun getEmailTwo(): String {
        return preferences.getString(KEY_EMAIL_TWO, "") ?: ""
    }

    fun setEmailTwo(email: String) {
        preferences.edit().putString(KEY_EMAIL_TWO, email).apply()
    }

    fun getEmailPassword(): String {
        return preferences.getString(KEY_EMAIL_PASSWORD, "") ?: ""
    }

    fun setEmailPassword(password: String) {
        preferences.edit().putString(KEY_EMAIL_PASSWORD, password).apply()
    }

    fun getSenderEmail(): String {
        return preferences.getString(KEY_SENDER_EMAIL, "") ?: ""
    }

    fun setSenderEmail(email: String) {
        preferences.edit().putString(KEY_SENDER_EMAIL, email).apply()
    }

    fun isConfigured(): Boolean {
        val emailOne = getEmailOne()
        val emailTwo = getEmailTwo()
        val password = getEmailPassword()
        return emailOne.isNotEmpty() && emailTwo.isNotEmpty() && password.isNotEmpty()
    }
}
