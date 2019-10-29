package trade.paper.app.models.rest.paper


import com.google.gson.annotations.SerializedName

data class AgreementResponse(
    @SerializedName("avatar")
    val avatar: String,
    @SerializedName("refCode")
    val refCode: String
)