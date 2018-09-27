package com.mercandalli.sdk.play_store.upload

import com.google.api.client.http.FileContent
import com.google.api.services.androidpublisher.AndroidPublisher
import com.mercandalli.sdk.play_store.log.Log
import java.io.File
import java.io.IOException
import java.security.GeneralSecurityException
import java.util.*

object ImageUpload {

    private const val TAG = "ImageUpload"

    /**
     * https://developers.google.com/android-publisher/api-ref/edits/bundles/upload
     */
    private const val MIME_TYPE_BUNDLE = "application/octet-stream"

    private const val ARG_FORCE = "--force"

    @JvmStatic
    fun main(args: Array<String>) {
        val configurationFilePath = parseArguments(args.toList())

        val configuration = ImageConfiguration.fromFilePath(configurationFilePath)
        val force = isForced(args)

        if (force) {
            println("Upload forced.")
        } else {
            if (!confirmRelease("\nStart to upload?")) {
                println("Upload has been canceled.")
                return
            }
        }

        try {
            // Create the API service.
            val service = AndroidPublisherHelper.init(
                    configuration.getAppName(),
                    configuration.getClientSecretAbsolutePath()
            )
            for (image in configuration.getImages()) {
                if (uploadImage(service, configuration, image, force)) return
            }
        } catch (ex: IOException) {
            println("Exception was thrown while uploading apk : " + ex.message)
        } catch (ex: GeneralSecurityException) {
            println("Exception was thrown while uploading apk : " + ex.message)
        }
    }

    private fun uploadImage(
            service: AndroidPublisher,
            imageConfiguration: ImageConfiguration,
            image: ImageConfiguration.Image,
            force: Boolean
    ): Boolean {
        val edits = service.edits()

        // Create a new edit to make changes to your listing.
        val editRequest = edits.insert(
                imageConfiguration.getAppPackage(),
                null
        )
        val edit = editRequest.execute()
        val editId = edit.id
        // https://developers.google.com/android-publisher/api-ref/edits/images/upload
        val updateTrackRequest = edits
                .images()
                .upload(
                        imageConfiguration.getAppPackage(),
                        editId,
                        image.language,
                        image.type,
                        FileContent(
                                "image/png",
                                File(image.getAbsolutePath())
                        )
                )
        updateTrackRequest.execute()
        if (force) {
            println("Creation of a new release forced, channel: ${image.language}.")
        } else if (!confirmRelease("Confirm creation of a new release?")) {
            println("AppBundle upload has been canceled, channel: ${image.language}.")
            return true
        }

        // Commit changes for edit.
        val commitRequest = edits.commit(imageConfiguration.getAppPackage(), editId)
        commitRequest.execute()
        return false
    }

    private fun confirmRelease(message: String): Boolean {
        println("$message(Y/N)")
        val scanner = Scanner(System.`in`)
        var answer: String? = null
        while (scanner.hasNext()) {
            answer = scanner.next()
            if (answer == "Y" || answer == "N") {
                break
            }
        }
        return !(answer == null || answer == "N")
    }

    private fun parseArguments(args: List<String>): String {
        val argsCopy = ArrayList(args)
        if (argsCopy.contains(ARG_FORCE)) {
            argsCopy.remove(ARG_FORCE)
        }
        if (argsCopy.size != 1) {
            throw IllegalStateException("You should pass one parameter to the program which is the path of the" +
                    " configuration file.")
        }
        return argsCopy[0]
    }

    private fun isForced(args: Array<String>) = args.contains(ARG_FORCE)

    private fun println(message: String) {
        Log.d(TAG, message)
    }
}