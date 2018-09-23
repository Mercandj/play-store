package com.mercandalli.sdk.play_store.main

import com.mercandalli.sdk.play_store.upload.AutoApkUpload

class MainStart {

    fun start(args: List<String>) {
        AutoApkUpload.main(args.toTypedArray())
    }
}