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

typealias DeleteUserResponse = Response<Unit>

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repo: AuthRepository
): ViewModel() {
    private val _authState = MutableStateFlow<Boolean>(repo.currentUser == null)
    val authState: StateFlow<Boolean> = _authState.asStateFlow()

    private val _deleteUserState = MutableStateFlow<DeleteUserResponse>(Response.Idle)
    val deleteUserState: StateFlow<DeleteUserResponse> = _deleteUserState.asStateFlow()

    init {
        getAuthState()
    }

    private fun getAuthState() = viewModelScope.launch {
        repo.getAuthState().collect { isUserSignedOut ->
            _authState.value = isUserSignedOut
        }
    }

    fun signOut() = repo.signOut()

    fun deleteUser() = viewModelScope.launch {
        try {
            _deleteUserState.value = Response.Loading
            _deleteUserState.value = Response.Success(repo.deleteUser())
        } catch (e: Exception) {
            _deleteUserState.value = Response.Failure(e)
        }
    }
}