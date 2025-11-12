package com.suffixdigital.smartauthenticator.domain.model

/**
 * Created by Kirtikant Patadiya on 2025-11-10.
 */
data class OtpUiState(
    val loading: Boolean = false,
    val message: String? = null,
    val isVerified: Boolean = false,
    val verificationId: String? = null
)