package com.kykint.composestudy.api

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.kykint.composestudy.data.photoanalysis.Response
import com.kykint.composestudy.utils.encode
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import java.io.File

interface PhotoAnalysisService {
    @Multipart
    @POST("getData")
    fun getObjectInfos(
        @Part file: MultipartBody.Part
    ): Call<Response>
}

object PhotoAnalysisClient {
    private const val baseurl = "http://192.168.110.3:6160/"
    private val retrofit = Retrofit.Builder()
        .baseUrl(baseurl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service = retrofit.create(PhotoAnalysisService::class.java)
}

class PhotoAnalysisRepository {
    fun getObjectInfos(
        context: Context,
        onSuccess: (Response?) -> Unit = {}, // TODO: Data shouldn't be null
        onFailure: () -> Unit = {},
    ) {
        val call = PhotoAnalysisClient.service.getObjectInfos(
            getImageBody(
                name="image",
                file=File(context.filesDir.path+"/logo_small.jpg"),
            )
        )

        call.enqueue(object : Callback<Response> {
            override fun onResponse(call: Call<Response>, response: retrofit2.Response<Response>) {
                if (response.isSuccessful && response.body() != null) {
                    onSuccess(response.body()!!)
                } else {
                    Toast.makeText(
                        context, "Response was successful, but something went wrong",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<Response>, t: Throwable) {
                Toast.makeText(
                    context, "Couldn't even establish connection!",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("PhotoAnalysis", t.message?:"")
            }
        })
    }
}

fun getImageBody(name: String, file: File): MultipartBody.Part {
    return MultipartBody.Part.createFormData(
        name = name,
        filename = file.name,
        body = file.asRequestBody("image/*".toMediaType())
    )
}