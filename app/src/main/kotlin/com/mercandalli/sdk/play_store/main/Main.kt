package com.mercandalli.sdk.play_store.main

import com.mercandalli.sdk.play_store.upload.AutoApkUpload

fun main(args: Array<String>) {
    val shouldAddDefaultJsonPath = args.isEmpty() || args.size == 1 && args[0] == AutoApkUpload.ARG_FORCE
    val argsList = if (shouldAddDefaultJsonPath) {
        val list = ArrayList(args.toList())
        list.add("./play-store-publish.json")
        list
    } else {
        args.toList()
    }
    MainStart().start(argsList)
}