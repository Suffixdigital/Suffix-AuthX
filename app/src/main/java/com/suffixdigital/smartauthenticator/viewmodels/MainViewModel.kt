package com.suffixdigital.smartauthenticator.viewmodels

import androidx.lifecycle.ViewModel
import com.suffixdigital.smartauthenticator.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Created by Kirtikant Patadiya on 2025-04-15.
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val repo: AuthRepository
): ViewModel() {
    val isUserSignedOut get() = repo.currentUser == null

    val isEmailVerified get() = repo.currentUser?.isEmailVerified == true
}