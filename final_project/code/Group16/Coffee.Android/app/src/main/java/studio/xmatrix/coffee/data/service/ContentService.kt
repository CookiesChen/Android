package studio.xmatrix.coffee.data.service

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.graphics.Bitmap
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import io.objectbox.Box
import io.objectbox.kotlin.boxFor
import io.objectbox.kotlin.query
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*
import studio.xmatrix.coffee.data.common.network.ApiResponse
import studio.xmatrix.coffee.data.common.network.AppExecutors
import studio.xmatrix.coffee.data.model.*
import studio.xmatrix.coffee.data.service.resource.CommonResource
import studio.xmatrix.coffee.data.service.resource.ContentsResource
import studio.xmatrix.coffee.data.service.response.ContentResponse
import studio.xmatrix.coffee.data.service.response.ContentsResponse
import studio.xmatrix.coffee.data.service.response.PublishContentsResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
interface ContentService {

    companion object {
        const val BASE_URL = "$SERVICE_BASE_URL/content/"
    }

    @DELETE("{id}")
    fun deleteById(@Path("id") contentId: String): LiveData<ApiResponse<CommonResource>>

    @GET("detail/{id}")
    fun getById(@Path("id") contentId: String): LiveData<ApiResponse<ContentResponse>>

    @GET("public")
    fun getPublicListByPage(@Query("page") page: Int, @Query("eachPage") eachPage: Int): LiveData<ApiResponse<PublishContentsResponse>>

    @Multipart
    @POST("album")
    fun addAlbum(@Part parts: List<MultipartBody.Part>): LiveData<ApiResponse<CommonResource>>

    // 当id为self时获取自身数据
    @GET("album/{id}")
    fun getAlbumsByUserId(@Path("id") id: String): LiveData<ApiResponse<ContentsResponse>>

    // 目前仅用于获取图片
    @GET("$SERVICE_BASE_URL/file/{id}/{path}")
    fun getImageByIdAndPath(@Path("id") id: String, @Path("path") path: String): LiveData<ApiResponse<Bitmap>>

    // 当id为self时获取自身数据
    @GET("texts/{id}")
    fun getTextsByUserId(@Path("id") id: String): LiveData<ApiResponse<ContentsResponse>>

    @POST("text")
    fun addText(@Body body: ContentRequestBody): LiveData<ApiResponse<CommonResource>>

    @PATCH("all/{id}")
    fun updateById(@Path("id") id: String, @Body body: ContentRequestBody): LiveData<ApiResponse<CommonResource>>

    data class ContentRequestBody(
        val title: String,
        val detail: String,
        val tags: List<String>,
        @SerializedName("isPublic")
        val public: Boolean
    ) {
        fun toPartList(): List<MultipartBody.Part> {
            val list = ArrayList<MultipartBody.Part>()
            list.add(MultipartBody.Part.createFormData("title", title))
            list.add(MultipartBody.Part.createFormData("detail", detail))
            list.add(MultipartBody.Part.createFormData("tags", tags.joinToString()))
            list.add(MultipartBody.Part.createFormData("isPublic", public.toString()))
            return list
        }
    }
}

@Singleton
class ContentDatabase @Inject constructor(app: studio.xmatrix.coffee.App, private val executors: AppExecutors) {

    private val box: Box<Content> = app.boxStore.boxFor()

    private val albumBox: Box<Album> = app.boxStore.boxFor()
    private val appBox: Box<App> = app.boxStore.boxFor()
    private val fileBox: Box<File> = app.boxStore.boxFor()
    private val imageBox: Box<Image> = app.boxStore.boxFor()
    private val movieBox: Box<Movie> = app.boxStore.boxFor()

    fun deleteById(id: String) {
        box.query { equal(Content_.id, id) }.remove()
    }

    fun loadPublicContentsByNum(num: Int): LiveData<ContentsResource> {
        val data = MutableLiveData<ContentsResource>()
        executors.diskIO().execute {
            val res = ContentsResource(CommonResource.StatusSuccess, box.query {
                equal(Content_.publicContent, true)
                notEqual(Content_.userName, "")
                orderDesc(Content_.publishDate)
            }.find(0, num.toLong()))
            data.postValue(res)
        }
        return data
    }

    fun loadContentsByUserIdAndType(userId: String, type: String): LiveData<ContentsResource> {
        val data = MutableLiveData<ContentsResource>()
        executors.diskIO().execute {
            val res = ContentsResource(CommonResource.StatusSuccess, box.query {
                equal(Content_.ownId, userId)
                equal(Content_.type, type)
                equal(Content_.userName, "")
                orderDesc(Content_.publishDate)
            }.find())
            data.postValue(res)
        }
        return data
    }

