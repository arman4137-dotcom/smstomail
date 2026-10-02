package com.example.smstomail

import java.text.SimpleDateFormat
import java.util.*

data class SmsMessage(
    val id: Int = 0,
    val sender: String,
    val text: String,
    val timestamp: Long,
    val category: MessageCategorizer.Category,
    val sent: Boolean = false
) {
    fun getFormattedTime(): String {
        val date = Date(timestamp)
        val format = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale("fa", "IR"))
        return format.format(date)
    }
}
