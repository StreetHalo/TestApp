package trade.paper.app.models.rest.paper


import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("agreementAccepted")
    val agreementAccepted: Boolean,
    @SerializedName("avatar")
    val avatar: String,
    @SerializedName("createTime")
    val createTime: Long,
    @SerializedName("name")
    val name: String,
    @SerializedName("refCode")
    val refCode: String,
    @SerializedName("uid")
    val uid: String
)