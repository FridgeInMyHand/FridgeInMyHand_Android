package com.kykint.fridgeinmyhand.repository

import androidx.annotation.WorkerThread
import com.kykint.fridgeinmyhand.api.FridgeApi
import com.kykint.fridgeinmyhand.data.UserAccountInfo
import com.naver.maps.geometry.LatLng

interface IUserAccountInfoRepository {

    @WorkerThread
    suspend fun fetchUserAccountInfo(
        onSuccess: (UserAccountInfo) -> Unit = {},
        onFailure: () -> Unit = {}
    )

    @WorkerThread
    suspend fun saveUserLocation(latLng: LatLng, onSuccess: () -> Unit, onFailure: () -> Unit)

    @WorkerThread
    suspend fun saveUserKakaoTalkLink(url: String, onSuccess: () -> Unit, onFailure: () -> Unit)
}

class UserAccountInfoRepository : IUserAccountInfoRepository {

    /**
     * 사용자 정보 가져오기
     */
    @WorkerThread
    override suspend fun fetchUserAccountInfo(
        onSuccess: (UserAccountInfo) -> Unit,
        onFailure: () -> Unit,
    ) {
        FridgeApi.getUserAccountInfo(
            onSuccess = {
                onSuccess(UserAccountInfo(it.lat, it.long, it.url))
            },
            onFailure = onFailure,
        )
    }

    /**
     * 사용자 위치 정보 저장
     */
    @WorkerThread
    override suspend fun saveUserLocation(
        latLng: LatLng,
        onSuccess: () -> Unit,
        onFailure: () -> Unit,
    ) {
        FridgeApi.saveUserLocation(
            latLng,
            onSuccess = onSuccess,
            onFailure = onFailure,
        )
    }

    /**
     * 사용자 카카오톡 오픈채팅 링크 저장
     */
    @WorkerThread
    override suspend fun saveUserKakaoTalkLink(
        url: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit,
    ) {
        FridgeApi.saveUserKakaoTalkLink(
            url,
            onSuccess = onSuccess,
            onFailure = onFailure,
        )
    }
}