package com.mercandalli.sdk.play_store.upload

import org.json.JSONObject

import java.io.File
import java.lang.IllegalStateException

internal class Configuration(

        private val configurationFolderAbsolutePath: String,

        /**
         * Specify the name of your application. If the application name is
         * `null` or blank, the application will log a warning. Suggested
         * format is "MyCompany-Application/1.0".
         */
        private val appName: String,

        /**
         * Specify the package name of the app.
         */
        private val appPackage: String,

        private val channel: String,
        private val rolloutPercentage: Double,
        private val appBundlePath: String,
        private val clientSecretPath: String
) {

    fun getAppName() = appName
    fun getAppPackage() = appPackage
    fun getChannel() = channel
    fun getRolloutPercentage() = rolloutPercentage
    fun getAppBundleAbsolutePath() = File(configurationFolderAbsolutePath, appBundlePath).absolutePath!!
    fun getClientSecretAbsolutePath() = File(configurationFolderAbsolutePath, clientSecretPath).absolutePath!!

    companion object {

        fun fromFilePath(configurationFilePath: String): Configuration {
            val file = File(configurationFilePath)
            if (!file.exists()) {
                throw IllegalStateException("File does not exist. configurationFilePath: $configurationFilePath")
            }
            val jsonObject = JSONObject(file.readText())
            if (!jsonObject.has("application_name")) {
                throw IllegalStateException("You should provide 'application_name' with a value in the configuration file")
            }
            if (!jsonObject.has("application_package")) {
                throw IllegalStateException("You should provide 'application_package' with a value in the configuration file")
            }
            if (!jsonObject.has("channel")) {
                throw IllegalStateException("You should provide 'channel' with a value ('alpha', 'beta', 'production' or 'rollout') in the configuration file")
            }
            if (!jsonObject.has("rollout_percentage")) {
                throw IllegalStateException("You should provide 'rollout_percentage' with a value (inside ]0;1]) in the configuration file")
            }
            if (jsonObject.getString("channel") == Channel.CHANNEL_ROLLOUT &&
                    jsonObject.getDouble("rollout_percentage") == 0.0) {
                throw IllegalStateException("You should provide 'rollout_percentage' with a value (inside ]0;1]) in the configuration file")
            }
            if (!jsonObject.has("app_bundle_path")) {
                throw IllegalStateException("You should provide 'app_bundle_path' with a value in the configuration file")
            }
            if (!jsonObject.has("client_secret_path")) {
                throw IllegalStateException("You should provide 'client_secret_path' with a value in the configuration file")
            }
            return Configuration(
                    file.parentFile.absolutePath,
                    jsonObject.getString("application_name"),
                    jsonObject.getString("application_package"),
                    jsonObject.getString("channel"),
                    jsonObject.getDouble("rollout_percentage"),
                    jsonObject.getString("app_bundle_path"),
                    jsonObject.getString("client_secret_path")
            )
        }
    }
}
