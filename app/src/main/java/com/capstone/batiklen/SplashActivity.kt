package com.capstone.batiklen

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.lifecycleScope
import com.capstone.batiklen.ui.AuthViewModel
import com.capstone.batiklen.ui.welcome.WelcomeActivity
import com.capstone.batiklen.utils.ViewModelFactory
import com.capstone.batiklen.utils.dataStore
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.Locale


@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private val authViewModel by viewModels<AuthViewModel> {
        ViewModelFactory.getInstance(this, dataStore)
    }


    override fun onCreate(savedInstanceState: Bundle?) {

        lifecycleScope.launch {
            dataStore.data.map {preferences ->
                preferences[booleanPreferencesKey("theme_setting")] ?: false
            }.collect {isDarkMode ->
                Log.d("isdark", "$isDarkMode")
                AppCompatDelegate.setDefaultNightMode(
                    if(isDarkMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
                )
            }
        }

        super.onCreate(savedInstanceState)
        installSplashScreen()
        lifecycleScope.launch {
            dataStore.data.map { preferences->
                preferences[stringPreferencesKey("language_setting")] ?: "en"
            }.collect{isLang ->
                Log.d("islang", isLang)
                updateLocale(isLang)
            }
        }


//        binding = ActivitySplashBinding.inflate(layoutInflater)

        setContentView(R.layout.activity_splash)
        supportActionBar?.hide()
//        enableEdgeToEdge()
//        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            view.setPadding(0, systemBars.top, 0, systemBars.bottom)
//            insets
//        }
        lifecycleScope.launch {
            val userToken = authViewModel.getUserToken()
            Log.d("tokenSplash", "$userToken")
        }


        val splash = findViewById<ImageView>(R.id.icon)

        val avd = AnimationUtils.loadAnimation(this,R.anim.scal_anim)
        splash.startAnimation(avd)

        if(savedInstanceState == null){
            lifecycleScope.launch {
                delay(2000)
                getIsUserAuthenticate()
            }
        }else{
            lifecycleScope.launch {
                delay(2000)
                getIsUserAuthenticate()
            }
        }
    }

    private fun updateLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }
    private fun getIsUserAuthenticate() {
        val user = authViewModel.currentUser()
        Log.d("splashCheck",user?.displayName.toString())

        if(user != null){
            startMainActivity()
        }else{
            startWelcomeActivity()
        }
    }

    private fun startMainActivity() {
        Log.d("splashMove","move")
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun startWelcomeActivity() {
        Log.d("splashMove","move")
        startActivity(Intent(this, WelcomeActivity::class.java))
        finish()
    }
}