package trade.paper.app.models.rpc.params

import com.google.gson.annotations.SerializedName

data class AuthParams(
        @SerializedName("algo") var algo: String,
        @SerializedName("pKey") var publicKey: String,
        @SerializedName("sKey") var secretKey: String
) : Params