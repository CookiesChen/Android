package studio.xmatrix.coffee.data.service

import android.arch.lifecycle.LiveData
import com.google.gson.annotations.SerializedName
import retrofit2.http.*
import studio.xmatrix.coffee.data.common.network.ApiResponse
import studio.xmatrix.coffee.data.service.resource.CommonResource
import studio.xmatrix.coffee.data.service.resource.NotificationsResource

interface NotificationService {

    companion object {
        const val BASE_URL = "$SERVICE_BASE_URL/notification/"
    }

    @PATCH("read/{id}")
    fun readOrUnreadById(@Path("id") id: String, @Body body: ReadRequestBody): LiveData<ApiResponse<CommonResource>>

    @DELETE("{id}")
    fun deleteById(@Path("id") id: String): LiveData<ApiResponse<CommonResource>>

    @GET("unread")
    fun getUnreadCount(): LiveData<ApiResponse<CommonResource>>

    @GET("all")
    fun getList(): LiveData<ApiResponse<NotificationsResource>>

    data class ReadRequestBody(@SerializedName("isRead") val read: Boolean)
}
