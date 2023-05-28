package com.kykint.fridgeinmyhand.api

import android.graphics.Bitmap
import android.widget.Toast
import androidx.annotation.WorkerThread
import com.kykint.fridgeinmyhand.App
import com.kykint.fridgeinmyhand.data.Food
import com.kykint.fridgeinmyhand.utils.Prefs
import com.kykint.fridgeinmyhand.utils.saveAsFile
import com.naver.maps.geometry.LatLng
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query
import java.io.File

interface FridgeApiService {
    /**
     * 음식 목록 받아오기
     */
    @GET("/food")
    fun getFood(
        @Query("RequestUUID") requestUUID: String,
        @Query("UserUUID") userUUID: String,
    ): Call<FoodListResponse>

    /**
     * 음식 목록 서버에 저장
     * 요청 형식: application/json
     */
    @POST("/food")
    fun postFood(
        @Body param: PostFoodListModel,
    ): Call<Void>

    /**
     * 음식 레이블 식별
     */
    @Multipart
    @POST("/detect")
    fun postDetect(
        @Part file: MultipartBody.Part,
    ): Call<FoodClassificationResponse>

    /**
     * 사용자 정보 받아오기
     */
    @GET("/user")
    fun getUser(
        @Query("UUID") uuid: String,
    ): Call<UserAccountInfoResponse>

    /**
     * 사용자 위치 등록
     * 요청 형식: application/json
     */
    @POST("/userLocation")
    fun postUserLocation(
        @Body param: PostUserLocationModel,
    ): Call<Void>

    /**
     * 사용자 카카오톡 링크 등록
     * 요청 형식: application/json
     */
    @POST("/userURL")
    fun postUserUrl(
        @Body param: PostUserUrlModel,
    ): Call<Void>

    /**
     * 근처 사용자 정보 받아오기
     */
    @GET("/nearbyUser")
    fun getNearbyUser(
        @Query("UUID") uuid: String,
        @Query("lat") lat: Double,
        @Query("long") long: Double,
        @Query("lat_limit") lat_limit: Double,
        @Query("long_limit") long_limit: Double,
    ): Call<NearbyUserResponse>
}

object FridgeApiClient {
    private const val baseurl = "http://kykint.com:3939"
    private val retrofit = Retrofit.Builder()
        .baseUrl(baseurl)
        .addConverterFactory(GsonConverterFactory.create())
        /*
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
        */
        .build()

    val service: FridgeApiService = retrofit.create(FridgeApiService::class.java)
}

