package com.capstone.batiklen.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.capstone.batiklen.data.AuthRepository
import com.capstone.batiklen.data.DataRepository
import com.capstone.batiklen.di.Injection
import com.capstone.batiklen.ui.AuthViewModel
import com.capstone.batiklen.ui.BatikViewModel

class ViewModelFactory(private val authRepository: AuthRepository, private val dataRepository: DataRepository): ViewModelProvider.NewInstanceFactory(){
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
            return AuthViewModel(authRepository) as T
        }else if (modelClass.isAssignableFrom(BatikViewModel::class.java)) {
            return BatikViewModel(dataRepository) as T
        }
        throw IllegalArgumentException("Unknown View Model Class ${modelClass.name}")
    }

    companion object{
        @Volatile
        private var instance: ViewModelFactory? = null
        fun getInstance(context: Context, dataStore: DataStore<Preferences>): ViewModelFactory =
            instance ?: synchronized(this){
                instance ?: ViewModelFactory(Injection.provideRepository(context,dataStore),
                    Injection.batikRepository(context))
            }
    }
}