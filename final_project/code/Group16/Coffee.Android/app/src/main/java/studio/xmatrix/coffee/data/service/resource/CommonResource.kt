package studio.xmatrix.coffee.data.service.resource

import com.google.gson.annotations.SerializedName

data class CommonResource(
    @SerializedName("State")
    val state: String,
    @SerializedName("Data")
    val resource: String
) {
    companion object {
        const val StatusSuccess = "success"
        const val StatusBadReq = "bad_req"
        const val StatusNotLogin = "not_login"
        const val StatusNotAllow = "not_allow"
        const val StatusExist = "had_exist"
        const val StatusError = "error"
        const val StatusNotValid = "not_invalid"
        // sign up
        const val StatusNotValidUsername = "invalid_name"
        const val StatusReservedUsername = "reserved_name"
        const val StatusNotValidEmail = "invalid_email"
        const val StatusExistUsername = "exist_name"
        const val StatusExistEmail = "exist_email"
        // verify
        const val StatusErrorCode = "error_emailCode"
        const val StatusTimeOutCode = "timeout_emailCode"
    }
}
