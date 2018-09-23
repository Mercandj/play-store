package com.mercandalli.sdk.play_store.log

object Log {

    fun d(tag: String, message: String) {
        System.out.println("[$tag] $message")
    }
}