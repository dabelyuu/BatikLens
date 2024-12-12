package com.capstone.batiklen.data.remote.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ImageUrl(
	@field:SerializedName("url")
	val url: List<String?>? = null
):Parcelable