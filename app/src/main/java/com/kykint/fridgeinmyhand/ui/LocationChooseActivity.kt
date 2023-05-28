package com.kykint.fridgeinmyhand.ui

import android.content.Intent
import android.graphics.PointF
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.kykint.fridgeinmyhand.ui.theme.FridgeInMyHandTheme
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.LocationTrackingMode
import com.naver.maps.map.compose.MapProperties
import com.naver.maps.map.compose.MapUiSettings
import com.naver.maps.map.compose.Marker
import com.naver.maps.map.compose.MarkerState
import com.naver.maps.map.compose.NaverMap
import com.naver.maps.map.compose.rememberFusedLocationSource

class LocationChooseActivity : ComponentActivity() {

    @OptIn(ExperimentalNaverMapApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val mapProperties by remember {
                mutableStateOf(
                    MapProperties(
                        locationTrackingMode = LocationTrackingMode.NoFollow,
                    )
                )
            }
            val mapUiSettings by remember {
                mutableStateOf(
                    MapUiSettings(
                        isLocationButtonEnabled = true,
                    ),
                )
            }

            var selectedLocation by remember { mutableStateOf<LatLng?>(null) }
            var saveButtonEnabled by remember { mutableStateOf(false) }

            LaunchedEffect(selectedLocation) {
                saveButtonEnabled = selectedLocation != null
            }

            FridgeInMyHandTheme {
                Column() {
                    Box(
                        modifier = Modifier.weight(1f),
                    ) {
                        NaverMap(
                            locationSource = rememberFusedLocationSource(),
                            properties = mapProperties,
                            uiSettings = mapUiSettings,
                            onMapClick = { pointF: PointF, latLng: LatLng ->
                                selectedLocation = latLng
                            }
                        ) {
                            selectedLocation?.let {
                                Marker(
                                    state = MarkerState(position = it),
                                    onClick = { selectedLocation = null; true }
                                )
                            }
                        }
                    }
                    Row {
                        Button(
                            modifier = Modifier.weight(1f),
                            onClick = { finish() },
                        ) {
                            Text("Cancel")
                        }
                        Button(
                            modifier = Modifier.weight(1f),
                            enabled = saveButtonEnabled,
                            onClick = {
                                selectedLocation?.let {
                                    val intent = Intent()
                                    intent.putExtra("lat", it.latitude)
                                    intent.putExtra("lng", it.longitude)
                                    setResult(RESULT_OK, intent)
                                    finish()
                                }
                            },
                        ) {
                            Text("Save")
                        }
                    }
                }
            }
        }
    }
}
