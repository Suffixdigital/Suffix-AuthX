package com.suffixdigital.smartauthenticator.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suffixdigital.smartauthenticator.core.EMPTY_STRING
import com.suffixdigital.smartauthenticator.domain.model.Response
import com.suffixdigital.smartauthenticator.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

typealias SignUpResponse = Response<Unit>
typealias EmailVerificationResponse = Response<Unit>

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val repo: AuthRepository
) : ViewModel() {
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()


    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _signUpState = MutableStateFlow<SignUpResponse>(Response.Idle)
    val signUpState: StateFlow<SignUpResponse> = _signUpState.asStateFlow()

    private val _emailVerificationState = MutableStateFlow<EmailVerificationResponse>(Response.Idle)
    val emailVerificationState: StateFlow<EmailVerificationResponse> =
        _emailVerificationState.asStateFlow()

    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
    }

    fun signUpWithEmailAndPassword() = viewModelScope.launch {
        _isLoading.value = true
        try {
            _signUpState.value = Response.Loading
            _signUpState.value = Response.Success(
                repo.signUpWithEmailAndPassword(
                    email.value.toString(),
                    password.value.toString()
                )
            )
        } catch (e: Exception) {
            _signUpState.value = Response.Failure(e)
            _isLoading.value = false
        }
    }

    fun sendEmailVerification() = viewModelScope.launch {
        try {
            _emailVerificationState.value = Response.Loading
            _emailVerificationState.value = Response.Success(repo.sendEmailVerification())
        } catch (e: Exception) {
            _emailVerificationState.value = Response.Failure(e)
        } finally {
            _isLoading.value = false
        }
    }
}