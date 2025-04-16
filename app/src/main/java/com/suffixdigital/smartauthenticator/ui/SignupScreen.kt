package com.suffixdigital.smartauthenticator.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.util.PatternsCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.suffixdigital.smartauthenticator.R
import com.suffixdigital.smartauthenticator.core.checkOffensiveWord
import com.suffixdigital.smartauthenticator.core.inputFilter
import com.suffixdigital.smartauthenticator.core.isPasswordPatternValid
import com.suffixdigital.smartauthenticator.core.phonePattern
import com.suffixdigital.smartauthenticator.core.trimPhoneNumber
import com.suffixdigital.smartauthenticator.databinding.ActivitySignupScreenBinding
import com.suffixdigital.smartauthenticator.domain.model.Response
import com.suffixdigital.smartauthenticator.ui.SignInScreen
import com.suffixdigital.smartauthenticator.viewmodels.SignUpViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignupScreen : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivitySignupScreenBinding

    val viewModel: SignUpViewModel by viewModels()

    /******************************************************************************
     * [textInputLayoutList] instance is use to store [TextInputLayout] data.
     *****************************************************************************/
    private lateinit var textInputLayoutList: MutableList<TextInputLayout>

    /******************************************************************************
     * [isEmailValid] is a boolean variable to validate does entered Email-ID is valid or not. Default value is 'False'
     *****************************************************************************/
    private var isEmailValid = false

    /******************************************************************************
     * [isPasswordPatternValid] is a boolean variable to validate does entered Password is valid or not. Default value is 'False'
     *****************************************************************************/
    private var isPasswordValid: Boolean = false

    /******************************************************************************
     * [phonePatternLength] is a total length of Phone Number which needs to insert in [TextInputEditText] by user.
     * [phonePattern] is defined in 'Constant' class
     *****************************************************************************/
    private val phonePatternLength = phonePattern.length



    /******************************************************************************
     * [emailIDTextWatcher] is a instance of [TextInputEditText]. Here [TextInputEditText] has overide class [TextWatcher] and it has
     * below over-ride functions to real time validate inserted data in [TextInputEditText]. Here This [TextWatcher] class is validate
     * entered Email-ID is valid or not. If Email-ID is not valid then display Error message to user
     *****************************************************************************/
    private val emailIDTextWatcher = object : TextWatcher {
        /******************************************************************************
         * [beforeTextChanged], No need to use this over-ride function here.
         *****************************************************************************/
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        /******************************************************************************
         * [onTextChanged], No need to use this over-ride function here.
         *****************************************************************************/
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        /******************************************************************************
         * [afterTextChanged], When user insert/delete character by character in [TextInputEditText] this function is validate does
         * Email-ID is empty or not also Email-ID is with valid pattern or not. If Email is valid then [isEmailValid] becomes true but if
         * Email-ID is not valid then display Error message to user. Once FirstName, LastName, Email and Phone are valid then 'Signup' button
         * becomes enable and Change button color and text color.
         *****************************************************************************/
        override fun afterTextChanged(editable: Editable?) {
            viewModel.onEmailChange(editable.toString())
            editable?.let {
                binding.etEmailId.setSelection(it.length)
            }
            when {
                /******************************************************************************
                 * If Email-ID empty then display error message to user. [isEmailValid] becomes a false
                 *****************************************************************************/
                editable.toString().isEmpty() -> {
                    isEmailValid = false
                    displayErrorMessage(
                        binding.tilEmailId,
                        binding.etEmailId,
                        resources.getString(R.string.err_empty_email)
                    )
                }
                /******************************************************************************
                 * If Email-ID is not valid then display error message to user. [isEmailValid] becomes a false
                 *****************************************************************************/
                !PatternsCompat.EMAIL_ADDRESS.matcher(editable.toString()).matches() -> {
                    isEmailValid = false
                    displayErrorMessage(
                        binding.tilEmailId,
                        binding.etEmailId,
                        resources.getString(R.string.err_invalid_email)
                    )
                }
                /******************************************************************************
                 * If Email-ID is valid then [isEmailValid] becomes a True. The Signup button becomes enable and change button's background
                 * color and text color.
                 *****************************************************************************/
                else -> {
                    isEmailValid = true
                    binding.tilEmailId.isErrorEnabled = false
                    enableSignupButton()
                }
            }
        }
    }


    /******************************************************************************
     * [passwordTextWatcher] is a instance of [TextInputEditText]. Here [TextInputEditText] has overide class [TextWatcher] and it has
     * below over-ride functions to real time validate inserted data in [TextInputEditText]. Here This [TextWatcher] class is validate
     * entered Password is valid or not. If Password is not valid then display Error message to user
     *****************************************************************************/
    private val passwordTextWatcher = object : TextWatcher {
        /******************************************************************************
         * [beforeTextChanged], No need to use this over-ride function here.
         *****************************************************************************/
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        /******************************************************************************
         * [onTextChanged], When user insert/delete character by character in [TextInputEditText] this function is validate does
         * Password is empty or not also Password is with valid pattern or not. If Password is valid then [isPasswordPatternValid] becomes true but if
         * Password is not valid then display Error message to user. Once FirstName, LastName, Phone, Password and VerifyPassword are valid then 'Activate Account' button becomes enable
         * and Change button color and text color.
         *****************************************************************************/
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            viewModel.onPasswordChange(s.trim().toString().toString())
            s.let {
                binding.etPassword.setSelection(it.length)
            }
            when {
                /******************************************************************************
                 * If Password is empty then display error message to user. [isPasswordPatternValid] becomes a false
                 *****************************************************************************/
                s.trim().toString().trim().isEmpty() -> {
                    isPasswordValid = false
                    displayErrorMessage(
                        binding.tilPassword,
                        binding.etPassword,
                        resources.getString(R.string.err_empty_password)
                    )
                }
                /******************************************************************************
                 * [isPasswordPatternValid] function is check user entered/deleted data is valid with required pattern are follow or not.
                 * Here for inserting password required at least one lower case letter, one upper case letter, number, special character and required password length between 8 to 32.
                 * If all the pattern not follow then display error message to user
                 *****************************************************************************/
                !isPasswordPatternValid(s.trim().toString()) -> {
                    isPasswordValid = false
                    displayErrorMessage(
                        binding.tilPassword,
                        binding.etPassword,
                        resources.getString(R.string.err_password_pattern_invalid)
                    )
                }
                /******************************************************************************
                 * If entered/deleted Password is with valid pattern then [isPasswordPatternValid] becomes true. Here password and verify password
                 * both are matched then 'Activate Account' button becomes enable and change background color and text color.
                 *****************************************************************************/
                else -> {
                    isPasswordValid = true
                    binding.tilPassword.isErrorEnabled = false
                    /******************************************************************************
                     * password and verify password are same
                     *****************************************************************************/
                    if (isPasswordValid && binding.etVerifyPassword.text?.trim().toString()
                            .isNotEmpty() && s.trim()
                            .toString() == binding.etVerifyPassword.text?.trim().toString()
                    ) {
                        binding.tilVerifyPassword.isErrorEnabled = false
                        enableSignupButton()
                    }
                    /******************************************************************************
                     * password and verify password are not same
                     *****************************************************************************/
                    else {
                        displayErrorMessage(
                            binding.tilVerifyPassword,
                            binding.etVerifyPassword,
                            resources.getString(R.string.err_password_not_matched)
                        )
                    }
                }
            }
        }

        /******************************************************************************
         * [afterTextChanged], No need to use this over-ride function here.
         *****************************************************************************/
        override fun afterTextChanged(p0: Editable?) {

        }

    }

    /******************************************************************************
     * [verifyPasswordTextWatcher] is a instance of [TextInputEditText]. Here [TextInputEditText] has overide class [TextWatcher] and it has
     * below over-ride functions to real time validate inserted data in [TextInputEditText]. Here This [TextWatcher] class is validate
     * entered VerifyPassword is valid or not. If Verify Password is not valid then display Error message to user
     *****************************************************************************/
    private val verifyPasswordTextWatcher = object : TextWatcher {
        /******************************************************************************
         * [beforeTextChanged], No need to use this over-ride function here.
         *****************************************************************************/
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            if (s.trim().toString().trim().isEmpty()) {
                displayErrorMessage(
                    binding.tilVerifyPassword,
                    binding.etVerifyPassword,
                    resources.getString(R.string.err_empty_verify_password)
                )
            } else {
                if (isPasswordValid && binding.etPassword.text?.trim().toString()
                        .isNotEmpty() && s.trim()
                        .toString() == binding.etPassword.text?.trim().toString()
                ) {
                    binding.tilVerifyPassword.isErrorEnabled = false
                    enableSignupButton()
                } else {
                    displayErrorMessage(
                        binding.tilVerifyPassword,
                        binding.etVerifyPassword,
                        resources.getString(R.string.err_password_not_matched)
                    )
                }
            }
        }

        /******************************************************************************
         * [afterTextChanged], No need to use this over-ride function here.
         *****************************************************************************/
        override fun afterTextChanged(p0: Editable?) {}

    }

    /******************************************************************************
     * [enableSignupButton] function is enable 'Signup' button when user insert valid FirstName,
     * LastName, Phone Number and EmailID. Here in this function also change Signup button's background color
     * and Text color.
     *****************************************************************************/
    private fun enableSignupButton() {
        if (isEmailValid && isPasswordValid) {
            binding.btnSignup.isEnabled = true
            binding.btnSignup.alpha = 1.0f
            binding.btnSignup.setTextColor(ContextCompat.getColor(this@SignupScreen, R.color.white))
            binding.btnSignup.setBackgroundColor(
                ContextCompat.getColor(
                    this@SignupScreen,
                    R.color.light_blue
                )
            )
        }
    }

    /******************************************************************************
     * This function is display error message to user for [TextInputEditText]. When user inserting/deleting
     * FirstName or LastName or Email or Phone and it are wrong pattern or invalid or empty then display Error message to user.
     * Here 'Signup' button make disable and its background color and text color also change.
     *
     * @param textInputLayout : [TextInputLayout] for FirstName or LastName or Email or Phone.
     * @param textInputEditText [TextInputEditText] for FirstName or LastName or Email or Phone.
     * @param errorMessage : Error message based on validation for FirstName or LastName or Email or Phone.
     *****************************************************************************/
    private fun displayErrorMessage(
        textInputLayout: TextInputLayout,
        textInputEditText: TextInputEditText,
        errorMessage: String
    ) {
        textInputLayout.errorIconDrawable = null
        textInputEditText.isCursorVisible = true
        textInputLayout.error = errorMessage

        binding.btnSignup.alpha = 0.65f
        binding.btnSignup.isEnabled = false
        binding.btnSignup.setTextColor(
            ContextCompat.getColor(
                this@SignupScreen,
                R.color.light_black
            )
        )
        binding.btnSignup.setBackgroundColor(
            ContextCompat.getColor(
                this@SignupScreen,
                R.color.light_gray
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
        setupViews()
    }

    private fun initViews() {
        textInputLayoutList = mutableListOf(
            binding.tilEmailId,
            binding.tilPassword,
            binding.tilVerifyPassword
        )

        binding.etEmailId.filters = arrayOf(inputFilter)

        binding.etEmailId.addTextChangedListener(emailIDTextWatcher)
        binding.etPassword.addTextChangedListener(passwordTextWatcher)
        binding.etVerifyPassword.addTextChangedListener(verifyPasswordTextWatcher)

        binding.btnSignup.setOnClickListener(this)
        binding.btnLogin.setOnClickListener(this)

    }

    private fun setupViews() {
        // Observe state
        lifecycleScope.launchWhenStarted {
            viewModel.email.collect { binding.etEmailId.setText(it) }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.password.collect { binding.etPassword.setText(it) }
        }

        // Observe isLoading state
        lifecycleScope.launchWhenStarted {
            viewModel.isLoading.collect { isLoading ->
                binding.loadingProcess.progressCircular.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }

        // Observe sign-up response
        lifecycleScope.launchWhenStarted {
            viewModel.signUpState.collect { response ->
                when (response) {
                    is Response.Success -> {
                        Toast.makeText(this@SignupScreen, getString(R.string.account_created_message), Toast.LENGTH_SHORT).show()
                        viewModel.sendEmailVerification()
                    }
                    is Response.Failure -> {
                        val error = response.e?.message ?: "Sign up failed"
                        Log.e("SignUp", error)
                        Toast.makeText(this@SignupScreen, error, Toast.LENGTH_SHORT).show()
                    }
                    else -> {}
                }
            }
        }

        // Observe email verification response
        lifecycleScope.launchWhenStarted {
            viewModel.emailVerificationState.collect { response ->
                when (response) {
                    is Response.Success -> {
                        Toast.makeText(this@SignupScreen, getString(R.string.email_verification_sent_message), Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@SignupScreen, VerifyEmailScreen::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        })
                    }
                    is Response.Failure -> {
                        val error = response.e?.message ?: "Verification failed"
                        Log.e("EmailVerification", error)
                        Toast.makeText(this@SignupScreen, error, Toast.LENGTH_SHORT).show()
                    }
                    else -> {}
                }
            }
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btn_signup -> {
                viewModel.signUpWithEmailAndPassword()
            }

            R.id.btn_login -> {
                startActivity(Intent(this@SignupScreen, SignInScreen::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                })
            }
        }
    }
}