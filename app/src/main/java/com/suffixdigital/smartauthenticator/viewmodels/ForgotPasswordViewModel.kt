package com.suffixdigital.smartauthenticator.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.suffixdigital.smartauthenticator.domain.model.Response
import com.suffixdigital.smartauthenticator.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by Kirtikant Patadiya on 2025-04-16.
 */
typealias PasswordResetEmailResponse = Response<Unit>

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val repo: AuthRepository
): ViewModel() {
    private val _email = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _passwordResetEmailState = MutableStateFlow<PasswordResetEmailResponse>(Response.Idle)
    val passwordResetEmailState: StateFlow<PasswordResetEmailResponse> = _passwordResetEmailState.asStateFlow()

    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
    }

    fun sendPasswordResetEmail(email: String) = viewModelScope.launch {
        try {
            _passwordResetEmailState.value = Response.Loading
            _passwordResetEmailState.value = Response.Success(repo.sendPasswordResetEmail(email))
        } catch (e: Exception) {
            _passwordResetEmailState.value = Response.Failure(e)
        }
    }
}