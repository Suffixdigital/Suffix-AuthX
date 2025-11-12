package com.suffixdigital.smartauthenticator.ui

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.util.PatternsCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.suffixdigital.smartauthenticator.R
import com.suffixdigital.smartauthenticator.core.inputFilter
import com.suffixdigital.smartauthenticator.databinding.ActivitySignInScreenBinding
import com.suffixdigital.smartauthenticator.domain.model.Response
import com.suffixdigital.smartauthenticator.ui.MainActivity
import com.suffixdigital.smartauthenticator.viewmodels.SignInViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignInScreen : AppCompatActivity(),View.OnClickListener {
    private lateinit var binding: ActivitySignInScreenBinding

    private val viewModel: SignInViewModel by viewModels()

    /******************************************************************************
     * [textInputLayoutList] instance is use to store [TextInputLayout] data.
     *****************************************************************************/
    private lateinit var textInputLayoutList: MutableList<TextInputLayout>

    /******************************************************************************
     * [isEmailValid] is a boolean variable to validate does entered Email-ID is valid or not. Default value is 'False'
     *****************************************************************************/
    private var isEmailValid = false

    /******************************************************************************
     * [isPasswordValid] is a boolean variable to validate does entered Password is valid or not. Default value is 'False'
     *****************************************************************************/
    private var isPasswordValid = false

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
                    isEmailValid = false
                    displayErrorMessage(binding.tilEmailId, binding.etEmailId, resources.getString(R.string.err_empty_email))
                }
                /******************************************************************************
                 * If Email-ID is not valid then display error message to user. [isEmailValid] becomes a false
                 *****************************************************************************/
                !PatternsCompat.EMAIL_ADDRESS.matcher(editable.toString()).matches() -> {
                    isEmailValid = false
                    displayErrorMessage(binding.tilEmailId, binding.etEmailId, resources.getString(R.string.err_invalid_email))
                }
                /******************************************************************************
                 * If Email-ID is valid then [isEmailValid] becomes a True. The Login button becomes enable and change button's background
                 * color and text color.
                 *****************************************************************************/
                else -> {

                    isEmailValid = true
                    binding.tilEmailId.isErrorEnabled = false
                    if (isEmailValid && isPasswordValid) {
                        binding.btnLogIn.isEnabled = true
                        binding.btnLogIn.alpha = 1.0f
                        binding.btnLogIn.setTextColor(ContextCompat.getColor(this@SignInScreen, R.color.white))
                        binding.btnLogIn.setBackgroundColor(ContextCompat.getColor(this@SignInScreen, R.color.accent_green))
                    }
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
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        /******************************************************************************
         * [onTextChanged], No need to use this over-ride function here.
         *****************************************************************************/
        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        /******************************************************************************
         * [afterTextChanged], When user insert/delete character by character in [TextInputEditText] this function is validate does
         * Password is empty or not. If Password is valid then [isPasswordValid] becomes true but if Password is not valid then
         * display Error message to user. Once Email-ID and Password are valid then 'Login' button becomes enable and Change button color
         * and text color.
         *****************************************************************************/
        override fun afterTextChanged(editable: Editable?) {
            viewModel.onPasswordChange(editable.toString())
            editable?.let {
                binding.etPassword.setSelection(it.length)
            }
            when {
                /******************************************************************************
                 * If Password is empty then display error message to user. [isPasswordValid] becomes a false
                 *****************************************************************************/
                editable.toString().trim().isEmpty() -> {
                    isPasswordValid = false
                    displayErrorMessage(binding.tilPassword, binding.etPassword, resources.getString(R.string.err_empty_password))
                }
                /******************************************************************************
                 * If Password is valid then [isPasswordValid] becomes a True. The Login button becomes enable and change button's background
                 * color and text color.
                 *****************************************************************************/
                else -> {

                    isPasswordValid = true
                    binding.tilPassword.isErrorEnabled = false
                    if (isEmailValid && isPasswordValid) {
                        binding.btnLogIn.isEnabled = true
                        binding.btnLogIn.alpha = 1.0f
                        binding.btnLogIn.setTextColor(ContextCompat.getColor(this@SignInScreen, R.color.white))
                        binding.btnLogIn.setBackgroundColor(ContextCompat.getColor(this@SignInScreen, R.color.accent_green))
                    }
                }
            }
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
        setupViews()
    }


    private fun initViews() {
        textInputLayoutList = mutableListOf(binding.tilEmailId, binding.tilPassword)
        binding.etEmailId.addTextChangedListener(emailIDTextWatcher)
        binding.etPassword.addTextChangedListener(passwordTextWatcher)
        binding.etEmailId.filters = arrayOf(inputFilter)

        binding.btnLogIn.setOnClickListener(this)
        binding.btnForgotPassword.setOnClickListener(this)
        binding.btnRegister.setOnClickListener(this)
        binding.btnBack.setOnClickListener(this)
    }

    private fun setupViews(){
        // Observe state
        /*lifecycleScope.launchWhenStarted {
            viewModel.email.collect { binding.etEmailId.setText(it) }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.password.collect { binding.etPassword.setText(it) }
        }*/

        lifecycleScope.launchWhenStarted {
            viewModel.signInState.collect { response ->
                when (response) {
                    is Response.Loading -> binding.loadingProcess.progressCircular.visibility = View.VISIBLE
                    is Response.Success -> {
                        binding.loadingProcess.progressCircular.visibility = View.GONE
                        if (viewModel.isEmailVerified) {
                            startActivity(Intent(this@SignInScreen, HomeScreen::class.java).apply {
                                putExtra("SignInType", "EmailPasswordSignIn")
                                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            })
                        } else {
                            startActivity(Intent(this@SignInScreen, VerifyEmailScreen::class.java).apply {
                                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            })
                        }
                    }
                    is Response.Failure -> {
                        binding.loadingProcess.progressCircular.visibility = View.GONE
                        Toast.makeText(this@SignInScreen, response.e?.message ?: "Unknown error", Toast.LENGTH_SHORT).show()
                    }
                    is Response.Idle -> {
                        binding.loadingProcess.progressCircular.visibility = View.GONE
                    }
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

        binding.btnLogIn.alpha = 0.65f
        binding.btnLogIn.isEnabled = false
        binding.btnLogIn.setTextColor(ContextCompat.getColor(this@SignInScreen, R.color.light_black))
        binding.btnLogIn.setBackgroundColor(ContextCompat.getColor(this@SignInScreen, R.color.light_gray))
    }
    override fun onClick(view: View?) {
        when(view?.id){
            R.id.btn_log_in ->{
                viewModel.signInWithEmailAndPassword()
            }

            R.id.btn_forgot_password ->{
                startActivity(Intent(this@SignInScreen, ForgotPasswordScreen::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                })
            }

            R.id.btn_register ->{
                startActivity(Intent(this@SignInScreen, SignupScreen::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                })
            }

            R.id.btn_back ->{
                startActivity(Intent(this@SignInScreen, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                })
            }
        }
    }
}