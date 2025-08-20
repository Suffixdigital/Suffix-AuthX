package com.suffixdigital.smartauthenticator.receiver

/**
 * Created by Kirtikant Patadiya on 2025-08-18.
 */
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import com.suffixdigital.smartauthenticator.core.TAG

class SmsBroadcastReceiver : BroadcastReceiver() {

    var otpListener: ((String) -> Unit)? = null

    override fun onReceive(context: Context?, intent: Intent?) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent?.action) {
            val extras = intent.extras
            val status = extras?.get(SmsRetriever.EXTRA_STATUS) as? Status

            when (status?.statusCode) {
                CommonStatusCodes.SUCCESS -> {
                    val message = extras.get(SmsRetriever.EXTRA_SMS_MESSAGE) as String
                    // Extract 6-digit OTP
                    val otp = Regex("(\\d{6})").find(message)?.value
                    if (!otp.isNullOrEmpty()) {
                        Log.e(TAG, "SMS Retriever received otp: $otp")
                        otpListener?.invoke(otp)
                    }
                }
                CommonStatusCodes.TIMEOUT -> {
                    Log.e(TAG, "SMS Retriever timed out")
                }
            }
        }
    }
}
