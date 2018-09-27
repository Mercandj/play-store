package com.mercandalli.sdk.play_store.main

import com.mercandalli.sdk.play_store.upload.AppBundleUpload
import com.mercandalli.sdk.play_store.upload.ImageUpload
import java.io.File

object MainImageUpload {

    /**
     * Ide main.
     */
    @JvmStatic
    fun main(args: Array<String>) {
        val argsList = args.toMutableList()
        val paramsPath = File("app/sampledata/play-store-image.json").absolutePath
        argsList.add(paramsPath)
        start(argsList)
    }

    fun start(args: List<String>) {
        val argsList = args.toMutableList()
        val shouldAddDefaultJsonPath = argsList.isEmpty() || argsList.size == 1 && argsList[0] == AppBundleUpload.ARG_FORCE
        if (shouldAddDefaultJsonPath) {
            argsList.add("./play-store-image.json")
        }
        ImageUpload.main(args.toTypedArray())
    }
}