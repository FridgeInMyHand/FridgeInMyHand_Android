package com.kykint.fridgeinmyhand.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import com.kykint.fridgeinmyhand.compose.EditUserInfoScreen
import com.kykint.fridgeinmyhand.ui.theme.FridgeInMyHandTheme
import com.kykint.fridgeinmyhand.viewmodel.EditUserInfoViewModel
import com.naver.maps.geometry.LatLng

class EditUserInfoActivity : ComponentActivity() {

    private val viewModel: EditUserInfoViewModel by viewModels { EditUserInfoViewModel.Factory }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            FridgeInMyHandTheme {
                EditUserInfoScreen(
                    viewModel,
                    onLocationChooseClicked = {
                        startActivityForResult(
                            Intent(this, LocationChooseActivity::class.java),
                            REQUEST_GET_LOCATION
                        )
                    },
                )
            }
        }

        viewModel.loadInfos()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_GET_LOCATION && resultCode == RESULT_OK) {
            val lat = data?.getDoubleExtra("lat", -1.0) ?: -1.0
            val lng = data?.getDoubleExtra("lng", -1.0) ?: -1.0
            if (lat != -1.0 && lng != -1.0) {
                viewModel.onEditUserLocationDone(LatLng(lat, lng))
            }
        }
    }

    companion object {
        private const val REQUEST_GET_LOCATION = 10000
    }
}
