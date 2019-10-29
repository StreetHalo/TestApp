package trade.paper.app.models.rest.paper

data class LoginCredentials (val credentials: Token)
data class Token(val fcmToken: String)