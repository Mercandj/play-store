package com.mercandalli.sdk.play_store.main

import com.mercandalli.sdk.play_store.upload.AppBundleUpload
import java.io.File

object MainAppBundleUpload {

    /**
     * Ide main.
     */
    @JvmStatic
    fun main(args: Array<String>) {
        val argsList = args.toMutableList()
        val paramsPath = File("app/sampledata/play-store-publish.json").absolutePath
        argsList.add(paramsPath)
        start(argsList)
    }

    fun start(args: List<String>) {
        val argsList = args.toMutableList()
        val shouldAddDefaultJsonPath = argsList.isEmpty() || argsList.size == 1 && argsList[0] == AppBundleUpload.ARG_FORCE
        if (shouldAddDefaultJsonPath) {
            argsList.add("./play-store-publish.json")
        }
        AppBundleUpload.main(argsList.toTypedArray())
    }
}