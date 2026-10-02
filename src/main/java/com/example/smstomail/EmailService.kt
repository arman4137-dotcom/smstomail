package com.example.smstomail

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.Looper
import android.widget.Toast
import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage
import kotlin.concurrent.thread

class EmailService : Service() {

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            val sender = intent.getStringExtra("sender") ?: ""
            val message = intent.getStringExtra("message") ?: ""
            val category = intent.getStringExtra("category") ?: ""

            thread {
                sendEmail(sender, message, category)
                stopSelf()
            }
        }
        return START_STICKY
    }

    private fun sendEmail(sender: String, messageText: String, category: String) {
        try {
            val prefs = PreferencesManager(this)
            val emailOne = prefs.getEmailOne()
            val emailTwo = prefs.getEmailTwo()
            val password = prefs.getEmailPassword()
            val senderEmail = prefs.getSenderEmail()

            if (emailOne.isEmpty() || emailTwo.isEmpty() || password.isEmpty() || senderEmail.isEmpty()) {
                showToast("تنظیمات ایمیل ناقص است")
                return
            }

            val props = Properties()
            props["mail.smtp.host"] = "smtp.gmail.com"
            props["mail.smtp.port"] = "587"
            props["mail.smtp.auth"] = "true"
            props["mail.smtp.starttls.enable"] = "true"
            props["mail.smtp.starttls.required"] = "true"
            props["mail.smtp.connectiontimeout"] = "5000"
            props["mail.smtp.timeout"] = "5000"
            props["mail.smtp.writetimeout"] = "5000"

            val authenticator = object : Authenticator() {
                override fun getPasswordAuthentication(): PasswordAuthentication {
                    return PasswordAuthentication(senderEmail, password)
                }
            }

            val session = Session.getInstance(props, authenticator)
            session.debug = false

            val subject = "پیام از $sender [$category]"
            val emailContent = buildEmailContent(sender, messageText, category)

            // Send to first email
            sendEmailToAddress(session, emailOne, senderEmail, subject, emailContent)

            // Send to second email
            if (emailTwo.isNotEmpty()) {
                sendEmailToAddress(session, emailTwo, senderEmail, subject, emailContent)
            }

            showToast("پیام با موفقیت ارسال شد")

        } catch (e: Exception) {
            showToast("خطا: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun sendEmailToAddress(
        session: Session,
        recipientEmail: String,
        senderEmail: String,
        subject: String,
        content: String
    ) {
        val message = MimeMessage(session)
        message.setFrom(InternetAddress(senderEmail))
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail))
        message.subject = subject
        message.setText(content, "utf-8", "html")

        Transport.send(message)
    }

    private fun buildEmailContent(sender: String, text: String, category: String): String {
        val date = Date()
        val format = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale("fa", "IR"))
        return """
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: Arial, sans-serif; direction: rtl; }
                    .container { background-color: #f5f5f5; padding: 20px; }
                    .message { background-color: white; padding: 15px; border-radius: 5px; }
                    .header { border-bottom: 2px solid #2196F3; margin-bottom: 10px; padding-bottom: 10px; }
                    .field { margin: 10px 0; }
                    .label { font-weight: bold; color: #2196F3; }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="message">
                        <div class="header">
                            <h2>پیام جدید</h2>
                        </div>
                        <div class="field">
                            <span class="label">از:</span> $sender
                        </div>
                        <div class="field">
                            <span class="label">دسته بندی:</span> $category
                        </div>
                        <div class="field">
                            <span class="label">زمان:</span> ${format.format(date)}
                        </div>
                        <div class="field" style="margin-top: 20px; padding: 10px; background-color: #f9f9f9; border-right: 3px solid #FF9800;">
                            <p>$text</p>
                        </div>
                    </div>
                </div>
            </body>
            </html>
        """.trimIndent()
    }

    private fun showToast(message: String) {
        val handler = android.os.Handler(Looper.getMainLooper())
        handler.post {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }
}
