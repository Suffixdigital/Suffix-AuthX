package com.suffixdigital.smartauthenticator.ui

import android.R.attr.visibility
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.suffixdigital.smartauthenticator.core.TAG
import com.suffixdigital.smartauthenticator.databinding.ActivityOtpVerifyBinding
import com.suffixdigital.smartauthenticator.receiver.SmsBroadcastReceiver
import java.util.concurrent.TimeUnit

class OtpVerifyActivity : AppCompatActivity(){
    private lateinit var binding: ActivityOtpVerifyBinding
    private lateinit var mAuth: FirebaseAuth
    private var verificationId: String? = null

    private lateinit var otpBoxes: Array<EditText>
    private lateinit var smsBroadcastReceiver: SmsBroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpVerifyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mAuth = FirebaseAuth.getInstance()

        otpBoxes = arrayOf(
            binding.etC1,
            binding.etC2,
            binding.etC3,
            binding.etC4,
            binding.etC5,
            binding.etC6,
        )
        setupEditTextInput()

        // Show phone number
        binding.tvMaskedNumber.text = String.format("+91 •••• %s", intent.getStringExtra("phone")!!.substring(6))

        verificationId = intent.getStringExtra("verificationId")

        // 🔹 Check if OTP was passed directly (auto-read by Firebase or SmsRetriever)
        val autoOtp = intent.getStringExtra("autoOtp")
        if (!autoOtp.isNullOrEmpty()) {
            Log.e(TAG,"autoOtp: $autoOtp  otp.length: ${autoOtp.length} == otpBoxes.size: ${otpBoxes.size}")
            autoFillOtp(autoOtp) // Fill in all the EditTexts immediately
            //verifyCode(autoOtp) // Auto-verify without waiting
        }

        // Resend OTP
        binding.tvResend.setOnClickListener { resendOtp() }

        // Verify OTP
        binding.btnVerify.setOnClickListener {
            if (isOtpValid()) {
                binding.loadingProcess.progressCircular.visibility = View.VISIBLE
                binding.btnVerify.visibility = View.INVISIBLE
                val code = getOtpCode()
                verificationId?.let { id ->
                    val credential = PhoneAuthProvider.getCredential(id, code)
                    signInWithCredential(credential)
                }
            } else {
                Toast.makeText(this, "OTP is not Valid!", Toast.LENGTH_SHORT).show()
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStart() {
        super.onStart()
        smsBroadcastReceiver = SmsBroadcastReceiver()
        smsBroadcastReceiver.otpListener = { otp ->
            autoFillOtp(otp)
            if (isOtpValid()) {
                binding.loadingProcess.progressCircular.visibility = View.VISIBLE
                binding.btnVerify.visibility = View.INVISIBLE
                val code = getOtpCode()
                verificationId?.let { id ->
                    val credential = PhoneAuthProvider.getCredential(id, code)
                    signInWithCredential(credential)
                }
            } else {
                Toast.makeText(this, "OTP is not Valid!", Toast.LENGTH_SHORT).show()
            }
        }
        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        registerReceiver(smsBroadcastReceiver, intentFilter,RECEIVER_EXPORTED)
        startSmsRetriever()
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(smsBroadcastReceiver)
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

    private fun isOtpValid(): Boolean {
        return otpBoxes.none { it.text.toString().trim().isEmpty() }
    }

    private fun getOtpCode(): String {
        return otpBoxes.joinToString("") { it.text.toString().trim() }
    }

    private fun signInWithCredential(credential: PhoneAuthCredential) {
        FirebaseAuth.getInstance().signInWithCredential(credential)
            .addOnCompleteListener { task ->
                binding.btnVerify.visibility = View.VISIBLE
                binding.loadingProcess.progressCircular.visibility = View.GONE
                if (task.isSuccessful) {
                    Toast.makeText(this, "Welcome...", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, HomeScreen::class.java).apply {
                        putExtra("SignInType", "PhoneSignIn")
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "OTP is not Valid!", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun resendOtp() {
        binding.loadingProcess.progressCircular.visibility = View.VISIBLE
        binding.btnVerify.visibility = View.INVISIBLE

        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {}
            override fun onVerificationFailed(e: FirebaseException) {
                binding.loadingProcess.progressCircular.visibility = View.GONE
                binding.btnVerify.visibility = View.VISIBLE
                Toast.makeText(this@OtpVerifyActivity, e.localizedMessage, Toast.LENGTH_SHORT).show()
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                binding.loadingProcess.progressCircular.visibility = View.GONE
                binding.btnVerify.visibility = View.VISIBLE
                this@OtpVerifyActivity.verificationId = verificationId
                Toast.makeText(this@OtpVerifyActivity, "OTP is successfully sent.", Toast.LENGTH_SHORT).show()
            }
        }

        val options = PhoneAuthOptions.newBuilder(mAuth)
            .setPhoneNumber("+91${intent.getStringExtra("phone")!!.trim()}")
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(this)
            .setCallbacks(callbacks)
            .build()

        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    private fun setupEditTextInput() {
        for (i in otpBoxes.indices) {
            otpBoxes[i].addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (i == 0 && s?.length ?: 0 > 1) {
                        // Handle paste into first box
                        val pasted = s.toString().take(otpBoxes.size)
                        for (j in pasted.indices) {
                            otpBoxes[j].setText(pasted[j].toString())
                        }
                        otpBoxes.last().requestFocus()
                    } else {
                        // Move forward
                        if (s?.length == 1 && i < otpBoxes.size - 1) {
                            otpBoxes[i + 1].requestFocus()
                        }
                        // Move backward
                        else if (s?.isEmpty() == true && i > 0) {
                            otpBoxes[i - 1].requestFocus()
                        }
                    }
                    checkOtpFilled()
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        }
    }

    private fun checkOtpFilled() {
        val filled = otpBoxes.all { it.text.toString().isNotEmpty() }
        binding.btnVerify.isClickable = filled
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = currentFocus ?: View(this)
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun autoFillOtp(otp: String) {
        if (otp.length == otpBoxes.size) {
            otp.forEachIndexed { i, c ->
                otpBoxes[i].setText(c.toString())
            }
            hideKeyboard()
            checkOtpFilled()
        }
    }


}
