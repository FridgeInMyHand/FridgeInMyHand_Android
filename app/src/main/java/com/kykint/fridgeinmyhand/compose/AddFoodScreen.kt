package com.kykint.fridgeinmyhand.compose

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.runtime.CompositionLocalProvider
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.jakewharton.threetenabp.AndroidThreeTen
import com.kykint.fridgeinmyhand.data.Food
import com.kykint.fridgeinmyhand.ui.theme.FridgeInMyHandTheme
import com.kykint.fridgeinmyhand.utils.epochSecondsToSimpleDate
import com.kykint.fridgeinmyhand.viewmodel.DummyAddFoodViewModel
import com.kykint.fridgeinmyhand.viewmodel.IAddFoodViewModel
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
    onAddFoodItemClicked: () -> Unit = {},
    onFoodNameChanged: (Int, String) -> Unit = { _, _ -> },
    onFoodBestBeforeChanged: (Int, Long) -> Unit = { _, _ -> },
    onFoodAmountChanged: (Int, String) -> Unit = { _, _ -> },
    onFoodPublicChanged: (Int, Boolean) -> Unit = { _, _ -> },
    onAddDoneClicked: () -> Unit = {},
    onItemRemoveClicked: (Int) -> Unit = {},
) {
    val state by viewModel.state.collectAsState()

    if (state is IAddFoodViewModel.State.Loading) {
        ServerWaitingDialog()
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("음식 추가") })
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
                onAddFoodItemClicked = onAddFoodItemClicked,
                onFoodNameChanged = onFoodNameChanged,
                onFoodBestBeforeChanged = onFoodBestBeforeChanged,
                onFoodAmountChanged = onFoodAmountChanged,
                onFoodPublicChanged = onFoodPublicChanged,
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
    FridgeInMyHandTheme {
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

@OptIn(ExperimentalFoundationApi::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun EditableFoodItemList(
    items: SnapshotStateList<Food>,
    viewModel: IAddFoodViewModel,
    onItemClick: (Int) -> Unit = {},
    onFoodNameChanged: (Int, String) -> Unit = { _, _ -> },
    onFoodBestBeforeChanged: (Int, Long) -> Unit = { _, _ -> },
    onFoodAmountChanged: (Int, String) -> Unit = { _, _ -> },
    onFoodPublicChanged: (Int, Boolean) -> Unit = { _, _ -> },
    onAddFoodItemClicked: () -> Unit = {},
    onAddDoneClicked: () -> Unit = {},
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

        itemsIndexed(items = items) { index, item ->
            EditableFoodItem(
                item = item,
                onFoodNameChanged = {
                    onFoodNameChanged(index, it)
                },
                onFoodBestBeforeChanged = {
                    onFoodBestBeforeChanged(index, it)
                },
                onFoodAmountChanged = {
                    onFoodAmountChanged(index, it)
                },
                onFoodPublicChanged = {
                    onFoodPublicChanged(index, it)
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
        ),
        onItemRemoveClicked = {},
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditableFoodItem(
    item: Food,
    onFoodNameChanged: (String) -> Unit = {},
    onFoodBestBeforeChanged: (Long) -> Unit = {},
    onFoodAmountChanged: (String) -> Unit = {},
    onFoodPublicChanged: (Boolean) -> Unit = {},
    onItemRemoveClicked: (() -> Unit)? = null,
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

        // rememberUpdatedState를 대신 쓸 수도 있지만 var로 선언할 수 없음
        var foodName by remember { mutableStateOf(item.name) }.apply {
            value = item.name
        }
        var foodBestBefore by remember { mutableStateOf(item.bestBefore) }.apply {
            value = item.bestBefore
        }
        var foodAmount by remember { mutableStateOf(item.amount) }.apply {
            value = item.amount
        }
        var foodPublic by remember { mutableStateOf(item.publicFood) }.apply {
            value = item.publicFood
        }

        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        /**
         * 유통기한 수정 버튼 클릭 시 나오는 캘린더
         */
        CalendarDialog(
            state = calenderState,
            config = CalendarConfig(
                monthSelection = true,
                yearSelection = true,
            ),
            selection = CalendarSelection.Date {
                val epoch = it.atStartOfDay(ZoneId.systemDefault()).toEpochSecond()
                selectedDate = it
                foodBestBefore = epoch
                onFoodBestBeforeChanged(epoch)
            },
        )

        Column(
            modifier = Modifier.background(MaterialTheme.colorScheme.surface),
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                /**
                 * 음식 이름 입력칸
                 */
                CustomTextField(
                    value = foodName,
                    onValueChange = {
                        foodName = it
                        onFoodNameChanged(it)
                    },
                    placeholder = {
                        Text(
                            "이름",
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    modifier = Modifier
                        .weight(0.7f)
                        .fillMaxWidth(),
                    textStyle = MaterialTheme.typography.bodySmall,
                )

                Spacer(Modifier.width(8.dp))

                /**
                 * 음식 양 입력 칸
                 */
                CustomTextField(
                    value = foodAmount ?: "",
                    onValueChange = {
                        foodAmount = it
                        onFoodAmountChanged(it)
                    },
                    placeholder = {
                        Text(
                            "수량",
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    modifier = Modifier.weight(0.3f),
                    textStyle = MaterialTheme.typography.bodySmall,
                )

                Spacer(Modifier.width(16.dp))

                /**
                 * 공유 여부 선택
                 */
                Text(
                    "공유",
                    style = MaterialTheme.typography.labelSmall,
                )
                CompositionLocalProvider(LocalMinimumInteractiveComponentEnforcement provides false) {
                    Checkbox(
                        checked = foodPublic ?: false,
                        onCheckedChange = {
                            foodPublic = it
                            onFoodPublicChanged(it)
                        },
                        modifier = Modifier.scale(0.7f),
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            /**
             * 음식 유통기한 입력 칸
             * 수동 입력이 아닌 캘린더 버튼 통해 입력
             */
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CustomTextField(
                    value = foodBestBefore?.let {
                        epochSecondsToSimpleDate(it).format(formatter)
                    } ?: "",
                    placeholder = {
                        Text(
                            "유통기한",
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    enabled = false,
                    textStyle = MaterialTheme.typography.bodySmall,
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
                onItemRemoveClicked?.let {
                    FilledTonalIconButton(
                        onClick = it,
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

            Spacer(modifier = Modifier.height(8.dp))
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
    FridgeInMyHandTheme {
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
