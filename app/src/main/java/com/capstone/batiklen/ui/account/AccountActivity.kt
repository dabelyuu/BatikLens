package com.capstone.batiklen.ui.account

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.capstone.batiklen.R
import com.capstone.batiklen.data.Result
import com.capstone.batiklen.databinding.ActivityAccountBinding
import com.capstone.batiklen.ui.AuthViewModel
import com.capstone.batiklen.ui.history.HistoryActivity
import com.capstone.batiklen.ui.welcome.WelcomeActivity
import com.capstone.batiklen.utils.ViewModelFactory
import com.capstone.batiklen.utils.dataStore

class AccountActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAccountBinding

    private val authViewModel by viewModels<AuthViewModel> {
        ViewModelFactory.getInstance(this, dataStore)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.account)

        binding = ActivityAccountBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.setting1.setOnClickListener{
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }

        binding.btnLogout.setOnClickListener{
            userLogout()
        }
        Glide.with(this)
            .load(authViewModel.currentUser()?.photoUrl)
            .placeholder(R.drawable.placeholder_person_24)
            .into(binding.userImage)
        authViewModel.currentUser()?.photoUrl

//        binding.tvEmail.text = authViewModel.currentUser()?.email

        authViewModel.getUserData().observe(this){result ->
            when(result){
                is Result.Loading -> {
                    binding.progressBarLogin.visibility = View.VISIBLE
                }
                is Result.Success -> {
                    binding.progressBarLogin.visibility = View.GONE
                    binding.tvEmail.text = result.data.username
                }
                is Result.Error -> {
                    binding.progressBarLogin.visibility = View.GONE
                    Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
                }
            }
        }

    }


    private fun goToWelcomeActivity() {
        val intent = Intent(this, WelcomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun userLogout() {
        authViewModel.signOut()
        val user = authViewModel.currentUser()
        if (user == null){
            goToWelcomeActivity()
            Toast.makeText(this, getString(R.string.you_have_been_logged_out), Toast.LENGTH_LONG).show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}