    fun savePublicContents(items: List<Content>?) {
        box.query {
            equal(Content_.publicContent, true)
            notEqual(Content_.userName, "")
        }.find().forEach {
            if (!it.album.isNull) {
                it.album.target.images.forEach { image ->
                    if (!image.file.isNull) fileBox.remove(image.file.targetId)
                    imageBox.remove(image._id)
                }
                albumBox.remove(it.album.targetId)
            }
            if (!it.app.isNull) {
                if (!it.app.target.file.isNull) fileBox.remove(it.app.target.file.targetId)
                it.app.target.images.forEach { image ->
                    if (!image.file.isNull) fileBox.remove(image.file.targetId)
                    imageBox.remove(image._id)
                }
                appBox.remove(it.app.targetId)
            }
            if (!it.movie.isNull) {
                if (!it.movie.target.file.isNull) fileBox.remove(it.movie.target.file.targetId)
                it.movie.target.images.forEach { image ->
                    if (!image.file.isNull) fileBox.remove(image.file.targetId)
                    imageBox.remove(image._id)
                }
                movieBox.remove(it.movie.targetId)
            }
            it.files.forEach { file -> fileBox.remove(file._id) }
            it.images.forEach { image ->
                if (!image.file.isNull) fileBox.remove(image.file.targetId)
                imageBox.remove(image._id)
            }
            box.remove(it._id)
        }
        items?.forEach { item ->
            box.query {
                equal(Content_.id, item.id)
                notEqual(Content_.userName, "")
            }.find().forEach {
                if (!it.album.isNull) {
                    it.album.target.images.forEach { image ->
                        if (!image.file.isNull) fileBox.remove(image.file.targetId)
                        imageBox.remove(image._id)
                    }
                    albumBox.remove(it.album.targetId)
                }
                if (!it.app.isNull) {
                    if (!it.app.target.file.isNull) fileBox.remove(it.app.target.file.targetId)
                    it.app.target.images.forEach { image ->
                        if (!image.file.isNull) fileBox.remove(image.file.targetId)
                        imageBox.remove(image._id)
                    }
                    appBox.remove(it.app.targetId)
                }
                if (!it.movie.isNull) {
                    if (!it.movie.target.file.isNull) fileBox.remove(it.movie.target.file.targetId)
                    it.movie.target.images.forEach { image ->
                        if (!image.file.isNull) fileBox.remove(image.file.targetId)
                        imageBox.remove(image._id)
                    }
                    movieBox.remove(it.movie.targetId)
                }
                it.files.forEach { file -> fileBox.remove(file._id) }
                it.images.forEach { image ->
                    if (!image.file.isNull) fileBox.remove(image.file.targetId)
                    imageBox.remove(image._id)
                }
                box.remove(it._id)
            }
        }
        box.put(items)
    }

    fun saveContents(userId: String, items: List<Content>?) {
        items?.forEach { item ->
            box.query {
                equal(Content_.id, item.id)
                equal(Content_.ownId, userId)
                equal(Content_.userName, "")
            }.find().forEach {
                if (!it.album.isNull) {
                    it.album.target.images.forEach { image ->
                        if (!image.file.isNull) fileBox.remove(image.file.targetId)
                        imageBox.remove(image._id)
                    }
                    albumBox.remove(it.album.targetId)
                }
                if (!it.app.isNull) {
                    if (!it.app.target.file.isNull) fileBox.remove(it.app.target.file.targetId)
                    it.app.target.images.forEach { image ->
                        if (!image.file.isNull) fileBox.remove(image.file.targetId)
                        imageBox.remove(image._id)
                    }
                    appBox.remove(it.app.targetId)
                }
                if (!it.movie.isNull) {
                    if (!it.movie.target.file.isNull) fileBox.remove(it.movie.target.file.targetId)
                    it.movie.target.images.forEach { image ->
                        if (!image.file.isNull) fileBox.remove(image.file.targetId)
                        imageBox.remove(image._id)
                    }
                    movieBox.remove(it.movie.targetId)
                }
                it.files.forEach { file -> fileBox.remove(file._id) }
                it.images.forEach { image ->
                    if (!image.file.isNull) fileBox.remove(image.file.targetId)
                    imageBox.remove(image._id)
                }
                box.remove(it._id)
            }
        }
        box.put(items)
    }
}
