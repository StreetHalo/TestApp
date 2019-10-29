package trade.paper.app.models

enum class TAG(var tag: String) {
    SOCKET("SOCKET"),
    SOCKED_FAILED("SOCKET FAILED"),
    UUID("UUID"),
    PUBLIC_KEY("PUBLIC_API_KEY"),
    PRIVATE_KEY("PRIVATE_API_KEY"),
    SYMBOL("symbol"),
    APP_NAME("com.hittechsexpertlimited.hitbtc"),
    PERIOD("period"),
    PARENT("parent"),
    ERROR("error"),
    FCM("fcm"),
    NOTIFICATION_DATA("notificationData"),
    NOTIFICATION_TITLE("notificationTitle"),
    PIN_REQUEST("pin_request"),
    PIN_ENABLED("pin_enabled"),
    SET_PIN("set_pin"),
    DO_YOU_NEED_LOGIN("do_you_need_login"),
    REMOVE_PIN("remove_pin")

}