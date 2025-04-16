package com.suffixdigital.smartauthenticator.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.CredentialManager
import androidx.databinding.DataBindingUtil
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.auth.api.signin.GoogleSignIn.*
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.OAuthProvider
import com.google.firebase.auth.auth
import com.suffixdigital.smartauthenticator.R
import com.suffixdigital.smartauthenticator.databinding.ActivityMainBinding
import com.suffixdigital.smartauthenticator.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity() : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityMainBinding

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var googleSignInClient: GoogleSignInClient


    private lateinit var credentialManager: CredentialManager

    private lateinit var callbackManager: CallbackManager

    private lateinit var loginManager: LoginManager

    val viewModel: MainViewModel by viewModels()

    private val googleSignInLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val data: Intent? = result.data
            val task = getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                showToast("GoogleSignIn", "Google sign in failed: ${e.message}")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        initViews()
        setupViews()
    }

    private fun initViews() {
        credentialManager = CredentialManager.create(this@MainActivity)
        firebaseAuth = FirebaseAuth.getInstance()

        binding.btnGoogle.setOnClickListener(this)
        binding.btnFacebook.setOnClickListener(this)
        binding.btnPhone.setOnClickListener(this)
        binding.btnEmailPassword.setOnClickListener(this)
        binding.btnTwitter.setOnClickListener(this)
        binding.btnGithub.setOnClickListener(this)
        binding.btnYahoo.setOnClickListener(this)
        binding.btnMicrosoft.setOnClickListener(this)

    }

    private fun setupViews() {
        //Sign in with google
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build()

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

        //Sign in with Facebook
        FacebookSdk.sdkInitialize(applicationContext)
        callbackManager = CallbackManager.Factory.create()
        loginManager = LoginManager.getInstance()
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        googleSignInLauncher.launch(signInIntent)
    }


    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val user: FirebaseUser? = firebaseAuth.currentUser
                binding.txtGoogle.text = getString(R.string.lbl_logout)
                showToast("GoogleSignIn", getString(R.string.lbl_welcome_user, user?.displayName))
            } else {
                showToast("GoogleSignIn", "Google sign in failed")
            }
        }
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(token.token)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                val user = firebaseAuth.currentUser
                binding.txtFacebook.text = getString(R.string.lbl_logout)
                showToast("FacebookLogin", getString(R.string.lbl_welcome_user, user?.displayName))
            } else {
                showToast("FacebookLogin", "Firebase auth failed: ${task.exception?.message}")
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    fun signInWithTwitter() {
        val provider =
            OAuthProvider.newBuilder("twitter.com").addCustomParameter("lang", "en") //optional
                .build()

        val pending = FirebaseAuth.getInstance().pendingAuthResult
        if (pending != null) {
            pending.addOnSuccessListener { result ->
                val user = result.user
                binding.txtTwitter.text = getString(R.string.lbl_logout)
                showToast("TwitterLogin",getString(R.string.lbl_welcome_user, user?.displayName))
            }.addOnFailureListener { e ->
                showToast("TwitterLogin", "Pending error: ${e.message}")
            }
        } else {
            FirebaseAuth.getInstance()
                .startActivityForSignInWithProvider(this@MainActivity, provider)
                .addOnSuccessListener { result ->
                    val user = result.user
                    binding.txtTwitter.text = getString(R.string.lbl_logout)
                    showToast("TwitterLogin", getString(R.string.lbl_welcome_user, user?.displayName))
                }.addOnFailureListener { e ->
                    showToast("TwitterLogin", "Sign in failed: ${e.message}")
                }
        }
    }

    fun signInWithGithub() {
        val provider = OAuthProvider.newBuilder("github.com")
        provider.scopes = listOf("user:email")

        val pending = firebaseAuth.pendingAuthResult
        if (pending != null) {
            pending.addOnSuccessListener { result ->
                val user = result.user
                binding.txtGithub.text = getString(R.string.lbl_logout)
                showToast("GithubLogin", getString(R.string.lbl_welcome_user, user?.displayName))
            }.addOnFailureListener { e ->
                showToast("GithubLogin", "Pending error: ${e.message}")
            }
        } else {
            firebaseAuth.startActivityForSignInWithProvider(this@MainActivity, provider.build())
                .addOnSuccessListener { result ->
                    val user = result.user
                    binding.txtGithub.text = getString(R.string.lbl_logout)
                    showToast("GithubLogin", getString(R.string.lbl_welcome_user, user?.displayName))
                }.addOnFailureListener { e ->
                    showToast("GithubLogin", "Sign in failed: ${e.message}")
                }
        }
    }

    private fun signInWithYahoo() {
        val provider = OAuthProvider.newBuilder("yahoo.com")
        val pending = FirebaseAuth.getInstance().pendingAuthResult
        if (pending != null) {
            pending.addOnSuccessListener { result ->
                val user = result.user
                binding.txtYahoo.text = getString(R.string.lbl_logout)
                showToast("YahooLogin", getString(R.string.lbl_welcome_user, user?.displayName))
            }.addOnFailureListener { e ->
                showToast("YahooLogin", "Pending error: ${e.message}")
            }
        } else {
            FirebaseAuth.getInstance()
                .startActivityForSignInWithProvider(this@MainActivity, provider.build())
                .addOnSuccessListener { result ->
                    val user = result.user
                    binding.txtYahoo.text = getString(R.string.lbl_logout)
                    showToast("YahooLogin", getString(R.string.lbl_welcome_user, user?.displayName))
                }.addOnFailureListener { e ->
                    showToast("YahooLogin","Sign in failed: ${e.message}")
                }
        }

    }

    private fun signInWithMicrosoft() {
        val provider = OAuthProvider.newBuilder("microsoft.com")
        val pending = firebaseAuth.pendingAuthResult
        if (pending != null) {
            pending.addOnSuccessListener { result ->
                val user = result.user
                binding.txtMicrosoft.text = getString(R.string.lbl_logout)
                showToast("MicrosoftLogin", getString(R.string.lbl_welcome_user, user?.displayName))
            }.addOnFailureListener { e ->
                showToast("MicrosoftLogin", "Pending error: ${e.message}")
            }
        } else {
            firebaseAuth.startActivityForSignInWithProvider(this@MainActivity, provider.build())
                .addOnSuccessListener { result ->
                    val user = result.user
                    binding.txtMicrosoft.text = getString(R.string.lbl_logout)
                    showToast("MicrosoftLogin", getString(R.string.lbl_welcome_user, user?.displayName))
                }.addOnFailureListener { e ->
                    showToast("MicrosoftLogin", "Sign in failed: ${e.message}")
                }
        }
    }

    private fun showToast(tag: String, message: String) {
        Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
        Log.d(tag, message)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.btn_phone -> {
                startActivity(Intent(this@MainActivity, OtpSendActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                })
            }

            R.id.btn_email_password -> {
                firebaseAuth.signOut()
                if (viewModel.isUserSignedOut) {
                    startActivity(Intent(this@MainActivity, SignInScreen::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    })
                } else {
                    if (viewModel.isEmailVerified) {
                        startActivity(Intent(this@MainActivity, HomeScreen::class.java).apply {
                            putExtra("SignInType", "EmailPasswordSignIn")
                            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        })
                    } else {
                        startActivity(
                            Intent(
                                this@MainActivity, VerifyEmailScreen::class.java
                            ).apply {
                                flags =
                                    Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            })
                    }
                }
            }

            R.id.btn_google -> {
                if (binding.txtGoogle.text.toString().equals(getString(R.string.lbl_sign_in_with_google), true)) {
                    signInWithGoogle()
                } else {
                    firebaseAuth.signOut()
                    googleSignInClient.signOut()
                        .addOnCompleteListener(this, OnCompleteListener { task: Task<Void?>? ->
                            showToast("GoogleSignIn", getString(R.string.lbl_logged_out_successfully))
                            binding.txtGoogle.text = getString(R.string.lbl_sign_in_with_google)
                        })
                }

            }

            R.id.btn_facebook -> {
                if (binding.txtFacebook.text.toString().equals(getString(R.string.lbl_sign_in_with_facebook), true)) {
                    loginManager.logInWithReadPermissions(this, listOf("email", "public_profile"))

                    loginManager.registerCallback(
                        callbackManager, object : FacebookCallback<LoginResult> {
                            override fun onSuccess(loginResult: LoginResult) {
                                handleFacebookAccessToken(loginResult.accessToken)
                            }

                            override fun onCancel() {
                                showToast("FacebookLogin", "Facebook login cancelled")
                            }

                            override fun onError(error: FacebookException) {
                                showToast("FacebookLogin", "Facebook login failed: ${error.message}")
                            }
                        })
                } else {
                    firebaseAuth.signOut()
                    loginManager.logOut()
                    showToast("FacebookLogin", getString(R.string.lbl_logged_out_successfully))
                    binding.txtFacebook.text = getString(R.string.lbl_sign_in_with_facebook)
                }

            }

            R.id.btn_twitter -> {
                if (binding.txtTwitter.text.toString().equals(getString(R.string.lbl_sign_in_with_twitter), true)) {
                    signInWithTwitter()
                } else {
                    firebaseAuth.signOut()
                    Firebase.auth.signOut()
                    showToast("TwitterLogin", getString(R.string.lbl_logged_out_successfully))
                    binding.txtTwitter.text = getString(R.string.lbl_sign_in_with_twitter)
                }

            }

            R.id.btn_github -> {
                if (binding.txtGithub.text.toString().equals(getString(R.string.lbl_sign_in_with_github), true)) {
                    signInWithGithub()
                } else {
                    firebaseAuth.signOut()
                    Firebase.auth.signOut()
                    showToast("Github", getString(R.string.lbl_logged_out_successfully))
                    binding.txtGithub.text = getString(R.string.lbl_sign_in_with_github)
                }

            }

            R.id.btn_yahoo -> {
                if (binding.txtYahoo.text.toString().equals(getString(R.string.lbl_sign_in_with_yahoo), true)) {
                    signInWithYahoo()
                } else {
                    firebaseAuth.signOut()
                    Firebase.auth.signOut()
                    showToast("YahooLogin", getString(R.string.lbl_logged_out_successfully))
                    binding.txtYahoo.text = getString(R.string.lbl_sign_in_with_yahoo)
                }

            }

            R.id.btn_microsoft -> {
                if (binding.txtMicrosoft.text.toString().equals(getString(R.string.lbl_sign_in_with_microsoft), true)) {
                    signInWithMicrosoft()
                } else {
                    firebaseAuth.signOut()
                    Firebase.auth.signOut()
                    showToast("MicrosoftLogin", getString(R.string.lbl_logged_out_successfully))
                    binding.txtMicrosoft.text = getString(R.string.lbl_sign_in_with_microsoft)
                }

            }


        }
    }
}