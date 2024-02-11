package com.demo.chitchat.models

data class LoginResult(
    val status: CallbackStatus,
    var verificationId: String = "",
    var error: String? = null
) {

}