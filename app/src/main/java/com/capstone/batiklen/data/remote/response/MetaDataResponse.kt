package com.capstone.batiklen.data.remote.response

import com.google.gson.annotations.SerializedName

data class MetaDataResponse(

	@field:SerializedName("data")
	val data: List<DataItem?>? = null,

	@field:SerializedName("success")
	val success: Boolean? = null
)