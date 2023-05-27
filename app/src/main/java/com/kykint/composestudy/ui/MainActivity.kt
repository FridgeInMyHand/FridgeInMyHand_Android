package com.kykint.composestudy.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.kykint.composestudy.compose.MainScreen
import com.kykint.composestudy.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    // private val viewModel by lazy {
    //     ViewModelProvider(this, MainViewModelFactory(FakeMyRepositoryImpl()))
    //         .get(MainViewModel::class.java)
    // }

    private val viewModel: MainViewModel by viewModels { MainViewModel.FakeFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen(
                viewModel = viewModel,
                onBtnClick = {
                    startActivity(
                        Intent(this, FridgeMainActivity::class.java)
                    )
                },
            )
        }

        viewModel.loadMyModels()

        // TODO: LiveData를 그냥 observe하는 것과 observeAsState()의 차이?

        // startActivity.observe(LocalContext.current) {
        /*
        viewModel.startFridgeMainActivity.observe(this) {
            startActivity(
                Intent(
                    this,
                    FridgeMainActivity::class.java
                )
            )
        }
        */
    }
}
