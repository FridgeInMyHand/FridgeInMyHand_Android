package com.kykint.composestudy.api

import com.kykint.composestudy.App
import com.kykint.composestudy.BuildConfig
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Protocol
import okhttp3.ResponseBody.Companion.toResponseBody

class MockResponse(
    private val responseJson: String,
    private val responseCode: Int,
    private val message: String = "",
    private val protocol: Protocol = Protocol.HTTP_1_1,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
        if (!BuildConfig.DEBUG) {
            throw IllegalAccessError("MockInterceptor is only meant for testing purposes!")
        }

        val request = chain.request()
        // val uri = request.url.toUri().toString()

        return okhttp3.Response.Builder()
            .request(request)
            .protocol(protocol)
            .code(responseCode)
            .message(message)
            .body(
                App.context.assets.open(responseJson).use {
                    it.readBytes().toResponseBody("application/json".toMediaType())
                }
            )
            .addHeader("content-type", "application/json")
            .build()
    }

    companion object {
        const val RESPONSE_PHOTO_ANALYSIS = "api-response/PhotoAnalysisResult.json"
        const val RESPONSE_FRIDGE_INFO = "api-response/FridgeInfoResult.json"
    }
}