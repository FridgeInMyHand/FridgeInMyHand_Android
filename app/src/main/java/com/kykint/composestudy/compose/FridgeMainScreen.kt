package com.kykint.composestudy.compose

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kykint.composestudy.model.Food
import com.kykint.composestudy.ui.ComposableToast
import com.kykint.composestudy.ui.theme.ComposeStudyTheme
import com.kykint.composestudy.viewmodel.DummyFridgeMainViewModel
import com.kykint.composestudy.viewmodel.IFridgeMainViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FridgeMainScreen(
    viewModel: IFridgeMainViewModel,
    onFabClick: () -> Unit = {},
    onBtnClick: () -> Unit = {},
) {
    ComposeStudyTheme {
        Scaffold(
            topBar = {
                TopAppBar(title = { Text("My Fridge") })
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = onFabClick,
                ) {
                    Icon(Icons.Filled.Add, "Add food")
                }
            },
        ) { contentPadding ->
            Box(
                modifier = Modifier.padding(contentPadding),
            ) {
                Column {
                    // val foods = viewModel.foods.observeAsState().value ?: emptyList()
                    val foods = viewModel.foods
                    val clicked = viewModel.onItemClickEvent.observeAsState().value

                    Box(
                        modifier = Modifier.weight(1f),
                    ) {
                        FoodList(
                            models = foods,
                            onItemClick = viewModel::onItemClick,
                        )
                    }
                    ElevatedButton(
                        onClick = onBtnClick,
                        modifier = Modifier.align(
                            Alignment.CenterHorizontally
                        ),
                    ) {
                        Text("This is a button")
                    }

                    clicked?.let {
                        it.getContentIfNotHandled()?.let {
                            ComposableToast(it.name)
                        }
                    }
                    // Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {Greeting("Android")}
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun FridgeMainScreenPreview() {
    FridgeMainScreen(viewModel = DummyFridgeMainViewModel())
}

@Composable
fun FoodList(models: List<Food>, onItemClick: (Int) -> Unit = {}) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // TODO: key 추가 후 성능 향상 테스트
        itemsIndexed(models) { index, item ->
            FoodListItem(model = item, onClick = {
                onItemClick.invoke(index)
            })
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun FoodListPreview() {
    val models = (1..3).map { i -> Food(name = "$i") }
    FoodList(models = models)
}

@Composable
fun FoodListItem(model: Food, onClick: () -> Unit = {}) {
    Card(
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
            contentColor = MaterialTheme.colorScheme.primary,
        ),
        modifier = Modifier
            .clickable(onClick = onClick)
            // .fillMaxWidth()
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(8.dp)
    ) {
        Text(
            text = model.name,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
        )
    }
}

@Preview(showBackground = true, showSystemUi = false)
@Composable
fun FoodListItemPreview() {
    val model = Food(name = "Title string")
    FoodListItem(model = model)
}