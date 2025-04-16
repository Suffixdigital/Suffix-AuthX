package com.suffixdigital.smartauthenticator.viewmodels

import android.util.Patterns
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

typealias SignInResponse = Response<Unit>

@HiltViewModel
class SignInViewModel @Inject constructor(private val repo: AuthRepository) : ViewModel() {
    private val _email: MutableStateFlow<String> = MutableStateFlow("")
    val email: StateFlow<String> = _email.asStateFlow()

    private val _password: MutableStateFlow<String> = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _signInState = MutableStateFlow<SignInResponse>(Response.Idle)
    val signInState: StateFlow<SignInResponse> = _signInState.asStateFlow()

    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
    }

    fun signInWithEmailAndPassword() = viewModelScope.launch {
        try {
            _signInState.value = Response.Loading
            _signInState.value =
                Response.Success(
                    repo.signInWithEmailAndPassword(
                        email.value.trim(),
                        password.value.trim()
                    )
                )
        } catch (e: Exception) {
            _signInState.value = Response.Failure(e)
        }
    }

    fun isEmailValid(): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email.value).matches()
    }

    fun isPasswordValid(): Boolean {
        return password.value.length >= 6
    }



    val isEmailVerified get() = repo.currentUser?.isEmailVerified == true
}