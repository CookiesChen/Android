package studio.xmatrix.coffee.data.service

import android.arch.lifecycle.LiveData
import com.google.gson.annotations.SerializedName
import retrofit2.http.*
import studio.xmatrix.coffee.data.common.network.ApiResponse
import studio.xmatrix.coffee.data.service.resource.CommentsResource
import studio.xmatrix.coffee.data.service.resource.CommonResource

interface CommentService {

    companion object {
        const val BASE_URL = "$SERVICE_BASE_URL/comment/"
    }

    @GET("{id}")
    fun getListByContentId(@Path("id") id: String): LiveData<ApiResponse<CommentsResource>>

    @POST("$SERVICE_BASE_URL/comment")
    fun add(@Body body: CommentRequestBody): LiveData<ApiResponse<CommonResource>>

    @DELETE("{id}")
    fun deleteById(@Path("id") id: String): LiveData<ApiResponse<CommonResource>>

    data class CommentRequestBody(
        val contentId: String,
        val fatherId: String,
        val content: String,
        @SerializedName("isReply")
        val reply: Boolean
    )
}
