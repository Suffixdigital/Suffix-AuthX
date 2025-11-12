package com.suffixdigital.smartauthenticator.domain.usecase

import com.google.firebase.auth.PhoneAuthCredential
import com.suffixdigital.smartauthenticator.domain.repository.OtpRepository
import javax.inject.Inject

/**
 * Created by Kirtikant Patadiya on 2025-11-10.
 */
class VerifyOtpUseCase @Inject constructor(
    private val repository: OtpRepository
) {
    operator fun invoke(credential: PhoneAuthCredential) =
        repository.signIn(credential)
}