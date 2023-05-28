package com.kykint.fridgeinmyhand.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import com.kykint.fridgeinmyhand.compose.FridgeMainScreen
import com.kykint.fridgeinmyhand.ui.theme.FridgeInMyHandTheme
import com.kykint.fridgeinmyhand.viewmodel.FridgeMainViewModel

class FridgeMainActivity : ComponentActivity() {
    /*
    private val viewModel by lazy {
        ViewModelProvider(this,
            FridgeMainViewModelFactory(DummyFridgeRepositoryImpl()))
            .get(FridgeMainViewModel::class.java)
    }
    */

    private val viewModel: FridgeMainViewModel by viewModels { FridgeMainViewModel.Factory }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FridgeInMyHandTheme {
                FridgeMainScreen(
                    viewModel = viewModel,
                    onFabClick = {
                        startActivityForResult(
                            Intent(this, AddFoodActivity::class.java),
                            REQUEST_ADD_FOOD
                        )
                    },
                    onFoodPropertyChanged = viewModel::updateFoodProperty,
                    onEditFoodClicked = viewModel::editFood,
                    onEditFoodDoneClicked = viewModel::saveEditedFood,
                    onEditFoodCancelClicked = viewModel::cancelEditFood,
                    onDeleteFoodClicked = viewModel::deleteFood,
                    onEditUserInfoClicked = {
                        startActivity(
                            Intent(this, EditUserInfoActivity::class.java),
                        )
                    }
                )
            }
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.refreshFoods()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_ADD_FOOD && resultCode == RESULT_OK) {
            viewModel.refreshFoods()
        }
    }

    companion object {
        private const val REQUEST_ADD_FOOD = 10000
    }
}
