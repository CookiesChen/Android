package studio.xmatrix.coffee.data.model

import com.google.gson.annotations.SerializedName
import io.objectbox.annotation.Convert
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToMany
import io.objectbox.relation.ToOne
import studio.xmatrix.coffee.data.common.converter.StringsConverter

@Entity
class Content constructor() {
    @Id
    var _id: Long = 0

    lateinit var userName: String
    lateinit var userAvatar: String
    var userGender: Int = User.GenderUnknown

    lateinit var id: String
    lateinit var name: String
    lateinit var detail: String
    lateinit var ownId: String
    var publishDate: Long = 0
    var editDate: Long = 0
    var likeNum: Long = 0
    var commentNum: Long = 0
    var publicContent: Boolean = false
    var nativeContent: Boolean = false
    lateinit var type: String
    lateinit var subType: String
    lateinit var remarks: String
    @Convert(converter = StringsConverter::class, dbType = String::class)
    lateinit var tags: List<String>

    lateinit var images: ToMany<Image>
    lateinit var files: ToMany<File>
    lateinit var movie: ToOne<Movie>
    lateinit var album: ToOne<Album>
    lateinit var app: ToOne<App>

    constructor(
        userName: String,
        userAvatar: String,
        userGender: Int,
        id: String,
        name: String,
        detail: String,
        ownId: String,
        publishDate: Long,
        editDate: Long,
        likeNum: Long,
        commentNum: Long,
        publicContent: Boolean,
        nativeContent: Boolean,
        type: String,
        subType: String,
        remarks: String,
        tags: List<String>
    ) : this() {
        this.userName = userName
        this.userAvatar = userAvatar
        this.userGender = userGender
        this.id = id
        this.name = name
        this.detail = detail
        this.ownId = ownId
        this.publishDate = publishDate
        this.editDate = editDate
        this.likeNum = likeNum
        this.commentNum = commentNum
        this.publicContent = publicContent
        this.nativeContent = nativeContent
        this.type = type
        this.subType = subType
        this.remarks = remarks
        this.tags = tags
    }

    companion object {
        const val TypeText = "Text"
        const val TypeAlbum = "Album"
        const val TypeFilm = "Film"
        const val TypeGame = "Game"
        const val TypeDoc = "Doc"
    }
}

@Entity
class Album constructor() {
    @Id
    var _id: Long = 0

    lateinit var title: String
    var time: Long = 0
    lateinit var location: String
    lateinit var images: ToMany<Image>

    constructor(title: String, time: Long, location: String) : this() {
        this.title = title
        this.time = time
        this.location = location
    }
}

@Entity
class App constructor() {

    @Id
    var _id: Long = 0

    lateinit var web: String
    lateinit var url: String
    lateinit var des: String
    lateinit var version: String

    lateinit var file: ToOne<File>
    lateinit var images: ToMany<Image>

    constructor(web: String, url: String, des: String, version: String) : this() {
        this.web = web
        this.url = url
        this.des = des
        this.version = version
    }
}

@Entity
class File constructor() {
    @Id
    var _id: Long = 0

    @SerializedName("File")
    lateinit var file: String
    @SerializedName("Size")
    var size: Long = 0
    @SerializedName("Title")
    lateinit var title: String
    @SerializedName("Time")
    var time: Long = 0
    @SerializedName("Count")
    var count: Long = 0
    @SerializedName("Type")
    lateinit var type: String

    constructor(file: String, size: Long, title: String, time: Long, count: Long) : this() {
        this.file = file
        this.size = size
        this.title = title
        this.time = time
        this.count = count
    }
}

@Entity
class Image constructor() {
    @Id
    var _id: Long = 0

    var native: Boolean = false
    lateinit var url: String
    lateinit var thumb: String
    lateinit var file: ToOne<File>

    constructor(native: Boolean, url: String, thumb: String) : this() {
        this.native = native
        this.url = url
        this.thumb = thumb
    }
}

@Entity
class Movie constructor() {

    @Id
    var _id: Long = 0

    lateinit var url: String
    lateinit var type: String
    lateinit var detail: String
    var watched: Boolean = false

    lateinit var file: ToOne<File>
    lateinit var images: ToMany<Image>

    constructor(url: String, type: String, detail: String, watched: Boolean) : this() {
        this.url = url
        this.type = type
        this.detail = detail
        this.watched = watched
    }
}
