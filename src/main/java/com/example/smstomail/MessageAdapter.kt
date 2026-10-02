package com.example.smstomail

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*

class MessageAdapter(
    context: Context,
    private val messages: List<SmsMessage>
) : ArrayAdapter<SmsMessage>(context, 0, messages) {

    override fun getView(position: Int, convertView: android.view.View?, parent: ViewGroup): android.view.View {
        var view = convertView
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.message_item, parent, false)
        }

        val message = getItem(position)

        if (message != null) {
            val senderPhone = view!!.findViewById<TextView>(R.id.senderPhone)
            val messageCategory = view.findViewById<TextView>(R.id.messageCategory)
            val messageText = view.findViewById<TextView>(R.id.messageText)
            val messageTime = view.findViewById<TextView>(R.id.messageTime)

            senderPhone.text = message.sender
            messageCategory.text = message.category.displayName
            messageText.text = message.text
            messageTime.text = message.getFormattedTime()
        }

        return view!!
    }
}
