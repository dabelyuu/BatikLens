package com.capstone.batiklen.ui.history

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.capstone.batiklen.R
import com.capstone.batiklen.data.Result
import com.capstone.batiklen.databinding.ActivityHistoryBinding
import com.capstone.batiklen.ui.BatikViewModel
import com.capstone.batiklen.ui.HistoryAdapter
import com.capstone.batiklen.utils.ViewModelFactory
import com.capstone.batiklen.utils.dataStore

class HistoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHistoryBinding

    private val batikViewModel by viewModels<BatikViewModel> {
        ViewModelFactory.getInstance(this, dataStore)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "History"

        val adapterHistory = HistoryAdapter(this)

        batikViewModel.getHistory().observe(this){result ->
            when(result){
                is Result.Loading ->{
                    binding.progressBarLogin.visibility = View.VISIBLE
                }
                is Result.Error ->{
                    binding.progressBarLogin.visibility = View.GONE
                    Toast.makeText(this, result.error, Toast.LENGTH_SHORT).show()
                }
                is Result.Success ->{
                    binding.progressBarLogin.visibility = View.GONE
                    adapterHistory.submitList(result.data)
                }
            }
        }

        binding.rvHistory.apply {
            layoutManager = LinearLayoutManager(this@HistoryActivity)
            adapter = adapterHistory
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