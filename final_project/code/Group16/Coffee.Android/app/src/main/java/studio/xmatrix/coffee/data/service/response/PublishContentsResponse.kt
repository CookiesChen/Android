package studio.xmatrix.coffee.data.service.response

import com.google.gson.annotations.SerializedName
import studio.xmatrix.coffee.data.model.Content
import studio.xmatrix.coffee.data.service.resource.ContentsResource

data class PublishContentsResponse(
    @SerializedName("State")
    val state: String,
    @SerializedName("Data")
    val publishData: List<PublishData>?
) {
    data class PublishData(
        @SerializedName("Data")
        val data: ContentResponse.ContentInfo,
        @SerializedName("User")
        val user: ContentResponse.UserBaseInfo
    )

    fun toContentsResource(): ContentsResource {
        if (publishData == null) {
            return ContentsResource(state = state, resource = null)
        }
        val arr = ArrayList<Content>()
        publishData.forEach {
            arr.add(
                Content(
                    userName = it.user.name,
                    userAvatar = it.user.avatar,
                    userGender = it.user.gender,
                    id = it.data.id,
                    name = it.data.name,
                    detail = it.data.detail,
                    ownId = it.data.ownId,
                    publishDate = it.data.publishDate,
                    editDate = it.data.editDate,
                    likeNum = it.data.likeNum,
                    commentNum = it.data.commentNum,
                    publicContent = it.data.public,
                    nativeContent = it.data.native,
                    type = it.data.type,
                    subType = it.data.subType,
                    remarks = it.data.remarks,
                    tags = it.data.tags
                ).apply {
                    it.data.images?.forEach { image -> images.add(image.toImage()) }
                    it.data.files?.forEach { file -> files.add(file) }
                    movie.target = if (it.data.movie?.file?.file.isNullOrEmpty()) null else it.data.movie?.toMovie()
                    album.target = if (it.data.album?.title.isNullOrEmpty()) null else it.data.album?.toAlbum()
                    app.target = if (it.data.app?.file?.file.isNullOrEmpty()) null else it.data.app?.toApp()
                }
            )
        }
        return ContentsResource(state = state, resource = arr)
    }
}
