package studio.xmatrix.coffee.data.service.response

import com.google.gson.annotations.SerializedName
import studio.xmatrix.coffee.data.model.*
import studio.xmatrix.coffee.data.service.resource.ContentResource

data class ContentResponse(
    @SerializedName("State")
    val state: String,
    @SerializedName("Data")
    val data: ContentInfo?,
    @SerializedName("User")
    val user: UserBaseInfo?
) {
    data class ContentInfo(
        @SerializedName("ID")
        val id: String,
        @SerializedName("Name")
        val name: String,
        @SerializedName("Detail")
        val detail: String,
        @SerializedName("OwnID")
        val ownId: String,
        @SerializedName("PublishDate")
        val publishDate: Long,
        @SerializedName("EditDate")
        val editDate: Long,
        @SerializedName("LikeNum")
        val likeNum: Long,
        @SerializedName("CommentNum")
        val commentNum: Long,
        @SerializedName("Public")
        val public: Boolean,
        @SerializedName("Native")
        val native: Boolean,
        @SerializedName("Type")
        val type: String,
        @SerializedName("SubType")
        val subType: String,
        @SerializedName("Remarks")
        val remarks: String,
        @SerializedName("Tag")
        val tags: List<String>,
        @SerializedName("Image")
        val images: List<ImageInfo>?,
        @SerializedName("Files")
        val files: List<File>?,
        @SerializedName("Movie")
        val movie: MovieInfo?,
        @SerializedName("Album")
        val album: AlbumInfo?,
        @SerializedName("App")
        val app: AppInfo?
    ) {

        data class AlbumInfo(
            @SerializedName("Images")
            val images: List<ImageInfo>,
            @SerializedName("Title")
            val title: String,
            @SerializedName("Time")
            val time: Long,
            @SerializedName("Location")
            val location: String
        ) {
            fun toAlbum(): Album {
                return Album(title = title, time = time, location = location).also {
                    images.forEach { image -> it.images.add(image.toImage()) }
                }
            }
        }

        data class AppInfo(
            @SerializedName("File")
            val file: File,
            @SerializedName("Web")
            val web: String,
            @SerializedName("URL")
            val url: String,
            @SerializedName("Image")
            val images: List<ImageInfo>,
            val des: String,
            @SerializedName("Version")
            val version: String
        ) {
            fun toApp(): App {
                return App(web = web, url = url, des = des, version = version).also {
                    it.file.target = file
                    images.forEach { image -> it.images.add(image.toImage()) }
                }
            }
        }

        data class ImageInfo(
            @SerializedName("Native")
            val native: Boolean,
            @SerializedName("File")
            val file: File,
            @SerializedName("URL")
            val url: String,
            @SerializedName("Thumb")
            val thumb: String
        ) {
            fun toImage(): Image {
                return Image(native = native, url = url, thumb = thumb).also { it ->
                    it.file.target = file
                }
            }
        }

        data class MovieInfo(
            @SerializedName("File")
            val file: File,
            @SerializedName("URL")
            val url: String,
            @SerializedName("Image")
            val images: List<ImageInfo>,
            @SerializedName("Type")
            val type: String,
            @SerializedName("Detail")
            val detail: String,
            @SerializedName("Watched")
            val watched: Boolean
        ) {
            fun toMovie(): Movie {
                return Movie(url = url, type = type, detail = detail, watched = watched).also { it ->
                    it.file.target = file
                    images.forEach { image -> it.images.add(image.toImage()) }
                }
            }
        }
    }

    data class UserBaseInfo(
        @SerializedName("Name")
        val name: String,
        @SerializedName("Avatar")
        val avatar: String,
        @SerializedName("Gender")
        val gender: Int
    )

    fun toContentResource(): ContentResource {
        if (data == null || user == null || data.id.isEmpty() || user.name.isEmpty()) {
            return ContentResource(state = state, resource = null)
        }
        return ContentResource(
            state = state, resource = Content(
                userName = user.name,
                userAvatar = user.avatar,
                userGender = user.gender,
                id = data.id,
                name = data.name,
                detail = data.detail,
                ownId = data.ownId,
                publishDate = data.publishDate,
                editDate = data.editDate,
                likeNum = data.likeNum,
                commentNum = data.commentNum,
                publicContent = data.public,
                nativeContent = data.native,
                type = data.type,
                subType = data.subType,
                remarks = data.remarks,
                tags = data.tags
            ).apply {
                data.images?.forEach { images.add(it.toImage()) }
                data.files?.forEach { files.add(it) }
                movie.target = if (data.movie?.file?.file.isNullOrEmpty()) null else data.movie?.toMovie()
                album.target = if (data.album?.title.isNullOrEmpty()) null else data.album?.toAlbum()
                app.target = if (data.app?.file?.file.isNullOrEmpty()) null else data.app?.toApp()
            }
        )
    }
}
