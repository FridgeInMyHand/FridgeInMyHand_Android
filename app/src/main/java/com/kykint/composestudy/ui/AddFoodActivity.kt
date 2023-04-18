package com.kykint.composestudy.ui

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.lifecycle.viewModelScope
import com.kykint.composestudy.api.PhotoAnalysisRepository
import com.kykint.composestudy.compose.AddFoodScreen
import com.kykint.composestudy.utils.encode
import com.kykint.composestudy.viewmodel.AddFoodViewModel
import kotlinx.coroutines.launch

class AddFoodActivity : ComponentActivity() {
    private val viewModel: AddFoodViewModel by viewModels()

    // TODO: Remove all RequireApi's
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repo = PhotoAnalysisRepository()
        setContent {
            AddFoodScreen(
                viewModel = viewModel,
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
                onAddDoneClicked = {
                    // TODO: Trigger refresh when adding is done
                    finish()
                }
            )
        }
    }
}
