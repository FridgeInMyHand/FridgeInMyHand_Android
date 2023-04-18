package com.kykint.composestudy.api

import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.annotation.WorkerThread
import com.kykint.composestudy.App
import com.kykint.composestudy.data.photoanalysis.Response
import com.kykint.composestudy.utils.saveAsFile
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

    val service: PhotoAnalysisService = retrofit.create(PhotoAnalysisService::class.java)
}

object PhotoAnalysis {
    @WorkerThread
    suspend fun getObjectInfos(
        bitmap: Bitmap,
        onSuccess: (Response?) -> Unit = {},
        onFailure: () -> Unit = {},
    ) {
        val tempImage = App.context.filesDir.path + File.separator + "temp.png"
        val file = bitmap.saveAsFile(tempImage)
        file?.let {
            getObjectInfos(tempImage, onSuccess, onFailure)
        } ?: onFailure()
    }

    @WorkerThread
    suspend fun getObjectInfos(
        filePath: String,
        onSuccess: (Response?) -> Unit = {}, // TODO: Data shouldn't be null
        onFailure: () -> Unit = {},
    ) {
        val call = PhotoAnalysisClient.service.getObjectInfos(
            getImageBody(
                name = "image",
                file = File(filePath),
            )
        )

        Log.d("SERVER", "calling photo analysis server")
        call.enqueue(object : Callback<Response> {
            override fun onResponse(call: Call<Response>, response: retrofit2.Response<Response>) {
                if (response.isSuccessful && response.body() != null) {
                    onSuccess(response.body()!!)
                } else {
                    Toast.makeText(
                        App.context, "Response was successful, but something went wrong",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<Response>, t: Throwable) {
                Toast.makeText(
                    App.context, "Couldn't even establish connection!",
                    Toast.LENGTH_SHORT
                ).show()
                Log.e("PhotoAnalysis", t.message ?: "")
                onFailure()
            }
        })
    }

    private fun getImageBody(name: String, file: File): MultipartBody.Part {
        return MultipartBody.Part.createFormData(
            name = name,
            filename = file.name,
            body = file.asRequestBody("image/*".toMediaType())
        )
    }
}
