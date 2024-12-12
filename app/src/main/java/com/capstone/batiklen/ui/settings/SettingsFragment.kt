package com.capstone.batiklen.ui.settings

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.capstone.batiklen.R
import com.capstone.batiklen.databinding.FragmentNotificationsBinding
import com.capstone.batiklen.utils.UserPreferences
import com.capstone.batiklen.utils.dataStore
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.Locale

class SettingsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var pref: UserPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val settingsViewModel =
            ViewModelProvider(this).get(SettingsViewModel::class.java)

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pref =  UserPreferences.getInstance(context?.dataStore!!)

        val currentLanguageCode = pref.getLanguageSetting().toString()
        val languages = listOf(
            "English" to "en",
            "Indonesia" to "in"
        )
        Log.d("getSettings", currentLanguageCode)
        val languageNames = languages.map { it.first }.toTypedArray()
        val selectedIndex = languages.indexOfFirst { it.second == currentLanguageCode }

        Log.d("getSettings", selectedIndex.toString())

        lifecycleScope.launch {
            context?.dataStore?.data?.map {preferences ->
                preferences[booleanPreferencesKey("theme_setting")] ?: false
            }?.collect {isDarkMode ->
                if(isDarkMode) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    binding.darkModeSwitch.isChecked = true
                }else {
                    AppCompatDelegate.setDefaultNightMode( AppCompatDelegate.MODE_NIGHT_NO)
                    binding.darkModeSwitch.isChecked = false
                }
            }
        }

        binding.darkModeSwitch.setOnCheckedChangeListener{_: CompoundButton?,isChecked: Boolean ->
            lifecycleScope.launch {
                Log.d("switch", "$isChecked")
                pref.saveThemeSettings(isChecked)
            }
        }

        binding.setting1.setOnClickListener{
            showSelectedLanguage(languageNames, selectedIndex)
        }
    }

    private fun showSelectedLanguage(
        languageNames: Array<String>,
        selectedIndex: Int) {
        MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle(getString(R.string.select_language))
            setSingleChoiceItems(languageNames, selectedIndex){dialog, which ->
                val selectedLanguage = languageNames[which]
                Log.d("selecLang", selectedLanguage)
                val selectedLanguageCode = when(selectedLanguage){
                    "English" -> "en"
                    "Indonesia" -> "in"
                    else -> "en"
                }

                lifecycleScope.launch {
                    pref.saveLanguageSelected(selectedLanguageCode)
                    Toast.makeText(requireContext(),
                        getString(R.string.language_updated_to, selectedLanguage), Toast.LENGTH_SHORT).show()

                    updateLocale(selectedLanguageCode)

                    dialog.dismiss()
                    activity?.recreate()
                }
            }
            setNegativeButton("Cancel"){dialog, _ ->
                dialog.dismiss()
            }
            show()
        }

    }

    private fun updateLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}