package com.mercandalli.sdk.play_store.main

import com.mercandalli.sdk.play_store.log.Log

fun main(args: Array<String>) {
    val argsList = args.toMutableList()
    if (argsList.contains("--image")) {
        argsList.remove("--image")
        Log.d("Main", "Upload images. Add --force to avoid check.")
        MainImageUpload.start(argsList)
        return
    }
    Log.d("Main", "Upload appBundle. Add --force to avoid check.")
    MainAppBundleUpload.start(argsList)
}