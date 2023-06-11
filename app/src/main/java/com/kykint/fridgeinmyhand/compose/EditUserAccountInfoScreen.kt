package com.kykint.fridgeinmyhand.compose

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Message
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.alorma.compose.settings.ui.SettingsMenuLink
import com.kykint.fridgeinmyhand.ui.theme.FridgeInMyHandTheme
import com.kykint.fridgeinmyhand.viewmodel.DummyEditUserAccountInfoViewModel
import com.kykint.fridgeinmyhand.viewmodel.IEditUserAccountInfoViewModel
import com.kykint.fridgeinmyhand.viewmodel.IEditUserAccountInfoViewModel.EditingState
import com.kykint.fridgeinmyhand.viewmodel.IEditUserAccountInfoViewModel.UiState


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditUserAccountInfoScreen(
    viewModel: IEditUserAccountInfoViewModel,
    onLocationChooseClicked: () -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    val editingState by viewModel.editingState.collectAsState()

    val userLocation by viewModel.userLocation.observeAsState()
    val kakaoTalkLink by viewModel.kakaoTalkLink.observeAsState()

    if (uiState is UiState.Loading) {
        ServerWaitingDialog(
            onDismissRequest = {
                viewModel.cancelLoadInfos()
            },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
            ),
        )
    }

    if (editingState is EditingState.EditingKakaoTalkLink) {
        EditUserKakaoTalkLink(
            kakaoTalkLink ?: "",
            onEditDone = viewModel::onEditUserKakaoTalkLinkDone,
        )
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("사용자 정보 수정") },
            )
        },
    ) { contentPadding ->
        Box(
            modifier = Modifier.padding(contentPadding),
        ) {
            Column {
                SettingsMenuLink(
                    icon = { Icon(imageVector = Icons.Filled.LocationOn, "Location") },
                    title = { Text("사용자 위치") },
                    subtitle = {
                        Text(userLocation?.let { "위도: ${it.latitude}\n경도: ${it.longitude}" }
                            ?: (if (uiState == UiState.Normal) "위치 정보 없음" else "정보를 불러오지 못했습니다."))
                    },
                    onClick = onLocationChooseClicked,
                    enabled = uiState == UiState.Normal,
                )
                SettingsMenuLink(
                    icon = { Icon(imageVector = Icons.Filled.Message, "KakaoTalk Link") },
                    title = { Text("카카오톡 오픈채팅 링크") },
                    subtitle = {
                        Text(
                            kakaoTalkLink
                                ?: (if (uiState == UiState.Normal) "링크 정보 없음" else "정보를 불러오지 못했습니다.")
                        )
                    },
                    onClick = viewModel::editUserKakaoTalkLink,
                    enabled = uiState == UiState.Normal,
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun EditUserAccountInfoScreenPreview() {
    FridgeInMyHandTheme {
        EditUserAccountInfoScreen(viewModel = DummyEditUserAccountInfoViewModel())
    }
}

@Composable
private fun ServerWaitingDialog(
    onDismissRequest: () -> Unit = {},
    properties: DialogProperties = DialogProperties(
        dismissOnBackPress = false,
        dismissOnClickOutside = false
    ),
) {
    ProgressDialog(
        onDismissRequest = onDismissRequest,
        properties = properties,
    ) {
        Text("Fetching info from server...")
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
private fun EditUserKakaoTalkLink(
    link: String,
    onEditDone: (String) -> Unit = {},
) {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false),
    ) {
        var newLink by remember { mutableStateOf(link) }
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.End,
            ) {
                CustomTextField(
                    value = newLink,
                    onValueChange = { newLink = it },
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { onEditDone(newLink) },
                ) {
                    Text("Save")
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Preview
@Composable
private fun EditUserKakaoTalkLinkPreview() {
    val link = "https://kakaotalk.com"
    EditUserKakaoTalkLink(link)
}
