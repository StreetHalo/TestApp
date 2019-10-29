package trade.paper.app.views


interface LoginView : BaseView {
    fun loginDone()
    fun loginFailed(message: String)
    fun showWrongCode(){}
}