package studio.xmatrix.coffee.data.service

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import io.objectbox.Box
import io.objectbox.kotlin.boxFor
import io.objectbox.kotlin.query
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Url
import studio.xmatrix.coffee.App
import studio.xmatrix.coffee.data.common.network.ApiResponse
import studio.xmatrix.coffee.data.common.network.AppExecutors
import studio.xmatrix.coffee.data.model.ImageObjectId
import studio.xmatrix.coffee.data.model.ImageObjectId_
import java.io.File
import java.io.FileInputStream
import javax.inject.Inject
import javax.inject.Singleton

interface ImageService {

    companion object {
        const val BASE_URL = "$SERVICE_BASE_URL/thumb/"
    }

    @GET
    fun getByUrl(@Url url: String): LiveData<ApiResponse<Bitmap>>

    @GET("{filename}")
    fun getByFilename(@Path("filename") filename: String): LiveData<ApiResponse<Bitmap>>

}

@Singleton
class ImageDatabase @Inject constructor(private val app: App, private val executors: AppExecutors) {

    private val box: Box<ImageObjectId> = app.boxStore.boxFor()

    companion object {
        const val ImagePath = "/images/"
    }

    fun loadById(id: String): LiveData<Bitmap> {
        val data = MutableLiveData<Bitmap>()
        executors.diskIO().execute {
            var filename: String? = null
            box.query { equal(ImageObjectId_.id, id) }.findFirst()?.let { filename = it._id.toString() }
            if (filename == null) {
                data.postValue(null)
            } else {
                val folder = File(app.cacheDir, ImagePath)
                val file = File(folder, filename)
                if (file.exists()) {
                    try {
                        val options = BitmapFactory.Options()
                        options.inPreferredConfig = Bitmap.Config.ARGB_4444
                        val bitmap = BitmapFactory.decodeStream(FileInputStream(file), null, options)
                        data.postValue(bitmap)
                    } catch (e: Exception) {
                        data.postValue(null)
                    }
                } else {
                    box.query { equal(ImageObjectId_.id, id) }.remove()
                    data.postValue(null)
                }
            }
        }
        return data
    }

    fun saveById(id: String, bitmap: Bitmap) {
        val folder = File(app.cacheDir, ImagePath)
        if (!folder.exists()) {
            if (!folder.mkdirs()) {
                return
            }
        }
        if (box.query { equal(ImageObjectId_.id, id) }.findFirst() == null) {
            val filename = box.put(ImageObjectId(id)).toString()
            File(folder, filename).outputStream().use {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
            }
        }
    }
}
