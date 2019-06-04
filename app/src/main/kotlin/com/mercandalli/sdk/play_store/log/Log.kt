package com.mercandalli.sdk.play_store.log

object Log {

    fun d(tag: String, message: String) {
        System.out.println("[PlayStore][$tag] $message")
    }

    fun e(tag: String, message: String) {
        System.out.println("[PlayStore][ERROR][$tag] $message")
    }
}