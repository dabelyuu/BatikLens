package com.capstone.batiklen.ui.result

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.capstone.batiklen.R
import com.capstone.batiklen.data.remote.response.Metadata
import com.capstone.batiklen.data.remote.response.PredictData
import com.capstone.batiklen.databinding.ActivityResultBinding
import com.capstone.batiklen.utils.UserPreferences
import com.capstone.batiklen.utils.dataStore
import com.capstone.batiklen.utils.translateLanguage
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding

    private lateinit var pref: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.result)

        pref = UserPreferences.getInstance(dataStore)


        val predictionData = intent.getSerializableExtra("PREDICTION_DATA") as? PredictData
        val metadata = intent.getParcelableExtra("METADATA") as? Metadata
        val imageUrls = intent.getStringExtra("IMAGE_URLS") // Retrieves ArrayList<String>

//        val data = intent.getStringExtra("CAMERAXPIC")
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch {
            val languageCode = pref.getLanguageSetting().first()

            val history = metadata?.sejarahBatik?.translateLanguage(baseContext, languageCode)
            val philosophy = metadata?.filosofiBatik?.translateLanguage(baseContext, languageCode)

            binding.tvSejarahContent.text = history
            binding.tvFilosofiContent.text = philosophy
        }

        Glide.with(this).load(imageUrls).into(binding.imgResult)
        binding.tvBatikName.text = predictionData?.result
        binding.tvBatikDescription.text = getString(R.string.confidence_score, predictionData?.confidenceScore)
        binding.tvAsalContent.text = metadata?.asalBatik


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