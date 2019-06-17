package studio.xmatrix.coffee.data.service

import android.arch.lifecycle.LiveData
import com.google.gson.annotations.SerializedName
import retrofit2.http.*
import studio.xmatrix.coffee.data.common.network.ApiResponse
import studio.xmatrix.coffee.data.service.resource.CommonResource
import studio.xmatrix.coffee.data.service.resource.LikeResource
import javax.inject.Singleton

@Singleton
interface LikeService {

    companion object {
        const val BASE_URL = "$SERVICE_BASE_URL/like/"
    }

    @GET("$SERVICE_BASE_URL/like")
    fun getList(): LiveData<ApiResponse<LikeResource>>

    @POST("{id}")
    fun addByObjectId(@Path("id") id: String, @Body body: LikeRequestBody): LiveData<ApiResponse<CommonResource>>

    @PATCH("{id}")
    fun removeByObjectId(@Path("id") id: String, @Body body: LikeRequestBody): LiveData<ApiResponse<CommonResource>>

    data class LikeRequestBody(
        @SerializedName("isContent")
        val content: Boolean,
        @SerializedName("isComment")
        val comment: Boolean,
        @SerializedName("isReply")
        val reply: Boolean
    )

    enum class LikeType {
        Content,
        Comment,
        Reply
    }
}
