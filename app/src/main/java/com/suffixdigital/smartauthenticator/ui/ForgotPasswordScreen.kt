package com.suffixdigital.smartauthenticator.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.util.PatternsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.suffixdigital.smartauthenticator.R
import com.suffixdigital.smartauthenticator.core.inputFilter
import com.suffixdigital.smartauthenticator.databinding.ActivityForgotPasswordScreenBinding
import com.suffixdigital.smartauthenticator.domain.model.Response
import com.suffixdigital.smartauthenticator.viewmodels.ForgotPasswordViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgotPasswordScreen : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityForgotPasswordScreenBinding
    val viewModel: ForgotPasswordViewModel by viewModels()

    /******************************************************************************
     * [emailIDTextWatcher] is a instance of [TextInputEditText]. Here [TextInputEditText] has overide class [TextWatcher] and it has
     * below over-ride functions to real time validate inserted data in [TextInputEditText]. Here This [TextWatcher] class is validate
     * entered Email-ID is valid or not. If Email-ID is not valid then display Error message to user
     *****************************************************************************/
    private val emailIDTextWatcher = object : TextWatcher {
        /******************************************************************************
         * [beforeTextChanged], No need to use this over-ride function here.
         *****************************************************************************/
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        /******************************************************************************
         * [onTextChanged], No need to use this over-ride function here.
         *****************************************************************************/
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        /******************************************************************************
         * [afterTextChanged], When user insert/delete character by character in [TextInputEditText] this function is validate does
         * Email-ID is empty or not also Email-ID is with valid pattern or not. If Email is valid then [isEmailValid] becomes true but if
         * Email-ID is not valid then display Error message to user. Once Email-ID and Password are valid then 'Login' button becomes enable and Change button color
         * and text color.
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
                    displayErrorMessage(binding.tilEmailId, binding.etEmailId, resources.getString(R.string.err_empty_email))
                }
                /******************************************************************************
                 * If Email-ID is not valid then display error message to user. [isEmailValid] becomes a false
                 *****************************************************************************/
                !PatternsCompat.EMAIL_ADDRESS.matcher(editable.toString()).matches() -> {
                    displayErrorMessage(binding.tilEmailId, binding.etEmailId, resources.getString(R.string.err_invalid_email))
                }
                /******************************************************************************
                 * If Email-ID is valid then [isEmailValid] becomes a True. The Login button becomes enable and change button's background
                 * color and text color.
                 *****************************************************************************/
                else -> {

                    binding.tilEmailId.isErrorEnabled = false
                    binding.btnResetPassword.isEnabled = true
                    binding.btnResetPassword.alpha = 1.0f
                    binding.btnResetPassword.setTextColor(ContextCompat.getColor(this@ForgotPasswordScreen, R.color.white))
                    binding.btnResetPassword.setBackgroundColor(ContextCompat.getColor(this@ForgotPasswordScreen, R.color.accent_green))
                }
            }
        }
    }

    /******************************************************************************
     * This function is display error message to user for [TextInputEditText]. When user inserting/deleting
     * EmailID and/or password and it are wrong pattern or invalid or empty then display Error message to user.
     * Here 'Login' button make disable and its background color and text color also change.
     *
     * @param textInputLayout : [TextInputLayout] for Email-ID or Password.
     * @param textInputEditText [TextInputEditText] for Email-ID or Password.
     * @param errorMessage : Error message based on validation for Email-ID or Password.
     *****************************************************************************/
    private fun displayErrorMessage(textInputLayout: TextInputLayout, textInputEditText: TextInputEditText, errorMessage: String) {
        textInputLayout.errorIconDrawable = null
        textInputEditText.isCursorVisible = true
        textInputLayout.error = errorMessage

        binding.btnResetPassword.alpha = 0.65f
        binding.btnResetPassword.isEnabled = false
        binding.btnResetPassword.setTextColor(ContextCompat.getColor(this@ForgotPasswordScreen, R.color.light_black))
        binding.btnResetPassword.setBackgroundColor(ContextCompat.getColor(this@ForgotPasswordScreen, R.color.light_gray))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_forgot_password_screen)

        initViews()
        setupViews()
    }

    private fun initViews() {
        binding.etEmailId.addTextChangedListener(emailIDTextWatcher)
        binding.etEmailId.filters = arrayOf(inputFilter)

        binding.btnResetPassword.setOnClickListener(this)
        binding.btnLogin.setOnClickListener(this)
    }

    private fun setupViews() {
        // Observe state
        lifecycleScope.launchWhenStarted {
            viewModel.passwordResetEmailState.collect { response ->
                when (response) {
                    is Response.Idle -> {
                        binding.loadingProcess.progressCircular.visibility = View.GONE
                    }
                    is Response.Loading -> {
                        binding.loadingProcess.progressCircular.visibility = View.VISIBLE
                    }
                    is Response.Success -> {
                        binding.loadingProcess.progressCircular.visibility = View.GONE
                        Toast.makeText(this@ForgotPasswordScreen, getString(R.string.reset_password_message), Toast.LENGTH_SHORT).show()

                    }
                    is Response.Failure -> {
                        binding.loadingProcess.progressCircular.visibility = View.GONE
                        val errorMessage = response.e?.message ?: "Unknown error"
                        Toast.makeText(this@ForgotPasswordScreen, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onClick(view: View?) {
        when (view?.id){
            R.id.btn_reset_password -> {
                viewModel.sendPasswordResetEmail(binding.etEmailId.text.toString())
            }

            R.id.btn_login ->{
                startActivity(Intent(this@ForgotPasswordScreen, SignInScreen::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                })
            }
        }
    }

}