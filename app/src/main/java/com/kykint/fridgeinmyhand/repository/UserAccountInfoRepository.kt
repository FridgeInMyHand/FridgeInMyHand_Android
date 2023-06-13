package com.kykint.fridgeinmyhand.repository

import androidx.annotation.WorkerThread
import com.kykint.fridgeinmyhand.api.FridgeApi
import com.kykint.fridgeinmyhand.data.UserAccountInfo
import com.naver.maps.geometry.LatLng

interface IUserAccountInfoRepository {

    @WorkerThread
    suspend fun fetchUserAccountInfo(uuid: String, onFailure: () -> Unit = {}): UserAccountInfo?

    @WorkerThread
    suspend fun saveMyLocation(latLng: LatLng, onSuccess: () -> Unit, onFailure: () -> Unit)

    @WorkerThread
    suspend fun saveMyKakaoTalkLink(url: String, onSuccess: () -> Unit, onFailure: () -> Unit)
}

class UserAccountInfoRepository : IUserAccountInfoRepository {

    /**
     * 사용자 정보 가져오기
     *
     * @param onFailure 에러가 발생했거나 유저 정보가 없을 시 콜백
     */
    @WorkerThread
    override suspend fun fetchUserAccountInfo(
        uuid: String,
        onFailure: () -> Unit,
    ): UserAccountInfo? {
        return FridgeApi.getUserAccountInfo(
            uuid,
            onFailure = onFailure,
        )?.let { UserAccountInfo(it.lat, it.long, it.url) }
    }

    /**
     * 사용자 위치 정보 저장
     */
    @WorkerThread
    override suspend fun saveMyLocation(
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
    override suspend fun saveMyKakaoTalkLink(
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