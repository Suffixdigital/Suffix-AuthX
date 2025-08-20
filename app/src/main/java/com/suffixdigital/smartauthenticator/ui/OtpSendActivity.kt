package com.suffixdigital.smartauthenticator.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.suffixdigital.smartauthenticator.core.TAG
import com.suffixdigital.smartauthenticator.databinding.ActivityOtpSendBinding
import com.suffixdigital.smartauthenticator.utils.AppSignatureHelper
import java.util.concurrent.TimeUnit

class OtpSendActivity : AppCompatActivity() {

    // ViewBinding to access views in the layout
    private lateinit var binding: ActivityOtpSendBinding

    // Firebase Authentication instance
    private lateinit var mAuth: FirebaseAuth

    // Callback for phone authentication
    private lateinit var mCallbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpSendBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance()

        val appSignatureHelper = AppSignatureHelper(this)
        val appHashes = appSignatureHelper.getAppSignatures()
        Log.d(TAG, "App hash for SMS: $appHashes")

        // Set click listener on the Send button
        binding.btnSend.setOnClickListener {
            val phoneNumber = binding.etPhone.text.toString().trim()
            when {
                phoneNumber.isEmpty() -> {
                    Toast.makeText(this, "Invalid Phone Number", Toast.LENGTH_SHORT).show()
                }
                phoneNumber.length != 10 -> {
                    Toast.makeText(this, "Type valid Phone Number", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    hideKeyboard()
                    sendOtp(phoneNumber)
                }
            }
        }
    }


    private fun sendOtp(phone: String) {
        val options = PhoneAuthOptions.newBuilder(mAuth)
            .setPhoneNumber("+91$phone")
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                    // ✅ Auto-verification succeeded (instant or SMS auto-retrieval)

                    val smsCode = credential.smsCode
                    if (smsCode != null) {
                        // Firebase auto-read success
                        goToOtpVerify(phone, credential.provider, smsCode)
                        Toast.makeText(this@OtpSendActivity, "onVerificationCompleted smsCode: ${credential.smsCode}", Toast.LENGTH_SHORT).show()

                    } else {
                        // Instant verification (no code needed)
                       // signInWithCredential(credential)
                    }
                }

                override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
                    super.onCodeSent(verificationId, token)
                    //this@OtpSendActivity.verificationId = verificationId

                    // Start SMS Retriever API as fallback
                    startSmsRetriever()

                    goToOtpVerify(phone, verificationId, null)
                }

                override fun onVerificationFailed(e: FirebaseException) {
                    Toast.makeText(this@OtpSendActivity, "Verification failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }).build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun goToOtpVerify(phone: String, verificationId: String?, autoOtp: String?) {
        val intent = Intent(this, OtpVerifyActivity::class.java).apply {
            putExtra("phone", phone)
            putExtra("verificationId", verificationId)
            autoOtp?.let { putExtra("autoOtp", it) }
        }
        startActivity(intent)
    }


    private fun startSmsRetriever() {
        val client = SmsRetriever.getClient(this)
        val task = client.startSmsRetriever()
        task.addOnSuccessListener {
            Log.d(TAG, "SMS Retriever started")
        }
        task.addOnFailureListener {
            Log.e(TAG, "Failed to start SMS Retriever", it)
        }
    }


    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("OTP", "signInWithCredential:success")
                    // Navigate to home/dashboard
                } else {
                    Log.w("OTP", "signInWithCredential:failure", task.exception)
                    Toast.makeText(this, "Verification failed", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = currentFocus ?: View(this)
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }


}