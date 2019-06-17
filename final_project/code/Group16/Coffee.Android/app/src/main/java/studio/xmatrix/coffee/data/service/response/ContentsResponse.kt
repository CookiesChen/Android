package studio.xmatrix.coffee.data.service.response

import com.google.gson.annotations.SerializedName
import studio.xmatrix.coffee.data.model.Content
import studio.xmatrix.coffee.data.model.User
import studio.xmatrix.coffee.data.service.resource.ContentsResource

data class ContentsResponse(
    @SerializedName("State")
    val state: String,
    @SerializedName("Data")
    val data: List<ContentResponse.ContentInfo>?
) {

    fun toContentsResource(): ContentsResource {
        if (data == null || data.isEmpty()) {
            return ContentsResource(state = state, resource = null)
        }
        val arr = ArrayList<Content>()
        data.forEach {
            arr.add(
                Content(
                    userName = "",
                    userAvatar = "",
                    userGender = User.GenderUnknown,
                    id = it.id,
                    name = it.name,
                    detail = it.detail,
                    ownId = it.ownId,
                    publishDate = it.publishDate,
                    editDate = it.editDate,
                    likeNum = it.likeNum,
                    commentNum = it.commentNum,
                    publicContent = it.public,
                    nativeContent = it.native,
                    type = it.type,
                    subType = it.subType,
                    remarks = it.remarks,
                    tags = it.tags
                ).apply {
                    it.images?.forEach { image -> images.add(image.toImage()) }
                    it.files?.forEach { file -> files.add(file) }
                    movie.target = if (it.movie?.file?.file.isNullOrEmpty()) null else it.movie?.toMovie()
                    album.target = if (it.album?.title.isNullOrEmpty()) null else it.album?.toAlbum()
                    app.target = if (it.app?.file?.file.isNullOrEmpty()) null else it.app?.toApp()
                }
            )
        }
        return ContentsResource(state = state, resource = arr)
    }
}
