package com.capstone.batiklen.data

//import com.google.android.gms.tasks.OnCompleteListener
//import com.google.firebase.Firebase
//import com.google.firebase.FirebaseApp
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import com.capstone.batiklen.utils.UserPreferences
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.Token
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AuthRepository private constructor(
    private val userPreferences: UserPreferences
){
    private val fAuth : FirebaseAuth = FirebaseAuth.getInstance()
    private val dbFirestore : FirebaseFirestore = FirebaseFirestore.getInstance()

    fun registerEmailAndPassword(name: String, email:String, password: String)= liveData(Dispatchers.IO){
        emit(Result.Loading)

        try {

            fAuth.createUserWithEmailAndPassword(email, password).await()

            val uid = fAuth.currentUser?.uid

            saveUserData(name, email, uid)
            emit(Result.Success("Register Success"))

        }catch (e: Exception){
            Log.d("register", "$e")
            emit(Result.Error("${e.message}"))
        }
    }

    fun signEmailAndPassword(email: String, password: String) = liveData(Dispatchers.IO){
        emit(Result.Loading)

        try {
            val signin = suspendCoroutine { continuation ->
                fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    if(it.isSuccessful){
                        continuation.resume(Result.Success("Login Success"))
                    }else{
                        continuation.resume(Result.Error(it.exception?.message))
                    }
                }
            }

            emit(signin)
        }catch (e : Exception){
            emit(Result.Error(e.message.toString()))
        }
    }

    fun loginUsingGoogle(idToken :String) = liveData(Dispatchers.IO){
        emit(Result.Loading)
        val credential : AuthCredential = GoogleAuthProvider.getCredential(idToken, null)
        try {
//            val googleAuth = suspendCoroutine {continuation ->
//                fAuth.signInWithCredential(credential).addOnCompleteListener(Executors.newSingleThreadExecutor()) {
//                    if (it.isSuccessful) {
//
//                        val uid = fAuth.currentUser?.uid
//                        val email = fAuth.currentUser?.email
//                        val name = fAuth.currentUser?.displayName
//
//                        saveUserData(name, email, uid)
//
//                        Log.d("authRepo", "signin success")
//                        continuation.resume( Result.Success("Sign In Success"))
//                    } else {
//                        Log.d("authRepo", "signin failed")
//                        continuation.resume(  Result.Error(it.exception?.message))
//                    }
//                }
//            }
            fAuth.signInWithCredential(credential).await()

            val uid = fAuth.currentUser?.uid
            val email = fAuth.currentUser?.email
            val name = fAuth.currentUser?.displayName

            saveUserData(name, email, uid)
            emit(Result.Success("SignIn Success"))

        }catch (e : Exception){
            emit(Result.Error(e.message.toString()))
        }
    }

    fun getCurrentUser(): FirebaseUser? {
        return fAuth.currentUser
    }

    suspend fun getUserToken(): String? {
        return try {
            // Get the current user
            val user = fAuth.currentUser

            if (user != null) {
                // Retrieve the ID token
                val tokenResult = user.getIdToken(true).await()
                val token = tokenResult?.token

                Log.d("tokenRepo","$token")

                if (token != null) {
                    // Prepare token data to save
                    val tokenData = mapOf(
                        "token" to token,
                        "email" to user.email,
                        "timestamp" to System.currentTimeMillis()
                    )

                    // Save token to Firestore
                    dbFirestore.collection("user_tokens")
                        .document(user.uid)
                        .set(tokenData)
                        .await()

                    Log.d("TokenRepo", "Token successfully saved for user: ${user.uid}")
                } else {
                    Log.e("TokenRepo", "Token is null.")
                }

                token // Return the token
            } else {
                Log.e("TokenRepo", "User is not logged in.")
                null // No user logged in
            }
        } catch (e: Exception) {
            Log.e("TokenRepo", "Error saving token: ${e.message}", e)
            null // Return null if an exception occurs
        }
    }

    fun signOut(){
        fAuth.signOut()
    }

    fun getUserData() = liveData(Dispatchers.IO){
        emit(Result.Loading)
        val userId = fAuth.currentUser?.uid
        if (userId == null) {
            emit(Result.Error("User is not authenticated."))
            return@liveData
        }

        try{
            val data = dbFirestore.collection("users").document(userId).get().await()

            if(data.exists()){
                val user = data.toObject(UserEntity::class.java)
                if(user != null){
                    emit(Result.Success(user))
                }else{
                    emit(Result.Error("Failed to parse user Data"))
                }
            }else {
                emit(Result.Error("User data not found."))
            }
        }catch (e: Exception){
            emit(Result.Error("Error fetching user data: ${e.message}"))
        }
    }

    private suspend fun saveUserData(name : String?, email: String?, uid : String?){
        val data = UserEntity(
            email!!,
            uid!!,
            name!!
        )
        try{
            val userDoc = dbFirestore.collection("users").document(fAuth.currentUser!!.uid).get().await()
            if (!userDoc.exists()) {
                dbFirestore.collection("users").document(uid).set(data).await()
            }
            Log.d("firestoreSave","save Success")
        }catch (e: Exception){

            Log.d("firestoreSave","${e.message}")
        }
    }

    fun getThemeSettings(): LiveData<Boolean>{
        return userPreferences.getThemeSetting().asLiveData()
    }


    companion object{
        @Volatile
        private var instance: AuthRepository? = null
        fun getInstance(
            userPreferences: UserPreferences
        ): AuthRepository =
            instance ?: synchronized(this){
                instance ?:AuthRepository(userPreferences)
            }.also { instance = it }
    }
}