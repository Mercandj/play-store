package com.mercandalli.sdk.play_store.main

import com.mercandalli.sdk.play_store.log.Log
import java.io.File

fun main(args: Array<String>) {
    val paramsPath = File("app/sampledata/play-store-publish.json").absolutePath
    Log.d("MainIde", "Params path: $paramsPath")
    MainStart().start(listOf(
            paramsPath
    ))
}