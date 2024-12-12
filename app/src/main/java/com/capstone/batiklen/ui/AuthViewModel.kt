package com.capstone.batiklen.ui

import androidx.lifecycle.ViewModel
import com.capstone.batiklen.data.AuthRepository

class AuthViewModel(private val authRepository: AuthRepository): ViewModel() {
    fun loginEmailAndPassword(email : String, password : String) = authRepository.signEmailAndPassword(email, password)

    fun registerEmailAndPassword(name:String, email: String, password: String) = authRepository.registerEmailAndPassword(name, email, password)

    fun signInUsingGoogle(idToken: String) = authRepository.loginUsingGoogle(idToken)

    fun signOut() = authRepository.signOut()

    fun currentUser() = authRepository.getCurrentUser()

    fun getUserData() = authRepository.getUserData()

    fun getThemeSettings() = authRepository.getThemeSettings()

    suspend fun getUserToken() = authRepository.getUserToken()
}