package com.kykint.composestudy.compose

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kykint.composestudy.model.Food
import com.kykint.composestudy.ui.theme.ComposeStudyTheme
import com.kykint.composestudy.viewmodel.DummyAddFoodViewModel
import com.kykint.composestudy.viewmodel.IAddFoodViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFoodScreen(
    viewModel: IAddFoodViewModel,
    onFabClick: () -> Unit = {},
    onSendPhotoTestClicked: () -> Unit = {},
) {
    ComposeStudyTheme {
        Scaffold(
            topBar = {
                TopAppBar(title = { Text("Add Food") })
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = onFabClick,
                ) {
                    Icon(Icons.Filled.PhotoCamera, "Take a picture")
                }
            },
        ) { contentPadding ->
            Box(
                modifier = Modifier.padding(contentPadding),
            ) {
                EditableFoodItemList(
                    items = viewModel.items,
                    onAddFoodItemClicked = viewModel::onAddFoodItemClicked,
                    onSendPhotoTestClicked = onSendPhotoTestClicked,
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AddFoodScreenPreview() {
    AddFoodScreen(viewModel = DummyAddFoodViewModel())
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun EditableFoodItemListPreview() {
    val items = (1..3).map { i -> Food(name = "$i") }
    EditableFoodItemList(items = items)
}

@Composable
fun EditableFoodItemList(
    items: List<Food>,
    onItemClick: (Int) -> Unit = {},
    onAddFoodItemClicked: () -> Unit = {},
    onSendPhotoTestClicked: () -> Unit = {},
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // TODO: key 추가 후 성능 향상 테스트
        itemsIndexed(items) { index, item ->
            EditableFoodItem(
                item = item,
                onNameChanged = {
                    Log.e("AddFoodScreen", "========== $it =========")
                }
            )
        }

        item {
            ElevatedButton(
                onClick = onAddFoodItemClicked,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(Icons.Filled.Add, "")
            }
        }
        // TODO: For testing only. Should not be here!!
        item {
            ElevatedButton(
                onClick = onSendPhotoTestClicked,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("사진 전송")
            }
        }
    }
}


@Preview
@Composable
fun EditableFoodItemPreview() {
    EditableFoodItem(item = Food(name = "김치", bestBefore = System.currentTimeMillis()))
}

@Composable
fun EditableFoodItem(
    item: Food,
    onNameChanged: (String) -> Unit = {},
    onBestBeforeChanged: (Long) -> Unit = {}
) {
    Card(
        shape = RoundedCornerShape(4.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
            contentColor = MaterialTheme.colorScheme.primary,
        ),
        modifier = Modifier
            // .clickable(onClick = onClick)
            // .fillMaxWidth()
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(8.dp)
    ) {
        Column {
            val foodName = remember { mutableStateOf(TextFieldValue(item.name)) }
            val foodBestBefore =
                remember { mutableStateOf(TextFieldValue(item.bestBefore?.toString() ?: "")) }
            OutlinedTextField(
                value = foodName.value,
                onValueChange = {
                    foodName.value = it
                    onNameChanged(it.text)
                },
                placeholder = {
                    Text(
                        "이름",
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
            )
            OutlinedTextField(
                value = foodBestBefore.value,
                onValueChange = {
                    foodBestBefore.value = it
                    try {
                        onBestBeforeChanged(it.text.toLong())
                    } catch (_: NumberFormatException) {
                    }
                },
                placeholder = {
                    Text(
                        "유통기한",
                        style = MaterialTheme.typography.labelMedium
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
            )
        }
    }
}
/*
@Preview(showBackground = true, showSystemUi = false)
@Composable
fun FoodListItemPreview() {
    val model = Food(name = "Title string")
    FoodListItem(model = model)
}

 */
