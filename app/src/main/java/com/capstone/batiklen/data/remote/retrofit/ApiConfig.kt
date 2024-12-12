package com.capstone.batiklen.data.remote.retrofit

import android.content.Context
import android.util.Log
import com.capstone.batiklen.data.AuthRepository
import com.capstone.batiklen.utils.UserPreferences
import com.capstone.batiklen.utils.dataStore
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class ApiConfig {
    companion object{
        fun getApiService(context: Context): ApiService {
            val userToken = AuthRepository.getInstance(UserPreferences.getInstance(context.dataStore))

            val authInterceptor = Interceptor{ chain ->
                val token = runBlocking {
                   fetchToken()
                }
                Log.d("tokenApiConfig", "$token")
                val req = chain.request()
                val requestHeader = req.newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
                chain.proceed(requestHeader)
            }
            val loggingInterceptor =
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)

            val client = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(authInterceptor)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl("https://batiklens-api-565827583178.asia-southeast2.run.app/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

            return retrofit.create(ApiService::class.java)
        }
        private suspend fun fetchToken(): String? {
            return suspendCoroutine { continuation ->
                val user = FirebaseAuth.getInstance().currentUser
                if (user != null) {
                    user.getIdToken(false).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            continuation.resume(task.result?.token)
                        } else {
                            continuation.resumeWithException(task.exception ?: Exception("Unknown error"))
                        }
                    }
                } else {
                    continuation.resume(null) // Handle no user case
                }
            }
        }
    }

}