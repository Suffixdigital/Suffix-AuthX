package com.suffixdigital.smartauthenticator.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthProvider
import com.suffixdigital.smartauthenticator.core.TAG
import com.suffixdigital.smartauthenticator.databinding.ActivityOtpSendBinding
import com.suffixdigital.smartauthenticator.utils.AppSignatureHelper
import com.suffixdigital.smartauthenticator.viewmodels.OTPViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class OtpSendActivity : AppCompatActivity() {

    private val viewModel: OTPViewModel by viewModels()
    // ViewBinding to access views in the layout
    private lateinit var binding: ActivityOtpSendBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtpSendBinding.inflate(layoutInflater)
        setContentView(binding.root)
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
                    viewModel.sendOtp(phoneNumber, this)
                }
            }
        }

        lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                binding.loadingProcess.progressCircular.visibility = if (state.loading) View.VISIBLE else View.GONE
//                tvStatus.text = state.message ?: ""

                if (state.verificationId != null) {
                    Toast.makeText(this@OtpSendActivity, state.message, Toast.LENGTH_LONG).show()
                    val phoneNumber = binding.etPhone.text.toString().trim()
                    val intent = Intent(this@OtpSendActivity, OtpVerifyActivity::class.java)
                    intent.putExtra("phone", phoneNumber)
                    intent.putExtra("verificationId", state.verificationId)
                    startActivity(intent)
                }
            }
        }
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = currentFocus ?: View(this)
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }
}