object FridgeApi {
    val foodLabels: Map<String, String> = hashMapOf(
        "01011001" to "쌀밥",
        "01012003" to "보리밥",
        "01012002" to "콩밥",
        "01012001" to "잡곡밥",
        "01013002" to "곤드레밥",
        "01013001" to "감자밥",
        "01014004" to "일반비빔밥",
        "01014003" to "볶음밥",
        "01014002" to "주먹밥",
        "01014001" to "김치볶음밥",
        "01012004" to "돌솥밥",
        "01014005" to "전주비빔밥",
        "01014006" to "삼선볶음밥",
        "01014007" to "새우볶음밥",
        "01014008" to "알밥",
        "01014009" to "산채비빔밥",
        "01014010" to "오므라이스",
        "01014011" to "육회비빔밥",
        "01014012" to "해물볶음밥",
        "01014013" to "열무비빔밥",
        "01015002" to "불고기덮밥",
        "01015003" to "소고기국밥",
        "01015004" to "송이덮밥",
        "01015005" to "오징어덮밥",
        "01015006" to "자장밥",
        "01015007" to "잡채밥",
        "01015008" to "잡탕밥",
        "01015009" to "장어덮밥",
        "01015010" to "제육덮밥",
        "01015011" to "짬뽕밥",
        "01015012" to "순대국밥",
        "01015013" to "카레라이스",
        "01015014" to "전주콩나물국밥",
        "01015015" to "해물덮밥",
        "01015016" to "회덮밥",
        "01015017" to "소머리국밥",
        "01015018" to "돼지국밥",
        "01016001" to "김치김밥",
        "01016002" to "농어초밥",
        "01016003" to "문어초밥",
        "01016004" to "새우초밥",
        "01016005" to "새우튀김롤",
        "01016006" to "샐러드김밥",
        "01016007" to "광어초밥",
        "01016008" to "소고기김밥",
        "01016009" to "갈비삼각김밥",
        "01016010" to "연어롤",
        "01016011" to "연어초밥",
        "01016012" to "유부초밥",
        "01016013" to "장어초밥",
        "01016014" to "참치김밥",
        "01016015" to "참치마요삼각김밥",
        "01016016" to "충무김밥",
        "01016017" to "치즈김밥",
        "01016018" to "캘리포니아롤",
        "01016019" to "한치초밥",
        "02011001" to "간자장",
        "02011002" to "굴짬뽕",
        "02011003" to "기스면",
        "02011004" to "김치라면",
        "02011005" to "김치우동",
        "02011006" to "김치말이국수",
        "02011007" to "닭칼국수",
        "02011008" to "들깨칼국수",
        "02011009" to "떡라면",
        "02011010" to "라면",
        "02011011" to "막국수",
        "02011012" to "메밀국수",
        "02011013" to "물냉면",
        "02011014" to "비빔국수",
        "02011015" to "비빔냉면",
        "02011016" to "삼선우동",
        "02011017" to "삼선자장면",
        "02011018" to "삼선짬뽕",
        "02011019" to "수제비",
        "02011020" to "쌀국수",
        "02011021" to "열무김치국수",
        "02011023" to "오일소스스파게티",
        "02011024" to "일식우동",
        "02011025" to "볶음우동",
        "02011027" to "자장면",
        "02011028" to "잔치국수",
        "02011029" to "짬뽕",
        "02011030" to "짬뽕라면",
        "02011031" to "쫄면",
        "02011032" to "치즈라면",
        "02011033" to "콩국수",
        "02011034" to "크림소스스파게티",
        "02011035" to "토마토소스스파게티",
        "02011036" to "해물칼국수",
        "02011037" to "회냉면",
        "02011038" to "떡국",
        "02011039" to "떡만둣국",
        "02012001" to "고기만두",
        "02012002" to "군만두",
        "02012003" to "김치만두",
        "02012004" to "물만두",
        "02012005" to "만둣국",
        "03011001" to "게살죽",
        "03011002" to "깨죽",
        "03011003" to "닭죽",
        "03011004" to "소고기버섯죽",
        "03011005" to "어죽",
        "03011006" to "잣죽",
        "03011007" to "전복죽",
        "03011008" to "참치죽",
        "03011009" to "채소죽",
        "03011010" to "팥죽",
        "03011011" to "호박죽",
        "03012001" to "콘스프",
        "03012002" to "토마토스프",
        "04011001" to "굴국",
        "04011002" to "김치국",
        "04011003" to "달걀국",
        "04011004" to "감자국",
        "04011005" to "미역국",
        "04011006" to "바지락조개국",
        "04011007" to "소고기무국",
        "04011008" to "소고기미역국",
        "04011009" to "소머리국밥",
        "04011010" to "순대국",
        "04011011" to "어묵국",
        "04011012" to "오징어국",
        "04011013" to "토란국",
        "04011014" to "탕국",
        "04011015" to "홍합미역국",
        "04011016" to "황태해장국",
        "04012001" to "근대된장국",
        "04012002" to "미소된장국",
        "04012003" to "배추된장국",
        "04012004" to "뼈다귀해장국",
        "04012005" to "선지(해장)국",
        "04012006" to "콩나물국",
        "04012007" to "시금치된장국",
        "04012008" to "시래기된장국",
        "04012009" to "쑥된장국",
        "04012010" to "아욱된장국",
        "04012011" to "우거지된장국",
        "04012012" to "우거지해장국",
        "04012013" to "우렁된장국",
        "04013002" to "갈비탕",
        "04013003" to "감자탕",
        "04013004" to "곰탕",
        "04013005" to "매운탕",
        "04013006" to "꼬리곰탕",
        "04013007" to "꽃게탕",
        "04013008" to "낙지탕",
        "04013009" to "내장탕",
        "04013010" to "닭곰탕",
        "04013011" to "닭볶음탕",
        "04013012" to "지리탕",
        "04013013" to "도가니탕",
        "04013014" to "삼계탕",
        "04013015" to "설렁탕",
        "04013017" to "알탕",
        "04013018" to "연포탕",
        "04013019" to "오리탕",
        "04013020" to "추어탕",
        "04013021" to "해물탕",
        "04013022" to "닭개장",
        "04013023" to "육개장",
        "04014001" to "미역오이냉국",
        "04015001" to "고등어찌개",
        "04015002" to "꽁치찌개 ",
        "04015003" to "동태찌개",
        "04016001" to "부대찌개",
        "04017001" to "된장찌개",
        "04017002" to "청국장찌개",
        "04018001" to "두부전골",
        "04018002" to "곱창전골",
        "04018003" to "소고기전골",
        "04018004" to "국수전골",
        "04019001" to "돼지고기김치찌개",
        "04019002" to "버섯찌개",
        "04019003" to "참치김치찌개",
        "04019004" to "순두부찌개",
        "04019005" to "콩비지찌개",
        "04019006" to "햄김치찌개",
        "04019007" to "호박찌개",
        "05011001" to "대구찜",
        "05011002" to "도미찜",
        "05011004" to "문어숙회",
        "15011020" to "test4",
        "05011008" to "아귀찜",
        "15011019" to "test3",
        "05011010" to "조기찜",
        "05011011" to "참꼬막",
        "05011012" to "해물찜",
        "16011007" to "test2",
        "06012001" to "닭갈비",
        "06012002" to "닭꼬치",
        "06012003" to "돼지갈비",
        "06012004" to "떡갈비",
        "06012005" to "불고기",
        "06012006" to "소곱창구이",
        "06012007" to "소양념갈비구이",
        "06012008" to "소불고기",
        "06012009" to "양념왕갈비",
        "06012010" to "햄버거스테이크",
        "06012011" to "훈제오리",
        "06012012" to "치킨데리야끼",
        "06012013" to "치킨윙",
        "06013001" to "더덕구이",
        "06013002" to "양배추구이",
        "06013003" to "두부구이",
        "07011001" to "가자미전",
        "07011002" to "굴전",
        "07011003" to "동태전",
        "07011004" to "해물파전",
        "07012001" to "동그랑땡",
        "07012002" to "햄부침",
        "07012003" to "육전",
        "07013001" to "감자전",
        "07013002" to "고추전",
        "07013003" to "김치전",
        "07013004" to "깻잎전",
        "07013005" to "녹두빈대떡",
        "07013006" to "미나리전",
        "07013007" to "배추전",
        "07013008" to "버섯전",
        "07013009" to "부추전",
        "07013010" to "야채전",
        "07013011" to "파전",
        "07013012" to "호박부침개",
        "07013013" to "호박전",
        "07014001" to "달걀말이",
        "07014002" to "두부부침",
        "07014003" to "두부전",
        "08011001" to "건새우볶음",
        "08011002" to "낙지볶음",
        "08011003" to "멸치볶음",
        "08011004" to "어묵볶음",
        "08011005" to "오징어볶음",
        "08011006" to "오징어채볶음",
        "08011007" to "주꾸미볶음",
        "08011008" to "해물볶음",
        "08012001" to "감자볶음",
        "08012002" to "김치볶음",
        "08012003" to "깻잎나물볶음",
        "08012004" to "느타리버섯볶음",
        "08012005" to "두부김치",
        "08012006" to "머위나물볶음",
        "08012007" to "양송이버섯볶음",
        "08012008" to "표고버섯볶음",
        "08012009" to "고추잡채",
        "08012010" to "호박볶음",
        "08013001" to "돼지고기볶음",
        "08013002" to "돼지껍데기볶음",
        "08013003" to "소세지볶음",
        "08013004" to "순대볶음",
        "08013005" to "오리불고기",
        "08013006" to "오삼불고기",
        "08014001" to "떡볶이",
        "08014002" to "라볶이",
        "08014003" to "마파두부",
        "09011001" to "가자미조림",
        "09011002" to "갈치조림",
        "09011003" to "고등어조림",
        "09011004" to "꽁치조림",
        "09011005" to "동태조림",
        "09011006" to "북어조림",
        "09011007" to "조기조림",
        "09011008" to "코다리조림",
        "09012001" to "달걀장조림",
        "09012002" to "메추리알장조림",
        "09013001" to "돼지고기메추리알장조림",
        "09013002" to "소고기메추리알장조림",
        "09014001" to "고추조림",
        "09014002" to "감자조림",
        "09014003" to "우엉조림",
        "09014004" to "알감자조림",
        "09015001" to "(검은)콩조림",
        "09015002" to "콩조림",
        "09015003" to "두부고추장조림",
        "10011001" to "미꾸라지튀김",
        "10011002" to "새우튀김",
        "10011003" to "생선가스",
        "10011004" to "쥐포튀김",
        "10011005" to "오징어튀김",
        "10012001" to "닭강정",
        "10012002" to "닭튀김",
        "10012003" to "돈가스",
        "10012004" to "모래집튀김",
        "10012005" to "양념치킨",
        "10012006" to "치즈돈가스",
        "10012007" to "치킨가스",
        "10012008" to "탕수육",
        "10012009" to "깐풍기",
        "10014001" to "감자튀김",
        "10014002" to "고구마맛탕",
        "10014003" to "고구마튀김",
        "10014004" to "고추튀김",
        "10014005" to "김말이튀김",
        "10014006" to "채소튀김",
        "11011001" to "노각무침",
        "11011002" to "단무지무침",
        "11011003" to "달래나물무침",
        "11011004" to "더덕무침",
        "11011005" to "도라지생채",
        "11011006" to "도토리묵",
        "11011007" to "마늘쫑무침",
        "11011008" to "무생채",
        "11011009" to "무말랭이",
        "11011010" to "오이생채",
        "11011011" to "파무침",
        "11012001" to "상추겉절이",
        "11012002" to "쑥갓나물무침",
        "11012003" to "청포묵무침",
        "11012004" to "해파리냉채",
        "11013001" to "가지나물",
        "11013002" to "고사리나물",
        "11013003" to "도라지나물",
        "11013004" to "무나물",
        "11013005" to "미나리나물",
        "11013006" to "숙주나물",
        "11013007" to "시금치나물",
        "11013009" to "취나물",
        "11013010" to "콩나물",
        "11013011" to "고구마줄기나물",
        "11013012" to "우거지나물무침",
        "11014001" to "골뱅이무침",
        "11014002" to "김무침",
        "11014003" to "미역초무침",
        "11014004" to "북어채무침",
        "11014005" to "회무침",
        "11014006" to "쥐치채",
        "11014007" to "파래무침",
        "11014008" to "홍어무침",
        "11015001" to "잡채",
        "11015002" to "탕평채",
        "12011001" to "갓김치",
        "12011002" to "고들빼기",
        "12011003" to "깍두기",
        "12011004" to "깻잎김치",
        "12011005" to "나박김치",
        "12011006" to "동치미",
        "12011007" to "배추겉절이",
        "12011008" to "배추김치",
        "12011009" to "백김치",
        "12011010" to "부추김치",
        "12011011" to "열무김치",
        "12011012" to "열무얼갈이김치",
        "12011013" to "오이소박이",
        "12011014" to "총각김치",
        "12011015" to "파김치",
        "13011001" to "간장게장",
        "13011002" to "마늘쫑장아찌",
        "13011003" to "고추장아찌",
        "13011004" to "깻잎장아찌",
        "13011005" to "마늘장아찌",
        "13011006" to "무장아찌",
        "13011007" to "양념게장",
        "13011008" to "양파장아찌",
        "13011009" to "오이지",
        "13011010" to "무피클",
        "13011011" to "오이피클",
        "13011012" to "단무지",
        "13012001" to "오징어젓갈",
        "13012002" to "명란젓",
        "14011001" to "생연어",
        "14011002" to "생선물회",
        "15011018" to "test1",
        "14011004" to "광어회",
        "14011005" to "훈제연어",
        "14012001" to "육회",
        "14012002" to "육사시미",
        "15011002" to "경단",
        "15011001" to "가래떡",
        "15011003" to "꿀떡",
        "15011004" to "시루떡",
        "15011005" to "메밀전병",
        "15011006" to "찰떡",
        "15011007" to "무지개떡",
        "15011008" to "백설기",
        "15011009" to "송편",
        "15011010" to "수수부꾸미",
        "15011011" to "수수팥떡",
        "15011012" to "쑥떡",
        "15011013" to "약식",
        "15011014" to "인절미",
        "15011015" to "절편",
        "15011016" to "증편",
        "15011017" to "찹쌀떡",
        "16011001" to "매작과",
        "16011002" to "다식",
        "16011003" to "약과",
        "16011004" to "유과",
        "16011005" to "산자",
        "16011006" to "깨강정",
        "09016001" to "땅콩조림",
        "01016020" to "일반김밥",
        "01015019" to "하이라이스",
        "02011040" to "짜장라면",
        "11014009" to "골뱅이국수무침",
        "11014010" to "오징어무침",
        "04013024" to "뼈해장국",
        "04019008" to "고추장찌개",
        "05012001" to "소갈비찜",
        "05012002" to "돼지갈비찜",
        "05012003" to "돼지고기수육",
        "05012004" to "찜닭",
        "05012005" to "족발",
        "05013001" to "달걀찜",
        "06014001" to "삼치구이",
    )

