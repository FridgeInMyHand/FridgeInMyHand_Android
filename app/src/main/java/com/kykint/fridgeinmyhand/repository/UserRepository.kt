package com.kykint.fridgeinmyhand.repository

import androidx.annotation.WorkerThread
import com.kykint.fridgeinmyhand.api.FridgeApi
import com.kykint.fridgeinmyhand.data.UserLocation

interface IUserRepository {

    @WorkerThread
    suspend fun getNearbyUsers(
        lat: Double, lng: Double, latLimit: Double, lngLimit: Double,
        onSuccess: (List<UserLocation>) -> Unit = {},
        onFailure: () -> Unit = {}
    )
}

class UserRepository : IUserRepository {

    @WorkerThread
    override suspend fun getNearbyUsers(
        lat: Double,
        lng: Double,
        latLimit: Double,
        lngLimit: Double,
        onSuccess: (List<UserLocation>) -> Unit,
        onFailure: () -> Unit
    ) {
        FridgeApi.getNearbyUsers(
            lat, lng, latLimit, lngLimit,
            onSuccess = { userLocations ->
                onSuccess(userLocations.map {
                    UserLocation(it.uuid, it.lat, it.long)
                })
            },
            onFailure = onFailure,
        )
    }
}