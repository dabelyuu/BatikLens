package com.capstone.batiklen.data.remote.retrofit

import com.capstone.batiklen.data.remote.response.AllDataResponse
import com.capstone.batiklen.data.remote.response.MetaDataResponse
import com.capstone.batiklen.data.remote.response.PredictResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {

    @Multipart
    @POST("predict")
    suspend fun predict(
        @Part image: MultipartBody.Part
    ): Response<PredictResponse>

    @GET("allbatiksmetadata")
    suspend fun allbatiksmetadata(): Response<AllDataResponse>

    @GET("history")
    suspend fun getHistory(): Response<MetaDataResponse>

}