package com.capstone.batiklen.ui.detail

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.capstone.batiklen.R
import com.capstone.batiklen.data.remote.response.BatikItem
import com.capstone.batiklen.databinding.ActivityDetailBinding
import com.capstone.batiklen.utils.UserPreferences
import com.capstone.batiklen.utils.dataStore
import com.capstone.batiklen.utils.translateLanguage
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    private lateinit var pref: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pref = UserPreferences.getInstance(dataStore)



        val data = intent.getParcelableExtra("BATIK_ITEM") as? BatikItem


        lifecycleScope.launch {
            val languageCode = pref.getLanguageSetting().first()
            val history = data?.sejarahBatik?.translateLanguage(baseContext, languageCode)
            val philosophy = data?.filosofiBatik?.translateLanguage(baseContext, languageCode)

            binding.tvSejarahContent.text = history
            binding.tvFilosofiContent.text = philosophy
        }

        // Ambil data dari intent
//        val batikName = intent.getStringExtra("BATIK_NAME")
//        val batikDescription = intent.getStringExtra("BATIK_DESCRIPTION")
//        val batikImageRes = intent.getIntExtra("BATIK_IMAGE_RES", R.drawable.default_image) // Gambar default jika tidak ada data

        // Set data ke tampilan
        binding.tvBatikName.text = data?.namaBatik
        binding.tvAsalContent.text = data?.asalBatik

        Glide.with(this).load(data?.imageUrl?.url?.get(0)).into(binding.imgBatik)
    }
}