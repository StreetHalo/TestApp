package trade.paper.app.listeners

interface LoginListener {
    fun onLoginComplete()
    fun onLoginFailed(message: String)
}