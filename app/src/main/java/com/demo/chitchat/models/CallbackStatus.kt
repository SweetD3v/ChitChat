package com.demo.chitchat.models

sealed class CallbackStatus {
    object OTPSENT : CallbackStatus()
    object SUCCESS : CallbackStatus()
    object FAILED : CallbackStatus()
}