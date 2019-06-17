package studio.xmatrix.coffee.data.repository

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.graphics.Bitmap
import studio.xmatrix.coffee.App
import studio.xmatrix.coffee.data.common.network.*
import studio.xmatrix.coffee.data.service.ImageDatabase
import studio.xmatrix.coffee.data.service.ImageService
import studio.xmatrix.coffee.data.service.UserDatabase
import studio.xmatrix.coffee.data.service.UserService
import studio.xmatrix.coffee.data.service.resource.CommonResource
import studio.xmatrix.coffee.data.service.resource.UserResource
import studio.xmatrix.coffee.data.service.response.UserInfoResponse
import studio.xmatrix.coffee.data.store.DefaultSharedPref
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val app: App,
    private val executors: AppExecutors,
    private val service: UserService,
    private val database: UserDatabase,
    private val imageService: ImageService,
    private val imageDatabase: ImageDatabase
) {

    fun login(name: String, password: String): LiveData<Resource<CommonResource>> {
        return object : NetworkDirectiveResource<CommonResource, CommonResource>(executors) {
            override fun createCall(): LiveData<ApiResponse<CommonResource>> {
                val bytes = password.toByteArray()
                val md = MessageDigest.getInstance("SHA-512")
                val digest = md.digest(bytes)
                val hash = digest.fold("") { str, it -> str + "%02x".format(it) }
                return service.loginByPassword(UserService.LoginRequestBody(name, hash))
            }

            override fun convertToResource(data: CommonResource) = data
        }.asLiveData()
    }

    fun logout(): LiveData<Resource<CommonResource>> {
        DefaultSharedPref.remove(DefaultSharedPref.Key.UserId)
        return object : NetworkDirectiveResource<CommonResource, CommonResource>(executors) {
            override fun convertToResource(data: CommonResource) = data
            override fun createCall() = service.logout()
        }.asLiveData()
    }

    fun register(name: String, email: String, password: String): LiveData<Resource<CommonResource>> {
        return object : NetworkDirectiveResource<CommonResource, CommonResource>(executors) {
            override fun createCall(): LiveData<ApiResponse<CommonResource>> {
                val bytes = password.toByteArray()
                val md = MessageDigest.getInstance("SHA-512")
                val digest = md.digest(bytes)
                val hash = digest.fold("") { str, it -> str + "%02x".format(it) }
                return service.register(UserService.RegisterRequestBody(name, email, hash))
            }

            override fun convertToResource(data: CommonResource) = data
        }.asLiveData()
    }

    fun getEmailValidCode(): LiveData<Resource<CommonResource>> {
        return object : NetworkDirectiveResource<CommonResource, CommonResource>(executors) {
            override fun createCall() = service.getEmailValidCode()
            override fun convertToResource(data: CommonResource) = data
        }.asLiveData()
    }

    fun validEmail(vcode: String): LiveData<Resource<CommonResource>> {
        return object : NetworkDirectiveResource<CommonResource, CommonResource>(executors) {
            override fun createCall() = service.validEmail(UserService.EmailValidRequestBody(vcode))
            override fun convertToResource(data: CommonResource) = data
        }.asLiveData()
    }

    fun getInfo(): LiveData<Resource<UserResource>> {
        var state = CommonResource.StatusSuccess
        return object : NetworkBoundResource<UserResource, UserInfoResponse>(executors) {
            override fun saveCallResult(item: UserInfoResponse) {
                state = item.state
                val res = item.toUserResource()
                if (item.state == CommonResource.StatusSuccess && res.resource != null) {
                    DefaultSharedPref.set(DefaultSharedPref.Key.UserId, item.id)
                    database.saveInfo(res.resource)
                } else {
                    DefaultSharedPref.remove(DefaultSharedPref.Key.UserId)
                }
            }

            override fun shouldFetch(data: UserResource?) = true

            override fun loadFromDb(): LiveData<UserResource> {
                val id = DefaultSharedPref.get(DefaultSharedPref.Key.UserId)
                return if (state == CommonResource.StatusSuccess && !id.isEmpty()) {
                    database.loadInfoById(id)
                } else {
                    val data = MutableLiveData<UserResource>()
                    executors.diskIO().execute { data.postValue(UserResource(state, null)) }
                    data
                }
            }

            override fun createCall() = service.getById("self")
        }.asLiveData()
    }

    fun getInfoById(id: String): LiveData<Resource<UserResource>> {
        return object : NetworkDirectiveResource<UserResource, UserInfoResponse>(executors) {
            override fun convertToResource(data: UserInfoResponse) = data.toUserResource()
            override fun createCall() = service.getById(id)
        }.asLiveData()
    }

    fun getAvatarByUrl(url: String): LiveData<Resource<Bitmap>> {
        return object : NetworkBoundResource<Bitmap, Bitmap>(executors) {
            override fun saveCallResult(item: Bitmap) = imageDatabase.saveById(url, item)
            override fun shouldFetch(data: Bitmap?) = data == null
            override fun loadFromDb() = imageDatabase.loadById(url)
            override fun createCall() = imageService.getByUrl(url)
        }.asLiveData()
    }

    fun updateInfoFromViolet(): LiveData<Resource<CommonResource>> {
        return object : NetworkDirectiveResource<CommonResource, CommonResource>(executors) {
            override fun convertToResource(data: CommonResource) = data
            override fun createCall() = service.update()
        }.asLiveData()
    }

    fun updateUserName(name: String): LiveData<Resource<CommonResource>> {
        return object : NetworkDirectiveResource<CommonResource, CommonResource>(executors) {
            override fun convertToResource(data: CommonResource) = data
            override fun createCall() = service.updateName(UserService.NameRequestBody(name))
        }.asLiveData()
    }
}
