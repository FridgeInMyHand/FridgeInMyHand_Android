package com.kykint.composestudy.ui

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.kykint.composestudy.App
import com.kykint.composestudy.BuildConfig
import com.kykint.composestudy.compose.AddFoodScreen
import com.kykint.composestudy.ui.theme.ComposeStudyTheme
import com.kykint.composestudy.viewmodel.AddFoodViewModel
import java.io.File

class AddFoodActivity : ComponentActivity() {
    private val viewModel: AddFoodViewModel by viewModels { AddFoodViewModel.Factory }

    // TODO: Remove all RequireApi's
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ComposeStudyTheme {
                AddFoodScreen(
                    viewModel = viewModel,
                    /*
                        onSendPhotoTestClicked = {
                            viewModel.viewModelScope.launch {
                                repo.getObjectInfos(
                                    this@AddFoodActivity,
                                    onSuccess = {
                                        if (it != null) {
                                            Toast.makeText(
                                                this@AddFoodActivity,
                                                it.names.encode(),
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        } else {
                                            Log.e("AddFoodActivity", "Great, we got null")
                                        }
                                    },
                                )
                            }
                        },
                    */
                    onAddDoneClicked = {
                        // TODO: Trigger refresh when adding is done
                        setResult(RESULT_OK)
                        finish()
                    },
                    onFabClick = {
                        // Check camera permission
                        if (checkSelfPermission(Manifest.permission.CAMERA) ==
                            PackageManager.PERMISSION_GRANTED &&
                            checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                            PackageManager.PERMISSION_GRANTED
                        ) {
                            launchCamera()
                        } else {
                            ActivityCompat.requestPermissions(
                                this,
                                arrayOf(
                                    Manifest.permission.CAMERA,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                                ),
                                PERM_REQ_CODE
                            )
                        }
                    },
                    onItemRemoveClicked = viewModel::removeItem,
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERM_REQ_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            launchCamera()
        } else {
            Toast.makeText(this, "사진 촬영을 위해서 카메라 권한을 허용해 주세요!", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CAMERA) {
            if (resultCode == RESULT_OK) {
                viewModel.onPictureTaken(tempPicPath)
            }
        }
    }

    private fun launchCamera() {
        // https://developer.android.com/training/camera/camera-intents
        // https://developer.android.com/reference/android/provider/MediaStore#ACTION_IMAGE_CAPTURE
        // https://parkho79.tistory.com/179
        // https://lakue.tistory.com/33
        val tempPicUri = File(tempPicPath).let {
            FileProvider.getUriForFile(
                this, FileUriProvider, it
            )
        }
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, tempPicUri)
        }
        try {
            startActivityForResult(takePictureIntent, REQUEST_CAMERA)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "No camera app available!", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val PERM_REQ_CODE = 100

        private const val REQUEST_CAMERA = 10000

        private const val FileUriProvider = BuildConfig.APPLICATION_ID

        private val tempPicPath =
            App.context.filesDir.absolutePath + File.separator + "temp_pic.jpg"
    }
}
