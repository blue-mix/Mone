package com.example.money.receiver



import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import android.widget.Toast

class SmsReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION == intent.action) {
            for (smsMessage in Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                val sender = smsMessage.displayOriginatingAddress  // Get sender phone number
                val messageBody = smsMessage.messageBody  // Get SMS content
                Toast.makeText(context, "New SMS from $sender: $messageBody", Toast.LENGTH_SHORT).show()

                Log.d("SmsReceiver", "SMS received from: $sender")
                Log.d("SmsReceiver", "Message: $messageBody")

            }
        }
    }
}
