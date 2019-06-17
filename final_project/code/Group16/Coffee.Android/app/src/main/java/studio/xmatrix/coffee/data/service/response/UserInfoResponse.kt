package studio.xmatrix.coffee.data.service.response

import com.google.gson.annotations.SerializedName
import studio.xmatrix.coffee.data.model.User
import studio.xmatrix.coffee.data.service.resource.UserResource

data class UserInfoResponse(
    @SerializedName("ID")
    val id: String,
    @SerializedName("State")
    val state: String,
    @SerializedName("Email")
    val email: String,
    @SerializedName("Name")
    val name: String,
    @SerializedName("Class")
    val userClass: Int,
    @SerializedName("Info")
    val info: UserInfo,
    @SerializedName("MaxSize")
    val maxSize: Long,
    @SerializedName("UsedSize")
    val usedSize: Long,
    @SerializedName("SingleSize")
    val singleSize: Long
) {
    data class UserInfo(
        @SerializedName("Name")
        val name: String,
        @SerializedName("Avatar")
        val avatar: String,
        @SerializedName("Bio")
        val bio: String,
        @SerializedName("Gender")
        val gender: Int
        // @SerializedName("NikeName")
        // val nickname: String
    )

    fun toUserResource(): UserResource {
        if (id.isEmpty()) {
            return UserResource(state, null)
        }
        return UserResource(
            state, User(
                id = id,
                email = email,
                userClass = userClass,
                name = info.name,
                avatar = info.avatar,
                bio = info.bio,
                gender = info.gender,
                maxSize = maxSize,
                usedSize = usedSize,
                singleSize = singleSize
            )
        )
    }
}
