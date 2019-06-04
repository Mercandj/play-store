package com.mercandalli.sdk.play_store.upload

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.HttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.repackaged.com.google.common.base.Preconditions
import com.google.api.client.repackaged.com.google.common.base.Strings
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.androidpublisher.AndroidPublisher
import com.google.api.services.androidpublisher.AndroidPublisherScopes

import java.io.*
import java.security.GeneralSecurityException

/**
 * Helper class to initialize the publisher APIs client library.
 *
 *
 * Before making any calls to the API through the client library you need to
 * call the [AndroidPublisherHelper.init] method. This will run
 * all precondition checks for for client id and secret setup properly in
 * resources/client_secrets.json and authorize this client against the API.
 *
 */
object AndroidPublisherHelper {

    /**
     * Directory to store user credentials
     */
    private const val DATA_STORE_SYSTEM_PROPERTY = "user.home"
    private const val DATA_STORE_FILE = ".store/android_publisher_api"
    private val DATA_STORE_DIR = File(
            System.getProperty(DATA_STORE_SYSTEM_PROPERTY),
            DATA_STORE_FILE
    )

    /**
     * Global instance of the JSON factory.
     */
    private val JSON_FACTORY = JacksonFactory.getDefaultInstance()

    /**
     * Global instance of the HTTP transport.
     */
    private var HTTP_TRANSPORT: HttpTransport? = null

    /**
     * Installed application user ID.
     */
    private const val INST_APP_USER_ID = "user"

    /**
     * Performs all necessary setup steps for running requests against the API
     * using the Installed Application auth method.
     *
     * @param applicationName the name of the application: com.example.app
     * @return the [AndroidPublisher] service
     */
    @Throws(IOException::class, GeneralSecurityException::class)
    internal fun init(
            applicationName: String,
            clientSecretsPath: String
    ): AndroidPublisher {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(applicationName),
                "applicationName cannot be null or empty!")

        // Authorization.
        newTrustedTransport()
        val credential = authorizeWithInstalledApplication(clientSecretsPath)

        // Set up and return API client.
        return AndroidPublisher.Builder(
                HTTP_TRANSPORT!!,
                JSON_FACTORY,
                credential
        )
                .setApplicationName(applicationName)
                .build()
    }

    /**
     * Authorizes the installed application to access user's protected data.
     */
    @Throws(IOException::class)
    private fun authorizeWithInstalledApplication(
            clientSecretsPath: String
    ): Credential {

        // load client secrets
        val file = File(clientSecretsPath)
        if (!file.exists()) {
            throw IllegalStateException("Cannot find file: ${file.absolutePath} with clientSecretsPath: $clientSecretsPath")
        }
        val resourceAsStream = FileInputStream(file)

        val clientSecrets = GoogleClientSecrets.load(
                JSON_FACTORY,
                InputStreamReader(resourceAsStream)
        )

        val dataStoreFactory = FileDataStoreFactory(DATA_STORE_DIR)

        // set up authorization code flow
        val flow = GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT,
                JSON_FACTORY,
                clientSecrets,
                setOf(AndroidPublisherScopes.ANDROIDPUBLISHER)
        )
                .setDataStoreFactory(dataStoreFactory)
                .build()
        // authorize
        return AuthorizationCodeInstalledApp(
                flow,
                LocalServerReceiver()
        ).authorize(INST_APP_USER_ID)
    }

    @Throws(GeneralSecurityException::class, IOException::class)
    private fun newTrustedTransport() {
        if (null == HTTP_TRANSPORT) {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport()
        }
    }
}
