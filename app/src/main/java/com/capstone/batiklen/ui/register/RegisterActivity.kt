package com.capstone.batiklen.ui.register

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.capstone.batiklen.MainActivity
import com.capstone.batiklen.data.Result
import com.capstone.batiklen.databinding.ActivityRegisterBinding
import com.capstone.batiklen.ui.AuthViewModel
import com.capstone.batiklen.utils.ViewModelFactory
import com.capstone.batiklen.utils.dataStore

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    private val registerViewModel by viewModels<AuthViewModel> {
        ViewModelFactory.getInstance(this, dataStore)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.hide()

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            buttonRegister.setOnClickListener{
                val name = nameEditText.text.toString()
                val email = emailEditText.text.toString()
                val password = passwordEditText.text.toString()
                registerAccount(name, email, password)
            }
        }
    }

    private fun registerAccount(name: String, email: String, password: String) {
        registerViewModel.registerEmailAndPassword(name, email, password).observe(this@RegisterActivity){result ->
            when(result){
                is Result.Loading -> {
                    binding.progressBarLogin.visibility = View.VISIBLE
                    Log.d("RegisterActivity", "Loading")
                }
                is Result.Success -> {
                    binding.progressBarLogin.visibility = View.GONE
                    Log.d("RegisterActivity", result.data)
                    Toast.makeText(this, "Register Success", Toast.LENGTH_SHORT).show()
                    val user = registerViewModel.currentUser()
                    if(user != null){
                        goToMainActivity()
                    }
                }
                is Result.Error -> {
                    binding.progressBarLogin.visibility = View.GONE
                    Log.d("RegisterActivity", "${result.error} Error")

                    Toast.makeText(this, result.error.toString(), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun goToMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }}