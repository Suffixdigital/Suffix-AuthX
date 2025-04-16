package com.suffixdigital.smartauthenticator.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.suffixdigital.smartauthenticator.R
import com.suffixdigital.smartauthenticator.databinding.ActivityVerifyEmailScreenBinding
import com.suffixdigital.smartauthenticator.domain.model.Response
import com.suffixdigital.smartauthenticator.viewmodels.VerifyEmailViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VerifyEmailScreen : AppCompatActivity(),View.OnClickListener {
    private lateinit var binding: ActivityVerifyEmailScreenBinding
    val viewModel: VerifyEmailViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =  DataBindingUtil.setContentView(this,R.layout.activity_verify_email_screen)
        initViews()
        setupViews()
    }

    private fun initViews(){
        binding.btnAlreadyVerified.setOnClickListener(this)
        binding.btnBack.setOnClickListener(this)
    }

    private fun setupViews(){
        lifecycleScope.launchWhenStarted {
            viewModel.reloadUserState.collect { response ->
                when (response) {
                    is Response.Idle -> {
                        binding.loadingProcess.progressCircular.visibility = View.GONE
                    }
                    is Response.Loading -> {
                        binding.loadingProcess.progressCircular.visibility = View.VISIBLE
                    }
                    is Response.Success -> {
                        binding.loadingProcess.progressCircular.visibility = View.GONE
                        viewModel.isEmailVerifiedState.value.let { isVerified ->
                            if (isVerified) {
                                startActivity(Intent(this@VerifyEmailScreen, HomeScreen::class.java).apply {
                                    putExtra("SignInType", "EmailPasswordSignIn")
                                    flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                                })
                            } else {
                                Toast.makeText(this@VerifyEmailScreen, getString(R.string.email_not_verified_message), Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    is Response.Failure -> {
                        binding.loadingProcess.progressCircular.visibility = View.GONE
                        val errorMessage = response.e?.message ?: "Error"
                        Toast.makeText(this@VerifyEmailScreen, errorMessage, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onClick(view: View?) {
        when(view?.id){
            R.id.btn_already_verified ->{
                viewModel.reloadUser()
            }
            R.id.btn_back ->{
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this@VerifyEmailScreen, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                })
            }
        }
    }
}