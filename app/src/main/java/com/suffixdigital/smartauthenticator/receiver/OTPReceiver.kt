package com.suffixdigital.smartauthenticator.receiver

/**
 * Created by Kirtikant Patadiya on 2025-04-09.
 */
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import java.util.regex.Pattern

class OTPReceiver : BroadcastReceiver() {

    private var otpReceiverListener: OtpReceiverListener? = null

    // Function to set the listener
    fun initListener(otpReceiverListener: OtpReceiverListener) {
        this.otpReceiverListener = otpReceiverListener
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        // Check if the received intent action matches the SMS retriever action
        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent?.action) {
            val bundle = intent.extras
            if (bundle != null) {
                val status = bundle.get(SmsRetriever.EXTRA_STATUS) as Status?
                status?.let {
                    when (it.statusCode) {
                        CommonStatusCodes.SUCCESS -> {
                            // SMS message retrieved successfully
                            val message = bundle.getString(SmsRetriever.EXTRA_SMS_MESSAGE)
                            message?.let {
                                // Extract the OTP from the message using regex
                                val pattern = Pattern.compile("\\d{6}")
                                val matcher = pattern.matcher(it)
                                if (matcher.find()) {
                                    val myOtp = matcher.group(0)
                                    otpReceiverListener?.onOtpSuccess(myOtp)
                                } else {
                                    // If no OTP found, trigger the timeout callback
                                    otpReceiverListener?.onOtpTimeout()
                                }
                            }
                        }
                        CommonStatusCodes.TIMEOUT -> {
                            // Trigger the timeout callback if SMS retrieval times out
                            otpReceiverListener?.onOtpTimeout()
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    interface OtpReceiverListener {
        // Called when OTP is successfully received
        fun onOtpSuccess(otp: String)

        // Called when there is a timeout in receiving OTP
        fun onOtpTimeout()
    }
}