package com.kykint.fridgeinmyhand.compose

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.SyncProblem
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.jakewharton.threetenabp.AndroidThreeTen
import com.kykint.fridgeinmyhand.data.Food
import com.kykint.fridgeinmyhand.ui.theme.FridgeInMyHandTheme
import com.kykint.fridgeinmyhand.utils.epochSecondsToSimpleDate
import com.kykint.fridgeinmyhand.viewmodel.DummyFridgeMainViewModel
import com.kykint.fridgeinmyhand.viewmodel.IFridgeMainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FridgeMainScreen(
    viewModel: IFridgeMainViewModel,
    onFabClick: () -> Unit = {},
    onItemClick: (Int) -> Unit = {},
    onFoodPropertyChanged: (Int, String?, Long?, String?, Boolean?) -> Unit =
        { _, _, _, _, _ -> },
    onEditFoodClicked: (Int) -> Unit = {},
    onEditFoodDoneClicked: (Int, Food) -> Unit = { _, _ -> },
    onEditFoodCancelClicked: () -> Unit = {},
    onDeleteFoodClicked: (Int) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    val editingState by viewModel.editingState.collectAsState()

    if (uiState is IFridgeMainViewModel.UiState.Loading) {
        ServerWaitingDialog()
    }

    if (editingState is IFridgeMainViewModel.EditingState.Editing) {
        viewModel.foods.value?.let {
            EditFoodDialog(
                it,
                (editingState as IFridgeMainViewModel.EditingState.Editing).foodIndex,
                onEditFoodDoneClicked,
                onEditFoodCancelClicked,
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("음식 목록") })
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onFabClick,
            ) {
                Icon(Icons.Filled.Add, "음식 추가")
            }
        },
    ) { contentPadding ->
        Box(
            modifier = Modifier.padding(contentPadding),
        ) {
            Column {
                // val foods = viewModel.foods.observeAsState().value ?: emptyList()
                val foods = viewModel.foods.observeAsState()
                val clicked = viewModel.onItemClickEvent.observeAsState().value

                Box(
                    modifier = Modifier.weight(1f),
                ) {
                    FoodList(
                        models = foods.value ?: emptyList(),
                        onItemClick = onItemClick,
                        onFoodPropertyChanged = onFoodPropertyChanged,
                        onEditFoodClicked = onEditFoodClicked,
                        onDeleteFoodClicked = onDeleteFoodClicked,
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

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun FridgeMainScreenPreview() {
    AndroidThreeTen.init(LocalContext.current)
    FridgeInMyHandTheme {
        FridgeMainScreen(viewModel = DummyFridgeMainViewModel())
    }
}

@Composable
private fun FoodList(
    models: List<Food>,
    onItemClick: (Int) -> Unit = {},
    onFoodPropertyChanged: (Int, String?, Long?, String?, Boolean?) -> Unit =
        { _, _, _, _, _ -> },
    onEditFoodClicked: (Int) -> Unit = {},
    onDeleteFoodClicked: (Int) -> Unit = {},
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(horizontal = 16.dp),
    ) {
        // TODO: key 추가 후 성능 향상 테스트
        itemsIndexed(
            items = models,
            key = { index, item -> index },
        ) { index, item ->
            FoodItemCard(
                food = item,
                onClick = {
                    onItemClick(index)
                },
                onFoodPropertyChanged = {
                    onFoodPropertyChanged(
                        index, null, null, null, it
                    )
                },
                onEditFoodClicked = {
                    onEditFoodClicked(index)
                },
                onDeleteFoodClicked = {
                    onDeleteFoodClicked(index)
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
    FridgeInMyHandTheme {
        FoodList(models = models)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FoodItemCard(
    food: Food,
    isMyFood: Boolean = true,
    onClick: () -> Unit = {},
    onFoodPropertyChanged: (Boolean) -> Unit = {},
    onEditFoodClicked: () -> Unit = {},
    onDeleteFoodClicked: () -> Unit = {},
) {
    MyElevatedCard(
        modifier = Modifier
            .clickable(onClick = onClick)
            .fillMaxWidth()
            .wrapContentHeight(),
    ) {
        // 내 음식이어서 삭제, 수정 등 부분이 보여야 하는지 여부
        val isEditorVisible by remember { mutableStateOf(isMyFood) }
        var foodPublicChecked by remember {
            mutableStateOf(food.isPublic)
        }.apply { value = food.isPublic }

        Row(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            val context = LocalContext.current
            var model by remember { mutableStateOf<ImageRequest?>(null) }

            LaunchedEffect(context, food.name) {
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
                modifier = Modifier.weight(1f),
            ) {
                /**
                 * 음식 이름 + 수량
                 */
                Text(
                    text = "${food.name} ${food.amount ?: ""}",
                    textAlign = TextAlign.Start,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(4.dp))

                /**
                 * 유통기한
                 */
                Text(
                    text = food.bestBefore?.let {
                        "${epochSecondsToSimpleDate(it)} 까지"
                    } ?: "유통기한 모름",
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            /**
             * 내 음식일 때만 표시되는 부분
             */
            if (isEditorVisible) {
                /**
                 * 공유 여부 체크박스
                 */
                CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
                    Text(
                        "공유",
                        style = MaterialTheme.typography.labelSmall,
                    )
                    Checkbox(
                        checked = foodPublicChecked,
                        onCheckedChange = onFoodPropertyChanged,
                        modifier = Modifier.scale(0.7f),
                    )
                }

                /**
                 * 편집 버튼
                 */
                IconButton(
                    onClick = onEditFoodClicked,
                    modifier = Modifier.size(24.dp),
                ) {
                    Icon(Icons.Filled.Edit, "", modifier = Modifier.size(16.dp))
                }

                /**
                 * 삭제 버튼
                 */
                IconButton(
                    onClick = onDeleteFoodClicked,
                    modifier = Modifier.size(24.dp),
                ) {
                    Icon(Icons.Filled.Close, "", modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = false)
@Composable
private fun FoodItemCardPreview() {
    AndroidThreeTen.init(LocalContext.current)
    val model = Food(name = "김치")
    FridgeInMyHandTheme {
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
    FridgeInMyHandTheme {
        ServerWaitingDialog()
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun EditFoodDialog(
    items: List<Food>,
    foodEditIndex: Int,
    onEditDone: (Int, Food) -> Unit = { _, _ -> },
    onEditCanceled: () -> Unit = {},
) {
    val food = items[foodEditIndex].copy()
    Dialog(
        onDismissRequest = onEditCanceled,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true),
    ) {
        MyElevatedCard {
            Box(modifier = Modifier.padding(8.dp)) {
                Column {
                    EditableFoodItem(
                        item = food,
                        onFoodNameChanged = { food.name = it },
                        onFoodBestBeforeChanged = { food.bestBefore = it },
                        onFoodAmountChanged = { food.amount = it },
                        onFoodPublicChanged = { food.isPublic = it },
                    )
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        ElevatedButton(
                            onClick = onEditCanceled,
                            elevation = null,
                            contentPadding = PaddingValues(8.dp),
                        ) {
                            Text("Cancel")
                        }
                        ElevatedButton(
                            onClick = { onEditDone(foodEditIndex, food) },
                            elevation = null,
                            contentPadding = PaddingValues(8.dp),
                        ) {
                            Text("Save")
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
private fun EditFoodDialogPreview() {
    val food = Food(
        name = "김치",
        bestBefore = 1000000000L,
        amount = "100g",
        isPublic = true
    )
    EditFoodDialog(items = listOf(food), foodEditIndex = 0)
}
