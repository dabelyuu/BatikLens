package com.capstone.batiklen.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.capstone.batiklen.data.AuthRepository
import com.capstone.batiklen.data.DataRepository
import com.capstone.batiklen.data.remote.retrofit.ApiConfig
import com.capstone.batiklen.data.remote.retrofit.ApiService
import com.capstone.batiklen.utils.UserPreferences

object Injection {
    fun provideRepository(context: Context, dataStore: DataStore<Preferences>): AuthRepository{
        val preferences= UserPreferences.getInstance(dataStore)

        return AuthRepository.getInstance(preferences)
    }

    fun batikRepository(context: Context): DataRepository{
        val apiService = ApiConfig.getApiService(context)

        return DataRepository.getInstance(apiService)
    }
}