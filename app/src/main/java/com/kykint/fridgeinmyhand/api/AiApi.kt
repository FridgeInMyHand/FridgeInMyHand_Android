package com.kykint.fridgeinmyhand.api

import android.content.SharedPreferences
import android.graphics.Bitmap
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.annotation.WorkerThread
import com.google.gson.GsonBuilder
import com.google.gson.stream.JsonReader
import com.kykint.fridgeinmyhand.App
import com.kykint.fridgeinmyhand.utils.Prefs
import com.kykint.fridgeinmyhand.utils.saveAsFile
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.POST
import java.io.File
import java.io.InputStreamReader


interface AiApiService {
    /**
     * 음식 레이블 식별
     */
    @POST("/detect")
    fun postDetect(
        @Body body: RequestBody,
    ): Call<FoodClassificationResponse>
}

object AiApiClient {
    private var baseUrl = Prefs.aiApiAddress
    private val builder = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())

    lateinit var service: AiApiService

    private val prefChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            if (key == Prefs.Key.aiApiAddress) {
                baseUrl = Prefs.aiApiAddress
                builder.baseUrl(baseUrl)
                createService()
            }
        }

    init {
        createService()

        Prefs.registerPrefChangeListener(prefChangeListener)
    }

    private fun createService() {
        service = builder.build().create()
    }
}

object AiApi {
    private val foodCodesJson = "FoodCodes.json"
    private val foodLabels: Map<String, String> = run {
        App.context.assets.open(foodCodesJson).use { `is` ->
            val reader = JsonReader(InputStreamReader(`is`))
            // val type: Type = object : TypeToken<HashMap<String, Any>>() {}.type
            val map: Map<String, String> =
                GsonBuilder().create().fromJson(reader, HashMap::class.java)
            map
        }
    }

    /**
     * 음식 사진 레이블 받아오기
     */
    @RequiresApi(Build.VERSION_CODES.O)
    @WorkerThread
    suspend fun getFoodLabels(
        bitmap: Bitmap,
        onSuccess: (List<String>) -> Unit = {},
        onFailure: () -> Unit = {},
    ) {
        val tempImage = App.context.filesDir.path + File.separator + "temp.png"
        val file = bitmap.saveAsFile(tempImage)
        file?.let {
            getFoodLabels(it, onSuccess, onFailure)
        } ?: onFailure()
    }

    /**
     * 음식 사진 레이블 받아오기
     */
    @RequiresApi(Build.VERSION_CODES.O)
    @WorkerThread
    suspend fun getFoodLabels(
        filePath: String,
        onSuccess: (List<String>) -> Unit = {},
        onFailure: () -> Unit = {},
    ) {
        return getFoodLabels(File(filePath), onSuccess, onFailure)
    }

    /**
     * 음식 사진 레이블 받아오기
     */
    @RequiresApi(Build.VERSION_CODES.O)
    @WorkerThread
    suspend fun getFoodLabels(
        file: File,
        onSuccess: (List<String>) -> Unit = {},
        onFailure: () -> Unit = {},
    ) {
        val fileData = RequestBody.create(
            "image/*".toMediaTypeOrNull(),
            file.readBytes()
        )
        val call = AiApiClient.service.postDetect(fileData)

        call.enqueue(object : Callback<FoodClassificationResponse> {
            override fun onResponse(
                call: Call<FoodClassificationResponse>,
                response: Response<FoodClassificationResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    onSuccess(response.body()!!.let { body ->
                        Log.e("getFoodLabels()", "SUCCESS:\n$body")
                        body.asSequence().map { foodLabels[it.data] }
                            .filterNotNull().toList()
                    })
                } else {
                    Toast.makeText(
                        App.context, "Response was successful, but something went wrong",
                        Toast.LENGTH_SHORT
                    ).show()
                    onFailure()
                }
            }

            override fun onFailure(call: Call<FoodClassificationResponse>, t: Throwable) {
                Log.e("getFoodLabels()", "ERROR:\n$t")
                Toast.makeText(
                    App.context, "서버와 통신에 실패했습니다! 로그를 확인하세요.",
                    Toast.LENGTH_SHORT
                ).show()
                onFailure()
            }
        })
    }
}
