package com.mercandalli.sdk.play_store.upload

import org.json.JSONArray
import org.json.JSONObject

import java.io.File
import java.lang.IllegalStateException

internal class ImageConfiguration(

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

        private val clientSecretPath: String,

        private val images: List<Image>
) {

    fun getAppName() = appName
    fun getAppPackage() = appPackage
    fun getImages() = ArrayList<Image>(images)
    fun getClientSecretAbsolutePath() = File(configurationFolderAbsolutePath, clientSecretPath).absolutePath!!

    data class Image(
            val configurationFolderAbsolutePath: String,
            val path: String,
            val language: String,
            val type: String
    ) {

        fun getAbsolutePath() = File(configurationFolderAbsolutePath, path).absolutePath!!

        companion object {

            fun fromJson(jsonArray: JSONArray, configurationFolderAbsolutePath: String): List<Image> {
                val images = ArrayList<Image>()
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    val image = fromJson(jsonObject, configurationFolderAbsolutePath)
                    images.add(image)
                }
                return images
            }

            private fun fromJson(jsonObject: JSONObject, configurationFolderAbsolutePath: String): Image {
                return Image(
                        configurationFolderAbsolutePath,
                        jsonObject.getString("path"),
                        jsonObject.getString("language"),
                        jsonObject.getString("type")
                )
            }
        }
    }

    companion object {

        fun fromFilePath(configurationFilePath: String): ImageConfiguration {
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
            if (!jsonObject.has("client_secret_path")) {
                throw IllegalStateException("You should provide 'client_secret_path' with a value in the configuration file")
            }
            if (!jsonObject.has("images")) {
                throw IllegalStateException("You should provide 'images' with a value in the configuration file")
            }
            val configurationFolderAbsolutePath = file.parentFile.absolutePath
            return ImageConfiguration(
                    configurationFolderAbsolutePath,
                    jsonObject.getString("application_name"),
                    jsonObject.getString("application_package"),
                    jsonObject.getString("client_secret_path"),
                    Image.fromJson(jsonObject.getJSONArray("images"), configurationFolderAbsolutePath)
            )
        }
    }
}
