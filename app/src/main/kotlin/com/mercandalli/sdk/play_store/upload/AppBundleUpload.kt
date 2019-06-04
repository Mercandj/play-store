package com.mercandalli.sdk.play_store.upload

import com.google.api.client.http.FileContent
import com.google.api.services.androidpublisher.AndroidPublisher
import com.google.api.services.androidpublisher.model.*
import com.mercandalli.sdk.play_store.log.Log

import java.io.File
import java.io.IOException
import java.nio.file.Path
import java.security.GeneralSecurityException
import java.util.*

object AppBundleUpload {

    private const val TAG = "AppBundleUpload"

    /**
     * https://developers.google.com/android-publisher/api-ref/edits/bundles/upload
     */
    private const val MIME_TYPE_BUNDLE = "application/octet-stream"

    const val CHANNEL_ROLLOUT = "rollout"

    const val ARG_FORCE = "--force"

    @JvmStatic
    fun main(args: Array<String>) {
        val configurationFilePath = extractConfigurationFilePath(args.toList())
        if (configurationFilePath.isEmpty()) {
            Log.e(TAG, "configurationFilePath is empty")
            return
        }
        val force = extractIsForced(args)
        Log.d(TAG, "configurationFilePath: $configurationFilePath")

        val appBundleConfiguration = AppBundleConfiguration.fromFilePath(configurationFilePath)
        val appBundlePath = extractAppBundlePath(appBundleConfiguration)

        displayUploadInformation(
                appBundleConfiguration,
                appBundlePath
        )
        if (force) {
            Log.d(TAG, "Upload forced.")
        } else if (!confirmRelease("\nStart to upload?")) {
            Log.d(TAG, "Upload has been canceled.")
            return
        }

        val applicationName = appBundleConfiguration.getAppName()
        val clientSecretsPath = appBundleConfiguration.getClientSecretAbsolutePath()
        val appPackage = appBundleConfiguration.getAppPackage()
        val channel = appBundleConfiguration.getChannel()
        try {
            // Create the API service.
            val service = AndroidPublisherHelper.init(
                    applicationName,
                    clientSecretsPath
            )
            val edits = service.edits()

            // Create a new edit to make changes to your listing.
            val editRequest = edits.insert(
                    appPackage,
                    null
            )
            val edit = editRequest.execute()
            val editId = edit.id
            val appVersionCode = uploadAppBundle(
                    appPackage,
                    edits,
                    editId,
                    appBundlePath
            )
            val trackRelease = TrackRelease()
            if (channel == "rollout") {
                trackRelease.userFraction = appBundleConfiguration.getRolloutPercentage()
            }
            trackRelease.versionCodes = Collections.singletonList(appVersionCode)
            trackRelease.status = "completed"
            val content = Track().setReleases(Collections.singletonList(trackRelease))
            val updateTrackRequest = edits
                    .tracks()
                    .update(
                            appPackage,
                            editId,
                            channel,
                            content
                    )
            updateTrackRequest.execute()
            displayUploadData(appBundleConfiguration, appVersionCode)

            if (force) {
                println("Creation of a new release forced, channel: $channel.")
            } else if (!confirmRelease("Confirm creation of a new release?")) {
                println("AppBundle upload has been canceled, channel: $channel.")
                return
            }

            // Commit changes for edit.
            val commitRequest = edits.commit(
                    appPackage,
                    editId
            )
            commitRequest.execute()
        } catch (ioException: IOException) {
            val message = ioException.message
            val stackTrace = ioException.stackTrace.joinToString("\n")
            Log.e(TAG, "Exception was thrown while uploading apk")
            Log.e(TAG, "Message: $message")
            Log.e(TAG, "StackTrace: $stackTrace")
        } catch (generalSecurityException: GeneralSecurityException) {
            val message = generalSecurityException.message
            val cause = generalSecurityException.cause
            val stackTrace = generalSecurityException.stackTrace.joinToString("\n")
            Log.e(TAG, "Exception was thrown while uploading apk")
            Log.e(TAG, "Message: $message")
            Log.e(TAG, "Cause: $cause")
            Log.e(TAG, "StackTrace: $stackTrace")
        }
    }

    private fun extractAppBundlePath(appBundleConfiguration: AppBundleConfiguration): Path {
        if (!appBundleConfiguration.getAppBundleAbsolutePath().endsWith(".aab")) {
            throw IllegalStateException("Selected app bundle doesn't end with aab: " + appBundleConfiguration.getAppBundleAbsolutePath())
        }
        val appBundle = File(appBundleConfiguration.getAppBundleAbsolutePath())
        if (!appBundle.exists()) {
            throw IllegalStateException("Selected app bundle doesn't exist: " + appBundleConfiguration.getAppBundleAbsolutePath())
        }
        return appBundle.toPath()
    }

    @Throws(IOException::class)
    private fun uploadAppBundle(
            appPackage: String,
            edits: AndroidPublisher.Edits,
            editId: String,
            appBundlePath: Path
    ): Long {
        val file = appBundlePath.toFile()

        println("AppBundle path to upload : " + file.absolutePath)

        val apkPathString = file.absolutePath
        val apkFile = FileContent(
                MIME_TYPE_BUNDLE,
                File(apkPathString)
        )
        val uploadRequest = edits
                .bundles()
                .upload(appPackage, editId, apkFile)
        val bundle = uploadRequest.execute()

        // Assign apk to alpha track.
        return bundle.versionCode!!.toLong()
    }

    private fun displayUploadInformation(
            appBundleConfiguration: AppBundleConfiguration,
            appBundlePath: Path
    ) {
        Log.d(TAG, "\nUpload information :\n")
        Log.d(TAG, "App name: " + appBundleConfiguration.getAppName())
        Log.d(TAG, "App package: " + appBundleConfiguration.getAppPackage())
        Log.d(TAG, "APK(s) directory: " + appBundleConfiguration.getAppBundleAbsolutePath())
        Log.d(TAG, "Distribution channel: " + appBundleConfiguration.getChannel())
        if (appBundleConfiguration.getChannel() == CHANNEL_ROLLOUT) {
            Log.d(TAG, "Percentage rollout: " + appBundleConfiguration.getRolloutPercentage())
        }
        Log.d(TAG, "\nYou are about to upload appBundle" + appBundlePath.fileName + ".")
    }

    private fun displayUploadData(
            appBundleConfiguration: AppBundleConfiguration,
            appVersionCode: Long?
    ) {
        println("You will upload app bundle with version code " +
                appVersionCode + " to the " + appBundleConfiguration.getChannel() + " channel.")
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

    private fun extractConfigurationFilePath(args: List<String>): String {
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

    private fun extractIsForced(args: Array<String>) = args.contains(ARG_FORCE)

    private fun println(message: String) {
        Log.d(TAG, message)
    }
}
