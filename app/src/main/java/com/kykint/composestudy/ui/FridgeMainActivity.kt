package com.kykint.composestudy.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.kykint.composestudy.compose.FridgeMainScreen
import com.kykint.composestudy.ui.theme.ComposeStudyTheme
import com.kykint.composestudy.utils.writeImageToSdcard
import com.kykint.composestudy.viewmodel.FridgeMainViewModel

class FridgeMainActivity : ComponentActivity() {
    /*
    private val viewModel by lazy {
        ViewModelProvider(this,
            FridgeMainViewModelFactory(DummyFridgeRepositoryImpl()))
            .get(FridgeMainViewModel::class.java)
    }
    */

    private val viewModel: FridgeMainViewModel by viewModels { FridgeMainViewModel.Factory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeStudyTheme {
                FridgeMainScreen(
                    viewModel = viewModel,
                    onFabClick = {
                        startActivityForResult(
                            Intent(this, AddFoodActivity::class.java),
                            REQUEST_ADD_FOOD
                        )
                    },
                    onBtnClick = {
                        writeImageToSdcard()
                    },
                )
            }
        }

        viewModel.loadFoods()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_ADD_FOOD && resultCode == RESULT_OK) {
            viewModel.loadFoods()
        }
    }

    companion object {
        private const val REQUEST_ADD_FOOD = 10000
    }
}