    /**
     * 내 음식 목록 받아오기
     */
    @WorkerThread
    suspend fun getMyFoodList(
        onSuccess: (FoodListResponse) -> Unit = {},
        onFailure: () -> Unit = {},
    ) = getFoodList(Prefs.uuid, onSuccess, onFailure)

    /**
     * 음식 목록 받아오기
     */
    @WorkerThread
    suspend fun getFoodList(
        requestUUID: String,
        onSuccess: (FoodListResponse) -> Unit = {},
        onFailure: () -> Unit = {},
    ) {
        val call = FridgeApiClient.service.getFood(requestUUID, Prefs.uuid)

        call.enqueue(object : Callback<FoodListResponse> {
            override fun onResponse(
                call: Call<FoodListResponse>,
                response: Response<FoodListResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    onSuccess(response.body()!!)
                } else {
                    onFailure()
                }
            }

            override fun onFailure(call: Call<FoodListResponse>, t: Throwable) {
                onFailure()
            }
        })
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
        val call = FridgeApiClient.service.postFood(
            PostFoodListModel(Prefs.uuid).fromFoods(foods)
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
     * 음식 사진 레이블 받아오기
     */
    @WorkerThread
    suspend fun getFoodLabels(
        bitmap: Bitmap,
        onSuccess: (List<String>) -> Unit = {},
        onFailure: () -> Unit = {},
    ) {
        val tempImage = App.context.filesDir.path + File.separator + "temp.png"
        val file = bitmap.saveAsFile(tempImage)
        file?.let {
            getFoodLabels(tempImage, onSuccess, onFailure)
        } ?: onFailure()
    }

    /**
     * 음식 사진 레이블 받아오기
     */
    @WorkerThread
    suspend fun getFoodLabels(
        filePath: String,
        onSuccess: (List<String>) -> Unit = {},
        onFailure: () -> Unit = {},
    ) {
        val call = FridgeApiClient.service.postDetect(
            getImageBody(
                name = "image",
                file = File(filePath),
            )
        )

        call.enqueue(object : Callback<FoodClassificationResponse> {
            override fun onResponse(
                call: Call<FoodClassificationResponse>,
                response: Response<FoodClassificationResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    onSuccess(response.body()!!
                        .asSequence().map { foodLabels[it.data] }
                        .filterNotNull().toList())
                } else {
                    Toast.makeText(
                        App.context, "Response was successful, but something went wrong",
                        Toast.LENGTH_SHORT
                    ).show()
                    onFailure()
                }
            }

            override fun onFailure(call: Call<FoodClassificationResponse>, t: Throwable) {
                Toast.makeText(
                    App.context, "Couldn't even establish connection!",
                    Toast.LENGTH_SHORT
                ).show()
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
        onSuccess: (UserAccountInfoResponse) -> Unit = {},
        onFailure: () -> Unit = {},
    ) {
        val call = FridgeApiClient.service.getUser(uuid)

        call.enqueue(object : Callback<UserAccountInfoResponse> {
            override fun onResponse(
                call: Call<UserAccountInfoResponse>,
                response: Response<UserAccountInfoResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    onSuccess(response.body()!!)
                } else {
                    onFailure()
                }
            }

            override fun onFailure(call: Call<UserAccountInfoResponse>, t: Throwable) {
                onFailure()
            }
        })
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
            PostUserLocationModel(Prefs.uuid, latLng.latitude, latLng.longitude)
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
            PostUserUrlModel(Prefs.uuid, url)
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
            Prefs.uuid, lat, long, lat_limit, long_limit
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

    private fun getImageBody(name: String, file: File): MultipartBody.Part {
        return MultipartBody.Part.createFormData(
            name = name,
            filename = file.name,
            body = file.asRequestBody("image/*".toMediaType())
        )
    }
}
