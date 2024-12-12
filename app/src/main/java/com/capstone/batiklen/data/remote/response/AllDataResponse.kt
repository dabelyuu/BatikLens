package com.capstone.batiklen.data.remote.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import java.io.Serializable

data class AllDataResponse(

	@field:SerializedName("data")
	val data: List<BatikItem?>? = null,

	@field:SerializedName("success")
	val success: Boolean? = null
)


@Parcelize
data class BatikItem(

	@field:SerializedName("namaBatik")
	val namaBatik: String? = null,

	@field:SerializedName("asalBatik")
	val asalBatik: String? = null,

	@field:SerializedName("imageUrl")
	val imageUrl: @RawValue ImageUrl? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("sejarahBatik")
	val sejarahBatik: String? = null,

	@field:SerializedName("filosofiBatik")
	val filosofiBatik: String? = null
): Parcelable

