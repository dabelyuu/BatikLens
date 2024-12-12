package com.capstone.batiklen.data.remote.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class DataItem(

	@field:SerializedName("metadata")
	val metadata: Metadata? = null,

	@field:SerializedName("imageUrl")
	val imageUrl: ImageUrl? = null,

	@field:SerializedName("history")
	val history: History? = null
): Parcelable