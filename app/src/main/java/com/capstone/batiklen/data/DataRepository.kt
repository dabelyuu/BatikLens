package com.capstone.batiklen.data

import androidx.lifecycle.liveData
import com.capstone.batiklen.data.remote.response.BatikItem
import com.capstone.batiklen.data.remote.response.DataItem
import com.capstone.batiklen.data.remote.retrofit.ApiService
import kotlinx.coroutines.Dispatchers
import okhttp3.MultipartBody

class DataRepository(private val apiService: ApiService) {

    fun getAllData() = liveData<Result<List<BatikItem>>>(Dispatchers.IO){
        emit(Result.Loading)
        try {
            val item = apiService.allbatiksmetadata()
            if(item.isSuccessful){
                val batiks = item.body()?.data
                val list =ArrayList<BatikItem>()

                batiks?.forEach{
                    if(it != null){
                        list.add(it)
                    }
                }
                 emit(Result.Success(list))
            }else{
                emit(Result.Error(item.message().toString()))
            }
        }catch (e: Exception){
            emit(Result.Error(e.message.toString()))
        }
    }

    fun predictImage(image: MultipartBody.Part) = liveData(Dispatchers.IO) {
        emit(Result.Loading)

        try{
            val response = apiService.predict(image)

            if(response.isSuccessful){
                val predictData = response.body()?.predictData
                val metadata  =response.body()?.metadata
                val imageUrl = response.body()?.imageUrl?.url?.get(0)


                emit(Result.Success(Triple(predictData, metadata, imageUrl)))
            }else {
                emit(Result.Error(response.body()?.message ?: "Unknown error occurred"))
            }
        }catch (e: Exception){
            emit(Result.Error(e.localizedMessage ?: "Error fetching prediction data"))
        }
    }

    fun getHistory() = liveData(Dispatchers.IO) {
        emit(Result.Loading)
        try {
            val response = apiService.getHistory()
            if (response.isSuccessful){
                val item = response.body()?.data
                val list = ArrayList<DataItem>()

                item?.forEach{
                    if(it != null){
                        list.add(it)
                    }
                }

                emit(Result.Success(list))
            }else{
                emit(Result.Error("Fetch Data Failed"))
            }
        }catch (e: Exception){
            emit(Result.Error(e.message.toString()))
        }
    }

    companion object{
        @Volatile
        private var instance: DataRepository? = null
        fun getInstance(
            apiService: ApiService
        ): DataRepository =
            instance ?: synchronized(this){
                instance ?:DataRepository(apiService)
            }.also { instance = it }
    }
}