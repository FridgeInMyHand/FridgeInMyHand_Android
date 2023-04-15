package com.kykint.composestudy.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.kykint.composestudy.compose.AddFoodScreen
import com.kykint.composestudy.compose.FridgeMainScreen
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

    private val viewModel: FridgeMainViewModel by viewModels { FridgeMainViewModel.FakeFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FridgeMainScreen(
                viewModel = viewModel,
                onFabClick = {
                    startActivity(
                        Intent(this, AddFoodActivity::class.java)
                    )
                },
                onBtnClick = {
                    writeImageToSdcard()
                },
            )
        }

        viewModel.loadFoods()
    }
}
