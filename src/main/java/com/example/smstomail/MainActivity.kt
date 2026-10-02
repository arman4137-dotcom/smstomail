package com.example.smstomail

import android.Manifest
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Build
import android.os.Bundle
import android.provider.Telephony
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : AppCompatActivity() {

    private lateinit var prefs: PreferencesManager

    private lateinit var enableSwitch: Switch
    private lateinit var emailOneInput: EditText
    private lateinit var emailTwoInput: EditText
    private lateinit var emailPasswordInput: EditText
    private lateinit var saveButton: Button
    private lateinit var messageList: ListView

    private val PERMISSION_REQUEST_CODE = 123

    private val permissions = arrayOf(
        Manifest.permission.RECEIVE_SMS,
        Manifest.permission.READ_SMS,
        Manifest.permission.INTERNET
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prefs = PreferencesManager(this)

        initializeViews()
        loadSettings()
        loadMessageHistory()
        setupPermissions()
        setupListeners()
    }

    private fun initializeViews() {
        enableSwitch = findViewById(R.id.enableSwitch)
        emailOneInput = findViewById(R.id.emailOne)
        emailTwoInput = findViewById(R.id.emailTwo)
        emailPasswordInput = findViewById(R.id.emailPassword)
        saveButton = findViewById(R.id.saveButton)
        messageList = findViewById(R.id.messageList)
    }

    private fun loadSettings() {
        enableSwitch.isChecked = prefs.isForwardingEnabled()
        emailOneInput.setText(prefs.getEmailOne())
        emailTwoInput.setText(prefs.getEmailTwo())
        emailPasswordInput.setText(prefs.getEmailPassword())
    }

    private fun setupPermissions() {
        val needsPermissions = mutableListOf<String>()

        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                != PackageManager.PERMISSION_GRANTED
            ) {
                needsPermissions.add(permission)
            }
        }

        if (needsPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this,
                needsPermissions.toTypedArray(),
                PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST_CODE) {
            var allGranted = true
            for (result in grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false
                    break
                }
            }

            if (!allGranted) {
                Toast.makeText(this, getString(R.string.permission_denied), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupListeners() {
        saveButton.setOnClickListener {
            saveSettings()
        }
    }

    private fun saveSettings() {
        val emailOne = emailOneInput.text.toString().trim()
        val emailTwo = emailTwoInput.text.toString().trim()
        val password = emailPasswordInput.text.toString().trim()

        if (emailOne.isEmpty() || emailTwo.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "تمام فیلد ها را پر کنید", Toast.LENGTH_SHORT).show()
            return
        }

        prefs.setEmailOne(emailOne)
        prefs.setEmailTwo(emailTwo)
        prefs.setEmailPassword(password)
        prefs.setSenderEmail(emailOne)
        prefs.setForwardingEnabled(enableSwitch.isChecked)

        Toast.makeText(this, "تنظیمات ذخیره شد", Toast.LENGTH_SHORT).show()
    }

    private fun loadMessageHistory() {
        val messages = mutableListOf<SmsMessage>()

        try {
            val cursor: Cursor? = contentResolver.query(
                Telephony.Sms.CONTENT_URI,
                arrayOf(
                    Telephony.Sms._ID,
                    Telephony.Sms.ADDRESS,
                    Telephony.Sms.BODY,
                    Telephony.Sms.DATE,
                    Telephony.Sms.TYPE
                ),
                null,
                null,
                "${Telephony.Sms.DATE} DESC LIMIT 50"
            )

            cursor?.use { c ->
                while (c.moveToNext()) {
                    val id = c.getInt(c.getColumnIndexOrThrow(Telephony.Sms._ID))
                    val address = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.ADDRESS))
                    val body = c.getString(c.getColumnIndexOrThrow(Telephony.Sms.BODY))
                    val date = c.getLong(c.getColumnIndexOrThrow(Telephony.Sms.DATE))

                    val category = MessageCategorizer.categorizeMessage(address, body)

                    messages.add(
                        SmsMessage(
                            id = id,
                            sender = address,
                            text = body,
                            timestamp = date,
                            category = category
                        )
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val adapter = MessageAdapter(this, messages)
        messageList.adapter = adapter
    }
}
