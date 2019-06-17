package studio.xmatrix.coffee.data.service.resource

import com.google.gson.annotations.SerializedName

data class LikeResource(
    @SerializedName("State")
    val state: String,
    @SerializedName("Data")
    val resource: List<String>?
)
