package com.kykint.fridgeinmyhand.ui

import android.content.Intent
import android.graphics.PointF
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.holix.android.bottomsheetdialog.compose.BottomSheetDialog
import com.holix.android.bottomsheetdialog.compose.BottomSheetDialogProperties
import com.kykint.fridgeinmyhand.compose.FoodList
import com.kykint.fridgeinmyhand.ui.theme.FridgeInMyHandTheme
import com.kykint.fridgeinmyhand.viewmodel.FoodShareViewModel
import com.kykint.fridgeinmyhand.viewmodel.IFoodShareViewModel.*
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.LocationTrackingMode
import com.naver.maps.map.compose.MapProperties
import com.naver.maps.map.compose.MapUiSettings
import com.naver.maps.map.compose.Marker
import com.naver.maps.map.compose.MarkerState
import com.naver.maps.map.compose.NaverMap
import com.naver.maps.map.compose.rememberCameraPositionState
import com.naver.maps.map.compose.rememberFusedLocationSource

class FoodShareActivity : ComponentActivity() {

    private val viewModel: FoodShareViewModel by viewModels { FoodShareViewModel.Factory }

    @OptIn(ExperimentalNaverMapApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val initLat = 37.5510676
        val initLng = 127.1521761
        val initLatLimit = 0.01
        val initLngLimit = 0.01

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

            val cameraPositionState = rememberCameraPositionState {
                // TODO: Change default position
                position = CameraPosition(LatLng(initLat, initLng), 11.0)
            }

            val uiState by viewModel.uiState.collectAsState()
            val nearbyUserInfoState by viewModel.nearbyUserInfoState.collectAsState()

            val nearbyUsers by viewModel.nearbyUsers.observeAsState()
            val nearbyUserFoods by viewModel.nearbyUserFoods.observeAsState()
            val nearbyUserUrl by viewModel.nearbyUserUrl.observeAsState()

            LaunchedEffect(
                cameraPositionState.position,
                cameraPositionState.coveringBounds,
            ) {
                if (!cameraPositionState.isMoving) {
                    Log.e("CAMERA", "${cameraPositionState.position}")
                    val lat = cameraPositionState.position.target.latitude
                    val lng = cameraPositionState.position.target.longitude
                    val latLimit = cameraPositionState.coveringBounds?.northLatitude?.minus(lat)
                        ?: initLatLimit
                    val lngLimit = cameraPositionState.coveringBounds?.eastLongitude?.minus(lng)
                        ?: initLngLimit

                    Log.e("CAMERA", "$lat\n$lat\n$latLimit\n$lngLimit")

                    viewModel.refreshNearbyUsers(lat, lng, latLimit, lngLimit)
                }
            }

            FridgeInMyHandTheme {
                var showUserFoodList by remember { mutableStateOf(false) }
                if (showUserFoodList) {
                    BottomSheetDialog(
                        onDismissRequest = { showUserFoodList = false },
                        properties = BottomSheetDialogProperties(
                            dismissWithAnimation = true,
                        ),

                        ) {
                        Surface(
                            modifier = Modifier.clip(
                                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                            )
                        ) {
                            if (nearbyUserInfoState == NearbyUserInfoState.Loading) {
                                CircularProgressIndicator()
                            } else {
                                Column() {
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Box(
                                        modifier = Modifier.weight(1f),
                                    ) {
                                        FoodList(
                                            models = nearbyUserFoods ?: emptyList(),
                                            isMyFood = false,
                                        )
                                    }
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp),
                                    ) {
                                        val context = LocalContext.current
                                        ElevatedButton(
                                            onClick = {
                                                nearbyUserUrl.let {
                                                    if (it != null) {
                                                        openChat(it)
                                                    } else {
                                                        Toast.makeText(
                                                            context,
                                                            "오픈채팅 링크를 등록하지 않은 사용자입니다.",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = Color(0xFFFAE100),
                                                contentColor = Color.Black,
                                            ),
                                            shape = RoundedCornerShape(4.dp),
                                            modifier = Modifier.fillMaxWidth(),
                                        ) {
                                            Text("오픈채팅 열기")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Column() {
                    Box(
                        modifier = Modifier.weight(1f),
                    ) {
                        NaverMap(
                            locationSource = rememberFusedLocationSource(),
                            properties = mapProperties,
                            uiSettings = mapUiSettings,
                            onMapClick = { pointF: PointF, latLng: LatLng ->
                                // selectedLocation = latLng
                            },
                            cameraPositionState = cameraPositionState,
                        ) {
                            nearbyUsers?.map { nearbyUser ->
                                Marker(
                                    state = MarkerState(
                                        position = LatLng(nearbyUser.lat, nearbyUser.lng),
                                    ),
                                    width = 36.dp,
                                    height = 48.dp,
                                    onClick = {
                                        viewModel.fetchNearbyUserInfo(nearbyUser.uuid)
                                        showUserFoodList = true
                                        true
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }

        // viewModel.refreshNearbyUsers(lat, lng, latLimit, lngLimit)
    }

    private fun openChat(url: String) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }
}
