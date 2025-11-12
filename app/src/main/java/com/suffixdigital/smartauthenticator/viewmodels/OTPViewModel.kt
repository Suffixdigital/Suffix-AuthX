package com.suffixdigital.smartauthenticator.viewmodels

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.suffixdigital.smartauthenticator.domain.model.OtpUiState
import com.suffixdigital.smartauthenticator.domain.usecase.SendOtpUseCase
import com.suffixdigital.smartauthenticator.domain.usecase.VerifyOtpUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by Kirtikant Patadiya on 2025-11-10.
 */

@HiltViewModel
/**
 * New ViewModel API (Lifecycle 2.8.x)
 * -----------------------------------
 * ViewModel(scope: CoroutineScope, vararg closeables: AutoCloseable)
 *
 * The `scope` is injected explicitly.
 */
class OTPViewModel @Inject constructor(
    private val scope: CoroutineScope,
    private val sendOtpUseCase: SendOtpUseCase,
    private val verifyOtpUseCase: VerifyOtpUseCase
) : ViewModel(scope) {

    private val _uiState = MutableStateFlow(OtpUiState())
    val uiState: StateFlow<OtpUiState> = _uiState

    private var lastVerificationId: String? = null

    fun sendOtp(phone: String, activity: Activity) {
        _uiState.value = OtpUiState(loading = true, message = "Sending OTP...")

        scope.launch {
            sendOtpUseCase(phone, activity).collect { result ->
                result.fold(
                    onSuccess = { value ->
                        when (value) {
                            is PhoneAuthCredential -> {
                                _uiState.value = _uiState.value.copy(
                                    loading = false,
                                    message = "Auto verified",
                                )
                                Log.d("OTPViewModel", "Auto verified with credential: $phone $value")
                                verifyCredential(value)
                            }
                            is Pair<*, *> -> {
                                lastVerificationId = value.first as String
                                _uiState.value = _uiState.value.copy(
                                    loading = false,
                                    verificationId = lastVerificationId,
                                    message = "OTP Sent"
                                )
                                Log.d("OTPViewModel", "OTP sent to $phone $lastVerificationId")
                            }
                        }
                    },
                    onFailure = {
                        _uiState.value = _uiState.value.copy(
                            loading = false,
                            message = it.localizedMessage
                        )
                    }
                )
            }
        }
    }

    fun verifyCode(code: String,verificationID: String) {
        Log.d("OTPViewModel","verificationID: $verificationID code: $code")
        val credential = PhoneAuthProvider.getCredential(verificationID, code)
        verifyCredential(credential)
    }

    private fun verifyCredential(credential: PhoneAuthCredential) {
        _uiState.value = _uiState.value.copy(loading = true, message = "Verifying...")

        verifyOtpUseCase(credential).addOnCompleteListener { task ->
            scope.launch {
                if (task.isSuccessful) {
                    _uiState.value = _uiState.value.copy(
                        loading = false,
                        isVerified = true,
                        message = "Success"
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        loading = false,
                        message = task.exception?.localizedMessage
                    )
                }
            }
        }
    }
}