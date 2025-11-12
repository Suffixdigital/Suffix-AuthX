package com.suffixdigital.smartauthenticator.domain.usecase

import android.app.Activity
import com.suffixdigital.smartauthenticator.domain.repository.OtpRepository
import javax.inject.Inject

/**
 * Created by Kirtikant Patadiya on 2025-11-10.
 */
class SendOtpUseCase @Inject constructor(
    private val repository: OtpRepository
) {
    operator fun invoke(phone: String, activity: Activity) =
        repository.sendOtp(phone, activity)
}