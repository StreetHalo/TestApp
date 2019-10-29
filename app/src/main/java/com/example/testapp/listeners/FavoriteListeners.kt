package trade.paper.app.listeners

interface FavoriteListeners {
    fun removeFavoriteFragment()

    fun addFavoriteFragment()

    fun notifyChildsDataSetChanged()
}