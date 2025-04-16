package com.suffixdigital.smartauthenticator.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.suffixdigital.smartauthenticator.R
import com.suffixdigital.smartauthenticator.databinding.ActivityHomeScreenBinding
import com.suffixdigital.smartauthenticator.domain.model.Response
import com.suffixdigital.smartauthenticator.ui.SignInScreen
import com.suffixdigital.smartauthenticator.viewmodels.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeScreen : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityHomeScreenBinding
    private lateinit var snackbar: Snackbar

    val viewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home_screen)

        initViews()
        setupViews()
    }

    private fun initViews() {
        val bundle = intent.extras
        if (bundle?.containsKey("SignInType") == true) {
            if (bundle.getString("SignInType") == "PhoneSignIn") {
                binding.btnSignOut.visibility = View.GONE
                binding.btnDeleteUser.visibility = View.GONE
            } else if (bundle.getString("SignInType") == "EmailPasswordSignIn") {
                binding.btnSignOut.visibility = View.VISIBLE
                binding.btnDeleteUser.visibility = View.VISIBLE
            } else {
                binding.btnSignOut.visibility = View.GONE
                binding.btnDeleteUser.visibility = View.GONE
            }
        }
        binding.btnSignOut.setOnClickListener(this)
        binding.btnDeleteUser.setOnClickListener(this)
    }

    private fun setupViews() {
        // Navigate if user is signed out
        lifecycleScope.launchWhenStarted {
            viewModel.authState.collect { isSignedOut ->
                if (isSignedOut) {
                    startActivity(Intent(this@HomeScreen, SignInScreen::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    })
                }
            }
        }

        // Handle delete user response
        lifecycleScope.launchWhenStarted {
            viewModel.deleteUserState.collect { response ->
                when (response) {
                    is Response.Idle -> {
                        binding.loadingProcess.progressCircular.visibility = View.GONE
                    }

                    is Response.Loading -> {
                        binding.loadingProcess.progressCircular.visibility = View.VISIBLE
                    }

                    is Response.Success -> {
                        binding.loadingProcess.progressCircular.visibility = View.GONE
                        Toast.makeText(
                            this@HomeScreen,
                            getString(R.string.user_deleted_message),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    is Response.Failure -> {
                        binding.loadingProcess.progressCircular.visibility = View.GONE
                        val message = response.e?.message ?: "Something went wrong"
                        if (message.contains(
                                getString(R.string.sensitive_keyword),
                                ignoreCase = true
                            )
                        ) {
                            snackbar = Snackbar.make(
                                binding.btnDeleteUser.rootView,
                                getString(R.string.reauthentication_required_message),
                                Snackbar.LENGTH_LONG
                            )
                                .setAction(getString(R.string.sign_out_action_label)) {
                                    viewModel.signOut()
                                }
                            snackbar.show()
                        } else {
                            Toast.makeText(this@HomeScreen, message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btn_sign_out -> {
                viewModel.signOut()
            }

            R.id.btn_delete_user -> {
                viewModel.deleteUser()
            }
        }
    }
}