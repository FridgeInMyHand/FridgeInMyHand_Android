package com.kykint.fridgeinmyhand.api

import android.content.SharedPreferences
import android.util.Log
import androidx.annotation.WorkerThread
import com.kykint.fridgeinmyhand.data.Food
import com.kykint.fridgeinmyhand.utils.Prefs
import com.naver.maps.geometry.LatLng
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.PUT

interface FridgeApiService {
    /**
     * 음식 목록 받아오기
     * 내용 형식: application/json
     */
    @PUT("/food")
    suspend fun getFood(
        @Body param: GetFoodRequestModel
    ): Response<FoodListResponse>

    /**
     * 음식 목록 서버에 저장
     * 내용 형식: application/json
     */
    @POST("/food")
    fun postFood(
        @Body param: PostFoodRequestModel,
    ): Call<Void>

    /**
     * 사용자 정보 받아오기
     * 내용 형식: application/json
     */
    @PUT("/user")
    suspend fun getUser(
        @Body param: GetUserRequestModel,
    ): Response<UserAccountInfoResponse>

    /**
     * 사용자 위치 등록
     * 내용 형식: application/json
     */
    @POST("/userLocation")
    fun postUserLocation(
        @Body param: PostUserLocationRequestModel,
    ): Call<Void>

    /**
     * 사용자 카카오톡 링크 등록
     * 내용 형식: application/json
     */
    @POST("/userURL")
    fun postUserUrl(
        @Body param: PostUserUrlRequestModel,
    ): Call<Void>

    /**
     * 근처 사용자 정보 받아오기
     * 내용 형식: application/json
     */
    @PUT("/nearbyUser")
    fun getNearbyUser(
        @Body param: GetNearbyUserRequestModel,
    ): Call<NearbyUserResponse>
}

object FridgeApiClient {
    private var baseUrl = Prefs.serverApiAddress
    private val builder = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())

    lateinit var service: FridgeApiService

    private val prefChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            if (key == Prefs.Key.serverApiAddress) {
                baseUrl = Prefs.serverApiAddress
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

object FridgeApi {
    /**
     * 내 음식 목록 받아오기
     */
    @WorkerThread
    suspend fun getMyFoodList(
        onFailure: () -> Unit = {},
    ) = getFoodList(Prefs.uuid, onFailure)

    /**
     * 음식 목록 받아오기
     */
    @WorkerThread
    suspend fun getFoodList(
        requestUUID: String,
        onFailure: () -> Unit = {},
    ): FoodListResponse? {
        return try {
            val request = GetFoodRequestModel(requestUUID, Prefs.uuid)
            val response = FridgeApiClient.service.getFood(request)

            Log.e("getFoodList()", "$request\n$response\n${response.body()}")
            response.body() ?: run {
                onFailure()
                null
            }

        } catch (e: Exception) {
            Log.e(
                "getFoodList()",
                "Exception: ${e.message}\nStack trace: ${e.stackTrace}"
            )
            onFailure()
            null
        }
    }

    /**
     * 내 음식 목록 서버에 저장
     */
    @WorkerThread
    suspend fun saveFoodList(
        foods: List<Food>,
        onSuccess: () -> Unit = {},
        onFailure: () -> Unit = {},
    ) {
        val request = PostFoodRequestModel(Prefs.uuid).fromFoods(foods)
        Log.e("saveFoodList()", "Saving foods: $request")

        val call = FridgeApiClient.service.postFood(request)

        call.enqueue(object : Callback<Void> {
            override fun onResponse(
                call: Call<Void>,
                response: Response<Void>
            ) {
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    Log.e("saveFoodList()", "Got response but error:\n$response")
                    onFailure()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("saveFoodList()", "ERROR:\n$t")
                onFailure()
            }
        })
    }

    /**
     * 사용자 목록 받아오기
     */
    @WorkerThread
    suspend fun getUserAccountInfo(
        uuid: String,
        onFailure: () -> Unit = {},
    ): UserAccountInfoResponse? {
        return try {
            val request = GetUserRequestModel(uuid)
            val response = FridgeApiClient.service.getUser(request)

            Log.e("getUserAccountInfo()", "$request\n$response\n${response.body()}")
            response.body() ?: run {
                onFailure()
                null
            }
        } catch (e: Exception) {
            Log.e(
                "getUserAccountInfo()",
                "Exception: ${e.message}\nStack trace: ${e.stackTrace}"
            )
            onFailure()
            null
        }
    }

    /**
     * 사용자 위치 정보 저장
     */
    @WorkerThread
    suspend fun saveUserLocation(
        latLng: LatLng,
        onSuccess: () -> Unit = {},
        onFailure: () -> Unit = {},
    ) {
        val call = FridgeApiClient.service.postUserLocation(
            PostUserLocationRequestModel(Prefs.uuid, latLng.latitude, latLng.longitude)
        )

        call.enqueue(object : Callback<Void> {
            override fun onResponse(
                call: Call<Void>,
                response: Response<Void>
            ) {
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    onFailure()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                onFailure()
            }
        })
    }

    /**
     * 사용자 카카오톡 링크 저장
     */
    @WorkerThread
    suspend fun saveUserKakaoTalkLink(
        url: String,
        onSuccess: () -> Unit = {},
        onFailure: () -> Unit = {},
    ) {
        val call = FridgeApiClient.service.postUserUrl(
            PostUserUrlRequestModel(Prefs.uuid, url)
        )

        call.enqueue(object : Callback<Void> {
            override fun onResponse(
                call: Call<Void>,
                response: Response<Void>
            ) {
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    onFailure()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                onFailure()
            }
        })
    }

    /**
     * 근처 사용자 정보 받아오기
     */
    @WorkerThread
    suspend fun getNearbyUsers(
        lat: Double, long: Double, lat_limit: Double, long_limit: Double,
        onSuccess: (NearbyUserResponse) -> Unit = {},
        onFailure: () -> Unit = {},
    ) {
        val call = FridgeApiClient.service.getNearbyUser(
            GetNearbyUserRequestModel(Prefs.uuid, lat, long, lat_limit, long_limit)
        )

        call.enqueue(object : Callback<NearbyUserResponse> {
            override fun onResponse(
                call: Call<NearbyUserResponse>,
                response: Response<NearbyUserResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    onSuccess(response.body()!!)
                } else {
                    onFailure()
                }
            }

            override fun onFailure(call: Call<NearbyUserResponse>, t: Throwable) {
                onFailure()
            }
        })
    }
}
