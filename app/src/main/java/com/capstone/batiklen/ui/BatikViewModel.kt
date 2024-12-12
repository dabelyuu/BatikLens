package com.capstone.batiklen.ui

import androidx.lifecycle.ViewModel
import com.capstone.batiklen.data.DataRepository
import okhttp3.MultipartBody

class BatikViewModel(private val dataRepository: DataRepository): ViewModel() {
    fun getAllBatik() = dataRepository.getAllData()

    fun predictImage(image: MultipartBody.Part) = dataRepository.predictImage(image)

    fun getHistory() = dataRepository.getHistory()
}