package com.capstone.batiklen.data.remote.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class Metadata(

	@field:SerializedName("namaBatik")
	val namaBatik: String? = null,

	@field:SerializedName("asalBatik")
	val asalBatik: String? = null,

	@field:SerializedName("sejarahBatik")
	val sejarahBatik: String? = null,

	@field:SerializedName("filosofiBatik")
	val filosofiBatik: String? = null
): Parcelable