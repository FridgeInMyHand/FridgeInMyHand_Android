package com.kykint.composestudy.compose

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.SyncProblem
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.jakewharton.threetenabp.AndroidThreeTen
import com.kykint.composestudy.data.Food
import com.kykint.composestudy.ui.ComposableToast
import com.kykint.composestudy.ui.theme.ComposeStudyTheme
import com.kykint.composestudy.utils.epochSecondsToSimpleDate
import com.kykint.composestudy.viewmodel.DummyFridgeMainViewModel
import com.kykint.composestudy.viewmodel.IFridgeMainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


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
    AndroidThreeTen.init(LocalContext.current)
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
            FoodListItem(food = item, onClick = {
                onItemClick.invoke(index)
            })
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun FoodListPreview() {
    AndroidThreeTen.init(LocalContext.current)
    val models = (1..3).map { i -> Food(name = "$i") }
    FoodList(models = models)
}

@Composable
fun FoodListItem(food: Food, onClick: () -> Unit = {}) {
    Card(
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
            contentColor = MaterialTheme.colorScheme.primary,
        ),
        modifier = Modifier
            .clickable(onClick = onClick)
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
        ) {
            val context = LocalContext.current
            var model by remember { mutableStateOf<ImageRequest?>(null) }

            LaunchedEffect(context) {
                withContext(Dispatchers.IO) {
                    model = food.getImageModel(context)
                }
            }
            Box(
                modifier = Modifier
                    .width(64.dp)
                    .height(64.dp),
            ) {
                if (model == null) {
                    Image(
                        Icons.Outlined.SyncProblem, "",
                        modifier = Modifier.fillMaxSize(),
                    )
                } else {
                    AsyncImage(
                        model = model,
                        contentDescription = food.name,
                        contentScale = ContentScale.Crop,
                        placeholder = rememberVectorPainter(Icons.Outlined.SyncProblem),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .fillMaxSize()
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                verticalArrangement = Arrangement.Center
            )
            {
                Text(
                    text = food.name,
                    textAlign = TextAlign.Start,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                )
                // Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = food.bestBefore?.let {
                        "${epochSecondsToSimpleDate(it)} 까지"
                    } ?: "유통기한 모름",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = false)
@Composable
fun FoodListItemPreview() {
    AndroidThreeTen.init(LocalContext.current)
    val model = Food(name = "김치")
    FoodListItem(food = model)
}