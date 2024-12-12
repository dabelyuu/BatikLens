package com.capstone.batiklen.ui.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.capstone.batiklen.R
import com.capstone.batiklen.data.remote.response.DataItem
import com.capstone.batiklen.data.remote.response.History
import com.capstone.batiklen.data.remote.response.Metadata
import com.capstone.batiklen.databinding.ActivityDetailResultBinding
import com.capstone.batiklen.utils.UserPreferences
import com.capstone.batiklen.utils.dataStore
import com.capstone.batiklen.utils.translateLanguage
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class DetailHistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailResultBinding

    private lateinit var pref: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pref = UserPreferences.getInstance(dataStore)

        val data = intent.getParcelableExtra<DataItem>("HISTORY_ITEM")
//        val predictionData = intent.getSerializableExtra("HISTORY_ITEM") as? History
//        val metadata = intent.getSerializableExtra("METADATA_ITEM") as? Metadata
//        val imageUrls = intent.getStringExtra("BATIK_IMAGE")

        lifecycleScope.launch {
            val languageCode = pref.getLanguageSetting().first()
            val history = data?.metadata?.sejarahBatik?.translateLanguage(baseContext, languageCode)
            val philosophy = data?.metadata?.filosofiBatik?.translateLanguage(baseContext, languageCode)

            binding.tvSejarahContent.text = history
            binding.tvFilosofiContent.text = philosophy
        }

        binding.tvBatikName.text = data?.history?.result
        binding.tvBatikDescription.text = getString(R.string.confidence_score, data?.history?.confidenceScore)
        binding.tvAsalContent.text = data?.metadata?.asalBatik
        Glide.with(this).load(data?.imageUrl?.url?.get(0)).into(binding.imgBatik)
    }
}