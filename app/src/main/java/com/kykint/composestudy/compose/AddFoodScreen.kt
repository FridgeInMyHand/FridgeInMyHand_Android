package com.kykint.composestudy.compose

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jakewharton.threetenabp.AndroidThreeTen
import com.kykint.composestudy.data.Food
import com.kykint.composestudy.ui.theme.ComposeStudyTheme
import com.kykint.composestudy.utils.epochSecondsToSimpleDate
import com.kykint.composestudy.viewmodel.DummyAddFoodViewModel
import com.kykint.composestudy.viewmodel.IAddFoodViewModel
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFoodScreen(
    viewModel: IAddFoodViewModel,
    onFabClick: () -> Unit = {},
    onSendPhotoTestClicked: () -> Unit = {},
    onAddDoneClicked: () -> Unit = {},
    onItemRemoveClicked: (Int) -> Unit = {},
) {
    val state by viewModel.state.collectAsState()

    if (state is IAddFoodViewModel.State.Loading) {
        ServerWaitingDialog()
    }

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
                items = viewModel.items, // TODO: should be replaced with an empty list
                viewModel = viewModel,
                onAddFoodItemClicked = viewModel::onAddFoodItemClicked,
                onSendPhotoTestClicked = onSendPhotoTestClicked,
                onAddDoneClicked = onAddDoneClicked,
                onItemRemoveClicked = onItemRemoveClicked,
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun AddFoodScreenPreview() {
    ComposeStudyTheme {
        AddFoodScreen(viewModel = DummyAddFoodViewModel())
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun EditableFoodItemListPreview() {
    val items = (1..3).map { i -> Food(name = "$i") }.toMutableStateList()
    EditableFoodItemList(items = items, viewModel = DummyAddFoodViewModel())
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun EditableFoodItemList(
    items: SnapshotStateList<Food>,
    viewModel: IAddFoodViewModel,
    onItemClick: (Int) -> Unit = {},
    onAddFoodItemClicked: () -> Unit = {},
    onAddDoneClicked: () -> Unit = {},
    onSendPhotoTestClicked: () -> Unit = {},
    onItemRemoveClicked: (Int) -> Unit = {},
) {
    // viewModel.detectedFoodNames.observeAsState().value?.let {
    //     ComposableToast(message = "New items detected")
    // }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(horizontal = 16.dp),
    ) {
        // TODO: key 추가 후 성능 향상 테스트

        itemsIndexed(items = items, key = { index: Int, item: Food -> index }) { index, item ->
            EditableFoodItem(
                item = item,
                onNameChanged = {
                    Log.e("AddFoodScreen", "========== $it =========")
                },
                onItemRemoveClicked = {
                    onItemRemoveClicked(index)
                },
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
        item {
            ElevatedButton(
                onClick = onAddDoneClicked,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("추가 완료")
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
private fun EditableFoodItemPreview() {
    AndroidThreeTen.init(LocalContext.current)
    EditableFoodItem(
        item = Food(
            name = "김치",
            bestBefore = (System.currentTimeMillis() / 1000).toLong()
        )
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EditableFoodItem(
    item: Food,
    onNameChanged: (String) -> Unit = {},
    onBestBeforeChanged: (Long) -> Unit = {},
    onItemRemoveClicked: () -> Unit = {},
) {
    MyElevatedCard(
        modifier = Modifier
            // .clickable(onClick = onClick)
            // .fillMaxWidth()
            .fillMaxWidth()
            .wrapContentHeight(),
    ) {
        val coroutineScope = rememberCoroutineScope()

        var selectedDate by remember {
            mutableStateOf<LocalDate?>(null)
        }
        val calenderState = rememberUseCaseState()

        var foodName by remember { mutableStateOf(TextFieldValue(item.name)) }
        var foodBestBefore by remember {
            mutableStateOf(item.bestBefore?.let {
                epochSecondsToSimpleDate(it)
            } ?: "")
        }

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        CalendarDialog(
            state = calenderState,
            config = CalendarConfig(
                monthSelection = true,
                yearSelection = true,
            ),
            selection = CalendarSelection.Date {
                selectedDate = it
                foodBestBefore = it.format(formatter)
                onBestBeforeChanged(it.atStartOfDay(ZoneId.systemDefault()).toEpochSecond())
            },
        )

        Column(
            modifier = Modifier.background(MaterialTheme.colorScheme.surface),
        ) {
            CustomTextField(
                value = foodName,
                onValueChange = {
                    foodName = it
                    onNameChanged(it.text)
                },
                placeholder = {
                    Text(
                        "이름",
                        style = MaterialTheme.typography.labelSmall
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 8.dp, top = 8.dp),
                textStyle = MaterialTheme.typography.bodySmall,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CustomTextField(
                    value = foodBestBefore,
                    /*
                    onValueChange = {
                        foodBestBefore = it
                        try {
                            onBestBeforeChanged(it.text.toLong())
                        } catch (_: NumberFormatException) {
                        }
                    },
                    */
                    placeholder = {
                        Text(
                            "유통기한",
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(8.dp),
                    enabled = false,
                )
                FilledTonalIconButton(
                    onClick = {
                        coroutineScope.launch {
                            calenderState.show()
                        }
                    },
                    modifier = Modifier
                        .padding(end = 4.dp)
                        .size(32.dp),
                ) {
                    Icon(
                        Icons.Filled.EditCalendar, "",
                        modifier = Modifier.size(16.dp),
                    )
                }
                FilledTonalIconButton(
                    onClick = onItemRemoveClicked,
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .size(32.dp),
                ) {
                    Icon(
                        Icons.Filled.Close, "",
                        modifier = Modifier.size(16.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun ServerWaitingDialog() {
    ProgressDialog {
        Text("Waiting for analysis result...")
    }
}

@Preview
@Composable
private fun ServerWaitingDialogPreview() {
    ComposeStudyTheme {
        ServerWaitingDialog()
    }
}

/*
@Preview(showBackground = true, showSystemUi = false)
@Composable
private fun FoodListItemPreview() {
    val model = Food(name = "Title string")
    FoodListItem(model = model)
}

 */
