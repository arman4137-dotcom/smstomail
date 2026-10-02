package com.example.smstomail

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Telephony
import android.widget.Toast

class SmsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            return
        }

        val prefs = PreferencesManager(context)
        if (!prefs.isForwardingEnabled() || !prefs.isConfigured()) {
            return
        }

        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)

        for (message in messages) {
            val sender = message.originatingAddress ?: "Unknown"
            val messageText = message.messageBody

            // Categorize the message
            val category = MessageCategorizer.categorizeMessage(sender, messageText)

            // Send email
            val emailIntent = Intent(context, EmailService::class.java)
            emailIntent.putExtra("sender", sender)
            emailIntent.putExtra("message", messageText)
            emailIntent.putExtra("category", category.displayName)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(emailIntent)
            } else {
                context.startService(emailIntent)
            }

            // Show toast
            Toast.makeText(
                context,
                "پیام برای ارسال آماده شد",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
