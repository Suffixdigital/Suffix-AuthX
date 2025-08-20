package com.suffixdigital.smartauthenticator.utils

/**
 * Created by Kirtikant Patadiya on 2025-08-18.
 */

import android.content.Context
import android.content.pm.PackageManager
import android.util.Base64
import android.util.Log
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*

class AppSignatureHelper(private val context: Context) {

    companion object {
        private const val HASH_TYPE = "SHA-256"
        private const val NUM_BASE64_CHAR = 11
        private const val TAG = "AppSignatureHelper"
    }

    fun getAppSignatures(): List<String> {
        val appCodes = mutableListOf<String>()

        try {
            val packageName = context.packageName
            val packageManager = context.packageManager
            val signatures = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                packageManager.getPackageInfo(
                    packageName,
                    PackageManager.GET_SIGNING_CERTIFICATES
                ).signingInfo!!.apkContentsSigners
            } else {
                @Suppress("DEPRECATION")
                packageManager.getPackageInfo(
                    packageName,
                    PackageManager.GET_SIGNATURES
                ).signatures
            }

            signatures!!.forEach { signature ->
                val hash = hash(packageName, signature.toCharsString())
                if (hash != null) {
                    appCodes.add(hash)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Package not found", e)
        }

        return appCodes
    }

    private fun hash(packageName: String, signature: String): String? {
        val appInfo = "$packageName $signature"
        return try {
            val messageDigest = MessageDigest.getInstance(HASH_TYPE)
            messageDigest.update(appInfo.toByteArray(StandardCharsets.UTF_8))
            var hashSignature = Base64.encodeToString(
                messageDigest.digest(),
                Base64.NO_PADDING or Base64.NO_WRAP
            )
            hashSignature = hashSignature.substring(0, NUM_BASE64_CHAR)
            Log.d(TAG, "App hash: $hashSignature")
            hashSignature
        } catch (e: NoSuchAlgorithmException) {
            Log.e(TAG, "Hash error: ${e.message}", e)
            null
        }
    }
}

