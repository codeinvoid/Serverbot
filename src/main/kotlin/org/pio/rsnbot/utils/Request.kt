package org.pio.rsnbot.utils

import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.jetbrains.annotations.Nullable
import org.pio.rsnbot.api.State
import org.pio.rsnbot.config.Config.token

class Request: State {

    companion object {
        const val success = 200
        const val accept = 202
        val client = OkHttpClient()
        private val gson = Gson()
        val mediaType = "application/json; charset=utf-8".toMediaType()
    }

    @Nullable
    override fun <T> request(data: Class<T>, type: String, uuid: String, api: String): T? {
        val request = Request.Builder()
            .url("${api}/${uuid}/${type}")
            .build()
        client.newCall(request).execute().use { response ->
            if (response.code == success) {
                return gson.fromJson(response.body?.string() ?: String(), data)
            }
            return null
        }
    }

    override fun put(data: Any, type: String, uuid: String, api: String): Boolean {
        val jsonString = gson.toJson(data)
        val body: RequestBody = jsonString.trimIndent().toRequestBody(mediaType)
        val request = Request.Builder()
            .url("${api}/${uuid}/${type}")
            .addHeader("Authorization", token)
            .put(body)
            .build()
        client.newCall(request).execute().use { response ->
            return response.code == accept
        }
    }

    override fun post(data: Any, type: String, uuid: String, api: String): Boolean {
        val jsonString = gson.toJson(data)
        val body: RequestBody = jsonString.trimIndent().toRequestBody(mediaType)
        val request = Request.Builder()
            .url("${api}/${uuid}/${type}")
            .addHeader("Authorization", token)
            .post(body)
            .build()

        client.newCall(request).execute().use { response ->
            return response.code == success
        }
    }

    override fun delete(uuid: String): Boolean {
        val body: RequestBody = "".trimIndent().toRequestBody(mediaType)
        val request: Request = Request.Builder()
            .url("https://api.p-io.org/v1/players/$uuid")
            .delete(body)
            .addHeader("Authorization", token)
            .build()

        client.newCall(request).execute().use { response ->
            return response.code == accept
        }
    }
}
