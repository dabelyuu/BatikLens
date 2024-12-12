package com.capstone.batiklen.data.remote.response

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class PredictResponse(

	@field:SerializedName("metadata")
	val metadata: Metadata? = null,

	@field:SerializedName("imageUrl")
	val imageUrl: ImageUrl? = null,

	@field:SerializedName("predictData")
	val predictData: PredictData? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("status")
	val status: String? = null
)

data class PredictData(

	@field:SerializedName("result")
	val result: String? = null,

	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@field:SerializedName("confidenceScore")
	val confidenceScore: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("idBatik")
	val idBatik: String? = null
): Serializable


