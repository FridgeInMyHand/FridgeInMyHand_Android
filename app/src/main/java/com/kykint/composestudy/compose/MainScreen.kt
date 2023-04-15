package com.kykint.composestudy.compose

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.kykint.composestudy.model.MyModel
import com.kykint.composestudy.ui.ComposableToast
import com.kykint.composestudy.ui.theme.ComposeStudyTheme
import com.kykint.composestudy.viewmodel.FakeMainViewModel
import com.kykint.composestudy.viewmodel.IMainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    viewModel: IMainViewModel,
    onBtnClick: () -> Unit = {},
) {
    ComposeStudyTheme {
        Scaffold(
            topBar = {
                TopAppBar(title = { Text("App Name") })
            },
        ) { contentPadding ->
            Box(
                modifier = Modifier.padding(contentPadding),
            ) {
                Column {
                    // val myModels = viewModel.myModels.observeAsState().value ?: emptyList()
                    val myModels = viewModel.myModels
                    val clickedModel = viewModel.onItemClickEvent.observeAsState().value

                    Box(
                        modifier = Modifier.weight(1f),
                    ) {
                        MyModelList(
                            models = myModels,
                            onItemClick = viewModel::onItemClick,
                        )
                    }
                    ElevatedButton(
                        onClick = onBtnClick,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                    ) {
                        Text("This is a button")
                    }

                    clickedModel?.let {
                        it.getContentIfNotHandled()?.let {
                            ComposableToast(it.title)
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
fun MainScreenPreview() {
    MainScreen(viewModel = FakeMainViewModel())
}

@Composable
fun MyModelList(models: List<MyModel>, onItemClick: (Int) -> Unit = {}) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // TODO: key 추가 후 성능 향상 테스트
        itemsIndexed(models) { index, item ->
            MyModelListItem(model = item, onClick = {
                onItemClick.invoke(index)
            })
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MyModelListPreview() {
    val models = (1..3).map { i -> MyModel(title = "$i") }
    MyModelList(models = models)
}

@Composable
fun MyModelListItem(model: MyModel, onClick: () -> Unit = {}) {
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
            text = model.title,
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
fun MyModelListItemPreview() {
    val model = MyModel(title = "Title string")
    MyModelListItem(model = model)
}

@Composable
fun Greeting(name: String) {
    Box(
        contentAlignment = Alignment.Center,
        // modifier = Modifier.fillMaxSize(),
    ) {
        Text(text = "Hello $name!", modifier = Modifier.align(Alignment.Center))
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    ComposeStudyTheme {
        Greeting("Android")
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview2() {
    ComposeStudyTheme {
        Greeting("Android22")
    }
}
