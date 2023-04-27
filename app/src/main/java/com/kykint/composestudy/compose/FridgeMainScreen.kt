package com.kykint.composestudy.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    val state by viewModel.state.collectAsState()

    if (state is IFridgeMainViewModel.State.Loading) {
        ServerWaitingDialog()
    }

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

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun FridgeMainScreenPreview() {
    AndroidThreeTen.init(LocalContext.current)
    ComposeStudyTheme {
        FridgeMainScreen(viewModel = DummyFridgeMainViewModel())
    }
}

@Composable
private fun FoodList(models: List<Food>, onItemClick: (Int) -> Unit = {}) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(horizontal = 16.dp),
    ) {
        // TODO: key 추가 후 성능 향상 테스트
        itemsIndexed(models) { index, item ->
            FoodItemCard(
                food = item,
                onClick = {
                    onItemClick.invoke(index)
                },
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun FoodListPreview() {
    AndroidThreeTen.init(LocalContext.current)
    val models = (1..3).map { i -> Food(name = "음식 $i") }
    ComposeStudyTheme {
        FoodList(models = models)
    }
}

@Composable
private fun FoodItemCard(food: Food, onClick: () -> Unit = {}) {
    MyElevatedCard(
        modifier = Modifier
            .clickable(onClick = onClick)
            .fillMaxWidth()
            .wrapContentHeight(),
    ) {
        Row(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
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
                    .width(48.dp)
                    .height(48.dp),
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
                verticalArrangement = Arrangement.Center,
            )
            {
                Text(
                    text = food.name,
                    textAlign = TextAlign.Start,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = food.bestBefore?.let {
                        "${epochSecondsToSimpleDate(it)} 까지"
                    } ?: "유통기한 모름",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = false)
@Composable
private fun FoodItemCardPreview() {
    AndroidThreeTen.init(LocalContext.current)
    val model = Food(name = "김치")
    ComposeStudyTheme {
        FoodItemCard(food = model)
    }
}

@Composable
private fun ServerWaitingDialog() {
    ProgressDialog {
        Text("Fetching food list from server...")
    }
}

@Preview
@Composable
private fun ServerWaitingDialogPreview() {
    ComposeStudyTheme {
        ServerWaitingDialog()
    }
}
