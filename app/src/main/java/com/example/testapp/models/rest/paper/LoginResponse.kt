package trade.paper.app.models.rest.paper


import com.google.gson.annotations.SerializedName

data class LoginResponse(
    @SerializedName("isNewUser")
    val isNewUser: Boolean,
    @SerializedName("sid")
    val sid: String,
    @SerializedName("user")
    val user: User
)