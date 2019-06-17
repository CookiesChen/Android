package studio.xmatrix.coffee.data.service.resource

import com.google.gson.annotations.SerializedName
import studio.xmatrix.coffee.data.service.response.ContentResponse

data class NotificationsResource(
    @SerializedName("State")
    val state: String,
    @SerializedName("Notification")
    val resource: List<Notification>?
) {

    data class Notification(
        @SerializedName("User")
        val user: ContentResponse.UserBaseInfo,
        @SerializedName("Data")
        val data: NotificationDetail
    ) {

        data class NotificationDetail(
            @SerializedName("ID")
            val id: String,
            @SerializedName("CreateTime")
            val createTime: Long,
            @SerializedName("Content")
            val content: String,
            @SerializedName("SourceID")
            val sourceId: String,
            @SerializedName("TargetID")
            val targetId: String,
            @SerializedName("Read")
            val read: Boolean,
            @SerializedName("Type")
            val type: String
        )

        companion object {
            const val TypeSystem = "system"
            const val TypeLike = "like"
            const val TypeReply = "reply"
            const val TypeFollow = "follow"
        }
    }
}
