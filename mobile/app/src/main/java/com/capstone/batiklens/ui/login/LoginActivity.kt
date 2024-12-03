package com.capstone.batiklens.ui.login

import android.content.Context
import android.content.Intent
import android.credentials.GetCredentialException
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.lifecycle.lifecycleScope
import com.capstone.batiklens.MainActivity
import com.capstone.batiklens.R
import com.capstone.batiklens.data.Result
import com.capstone.batiklens.databinding.ActivityLoginBinding
import com.capstone.batiklens.ui.AuthViewModel
import com.capstone.batiklens.ui.register.RegisterActivity
import com.capstone.batiklens.utils.ViewModelFactory
import com.capstone.batiklens.utils.dataStore
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    private val loginViewModel by viewModels<AuthViewModel> {
        ViewModelFactory.getInstance(this, dataStore)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonLogin.setOnClickListener{
            loginUsingEmailAndPassword()
        }

        binding.registerText.setOnClickListener{
            goToRegister()
        }

        binding.googleBtn.setOnClickListener {
            loginUsingGoogle()
        }
    }

    private fun loginUsingEmailAndPassword() {
        val email = binding.emailEditText.text.toString()
        val password = binding.passwordEditText.text.toString()

        loginViewModel.loginEmailAndPassword(email, password).observe(this){result ->
            when(result){
                is Result.Loading -> {}
                is Result.Success -> {
                    Log.d("Login", "success")
                    Toast.makeText(this, "Login Success", Toast.LENGTH_SHORT).show()
                    goToMainActivity()
                }
                is Result.Error -> {
                    Log.d("Login", "error")
                    Toast.makeText(this, "${result.error}", Toast.LENGTH_SHORT).show()
                }
            }

        }
    }

    private fun loginUsingGoogle() {
//        TODO("Not yet implemented")
        val credentialManager = CredentialManager.create(this)

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(getString(R.string.your_web_client_id))
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        lifecycleScope.launch {
            try {
                val result: GetCredentialResponse = credentialManager.getCredential(
                    request = request,
                    context = this@LoginActivity,
                )
                handleSignIn(result)
            }catch (e: Exception) {
                Log.d("errorCredential","${e.message}")
            }
        }
    }

    private fun handleSignIn(result: GetCredentialResponse) {
        when(val credential = result.credential){
            is CustomCredential -> {
                if(credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL){
                    val googleIdToken = GoogleIdTokenCredential.createFrom(credential.data)
                    loginViewModel.signInUsingGoogle(googleIdToken.idToken).observe(this){result ->
                        when(result){
                            is Result.Error -> {
                                Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
                            }
                            is Result.Loading -> {}
                            is Result.Success -> {
                                Toast.makeText(this, result.data, Toast.LENGTH_SHORT).show()
                                val user = loginViewModel.currentUser()
                                if(user != null){
                                    goToMainActivity()
                                }
                            }
                        }
                    }
                }else{
                    Log.e("error signin", "Unexpected type of credential")
                }
            }
            else -> {
                Log.e("error signin", "Unexpected type of credential")
            }
        }

    }

    private fun goToRegister() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }

    private fun goToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}