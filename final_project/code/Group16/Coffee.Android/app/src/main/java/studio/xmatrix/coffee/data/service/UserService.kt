package studio.xmatrix.coffee.data.service

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import io.objectbox.Box
import io.objectbox.kotlin.boxFor
import io.objectbox.kotlin.query
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import studio.xmatrix.coffee.App
import studio.xmatrix.coffee.data.common.network.ApiResponse
import studio.xmatrix.coffee.data.common.network.AppExecutors
import studio.xmatrix.coffee.data.model.User
import studio.xmatrix.coffee.data.model.User_
import studio.xmatrix.coffee.data.service.resource.CommonResource
import studio.xmatrix.coffee.data.service.resource.UserResource
import studio.xmatrix.coffee.data.service.response.UserInfoResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
interface UserService {

    companion object {
        const val BASE_URL = "$SERVICE_BASE_URL/user/"
    }

    @POST("login/pass")
    fun loginByPassword(@Body body: LoginRequestBody): LiveData<ApiResponse<CommonResource>>

    @POST("logout")
    fun logout(): LiveData<ApiResponse<CommonResource>>

    @POST("register")
    fun register(@Body body: RegisterRequestBody): LiveData<ApiResponse<CommonResource>>

    @POST("email")
    fun getEmailValidCode(): LiveData<ApiResponse<CommonResource>>

    @POST("valid")
    fun validEmail(@Body body: EmailValidRequestBody): LiveData<ApiResponse<CommonResource>>

    // 当id为self时获取自身数据
    @GET("info/{id}")
    fun getById(@Path("id") id: String): LiveData<ApiResponse<UserInfoResponse>>

    @POST("info")
    fun update(): LiveData<ApiResponse<CommonResource>>

    @POST("info/name")
    fun updateName(@Body body: NameRequestBody): LiveData<ApiResponse<CommonResource>>

    data class LoginRequestBody(val name: String, val password: String)
    data class RegisterRequestBody(val name: String, val email: String, val password: String)
    data class EmailValidRequestBody(val vCode: String)
    data class NameRequestBody(val name: String)
}

@Singleton
class UserDatabase @Inject constructor(app: App, private val executors: AppExecutors) {

    private val box: Box<User> = app.boxStore.boxFor()

    fun loadInfoById(id: String): LiveData<UserResource> {
        val data = MutableLiveData<UserResource>()
        executors.diskIO().execute {
            data.postValue(UserResource(CommonResource.StatusSuccess, box.query { equal(User_.id, id) }.findFirst()))
        }
        return data
    }

    fun saveInfo(user: User) {
        box.query { equal(User_.id, user.id) }.remove()
        box.put(user)
        return
    }
}
