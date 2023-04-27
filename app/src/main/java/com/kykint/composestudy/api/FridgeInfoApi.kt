package com.kykint.composestudy.api

import android.util.Log
import android.widget.Toast
import androidx.annotation.WorkerThread
import com.kykint.composestudy.App
import com.kykint.composestudy.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.POST

interface FridgeInfoService {
    @POST("getData")
    fun getFridgeInfo(): Call<FridgeInfoResponse>
}

object FridgeInfoClient {
    private const val baseurl = "http://192.168.110.3:6160/"
    private val retrofit = Retrofit.Builder()
        .baseUrl(baseurl)
        .addConverterFactory(GsonConverterFactory.create())
        .apply {
            if (BuildConfig.DEBUG) {
                client(
                    OkHttpClient()
                        .newBuilder()
                        .addInterceptor(
                            MockResponse(MockResponse.RESPONSE_FRIDGE_INFO, 200)
                        )
                        .build()
                )
            }
        }
        .build()

    val service: FridgeInfoService = retrofit.create(FridgeInfoService::class.java)
}

object FridgeInfoApi {
    @WorkerThread
    suspend fun getFridgeInfo(
        onSuccess: (FridgeInfoResponse?) -> Unit = {}, // TODO: Data shouldn't be null
        onFailure: () -> Unit = {},
    ) {
        val call = FridgeInfoClient.service.getFridgeInfo()

        call.enqueue(object : Callback<FridgeInfoResponse> {
            override fun onResponse(
                call: Call<FridgeInfoResponse>,
                response: retrofit2.Response<FridgeInfoResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    onSuccess(response.body()!!)
                } else {
                    Toast.makeText(
                        App.context, "Response was successful, but something went wrong",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<FridgeInfoResponse>, t: Throwable) {
                Toast.makeText(
                    App.context, "Couldn't even establish connection!",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("FridgeInfo", t.message ?: "")
                onFailure()
            }
        })
    }
}
