package studio.xmatrix.coffee.data.service.resource

import com.google.gson.annotations.SerializedName
import studio.xmatrix.coffee.data.service.response.ContentResponse

data class CommentsResource(
    @SerializedName("State")
    val state: String,
    @SerializedName("Data")
    val resource: List<CommentForContent>?
) {
    data class CommentForContent(
        @SerializedName("Comment")
        val comment: Comment,
        @SerializedName("User")
        val user: ContentResponse.UserBaseInfo,
        @SerializedName("Replies")
        val replies: List<ReplyForComment>?
    )

    data class ReplyForComment(
        @SerializedName("Reply")
        val reply: Comment,
        @SerializedName("User")
        val user: ContentResponse.UserBaseInfo,
        @SerializedName("Father")
        val father: ContentResponse.UserBaseInfo
    )

    data class Comment(
        @SerializedName("ID")
        val id: String,
        @SerializedName("ContentID")
        val contentId: String,
        @SerializedName("FatherID")
        val fatherId: String,
        @SerializedName("UserID")
        val userId: String,
        @SerializedName("Date")
        val date: Long,
        @SerializedName("Content")
        val content: String,
        @SerializedName("LikeNum")
        val likeNum: Long
    )
}
