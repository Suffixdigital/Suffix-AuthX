package com.suffixdigital.smartauthenticator.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.PhoneAuthProvider
import com.suffixdigital.smartauthenticator.databinding.ActivityOtpVerifyBinding
import com.suffixdigital.smartauthenticator.viewmodels.OTPViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OtpVerifyActivity : AppCompatActivity() {

    private val viewModel: OTPViewModel by viewModels()
    private lateinit var binding: ActivityOtpVerifyBinding
    private var phoneNumber: String = ""
    private var verificationID: String = ""

    private lateinit var otpBoxes: Array<EditText>

    private var resendEnabled = false
    private var countdown = 30

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpVerifyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()
        setupViews()
        setObserver()
    }

    private fun initViews() {
        otpBoxes = arrayOf(
            binding.etC1,
            binding.etC2,
            binding.etC3,
            binding.etC4,
            binding.etC5,
            binding.etC6,
        )

        phoneNumber = intent.getStringExtra("phone")!!
        verificationID = intent.getStringExtra("verificationId")!!
    }

    private fun setupViews() {
        setupEditTextInput()
        startRateLimiter()
        // Show phone number
        binding.tvMaskedNumber.text = String.format("+91 •••• %s", phoneNumber.substring(6))
        // Resend OTP
        binding.tvResend.setOnClickListener {
            if (resendEnabled) {
                resendEnabled = false
                viewModel.sendOtp(phoneNumber, this)
            }
        }

        // Verify OTP
        binding.btnVerify.setOnClickListener {
            if (isOtpValid()) {
                val code = getOtpCode()
                viewModel.verifyCode(code,verificationID)
            } else {
                Toast.makeText(this, "OTP is not Valid!", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun isOtpValid(): Boolean {
        return otpBoxes.none { it.text.toString().trim().isEmpty() }
    }

    private fun getOtpCode(): String {
        return otpBoxes.joinToString("") { it.text.toString().trim() }
    }

    private fun setObserver() {
        // Observe state
        lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                binding.loadingProcess.progressCircular.visibility =
                    if (state.loading) View.VISIBLE else View.GONE

                if (state.isVerified) {
                    Toast.makeText(this@OtpVerifyActivity, "Success", Toast.LENGTH_LONG).show()
                    val intent = Intent(this@OtpVerifyActivity, HomeScreen::class.java).apply {
                        putExtra("SignInType", "PhoneSignIn")
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    startActivity(intent)
                }


                if (state.verificationId != null) {
                    countdown = 30
                    verificationID = state.verificationId
                    startRateLimiter()
                    Toast.makeText(this@OtpVerifyActivity, state.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun startRateLimiter() {
        resendEnabled = false
        lifecycleScope.launch {
            while (countdown > 0) {
                binding.tvResend.text = "Resend in ${countdown--}s"
                delay(1000)
            }
            binding.tvResend.text = "Resend"
            resendEnabled = true
        }
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

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })
        }
    }

    private fun checkOtpFilled() {
        val filled = otpBoxes.all { it.text.toString().isNotEmpty() }
        binding.btnVerify.isClickable = filled
    }

}
