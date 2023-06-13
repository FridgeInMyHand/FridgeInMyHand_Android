package com.kykint.fridgeinmyhand.compose

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.Settings
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
import com.kykint.fridgeinmyhand.utils.Prefs
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
    val serverApiAddress by viewModel.serverApiAddress.observeAsState()
    val aiApiAddress by viewModel.aiApiAddress.observeAsState()

    if (uiState is UiState.Loading) {
        ServerWaitingDialog(
            onDismissRequest = {
                viewModel.onNoUserInfoFound()
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
    } else if (editingState is EditingState.EditingApiAddress) {
        EditApiAddress(
            serverApiAddress ?: Prefs.serverApiAddress,
            aiApiAddress ?: Prefs.aiApiAddress,
            viewModel::onEditApiAddressDone,
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
            val isUserInfoPresent = (uiState == UiState.Normal || uiState == UiState.Failure)
            Column {
                SettingsMenuLink(
                    icon = { Icon(imageVector = Icons.Filled.LocationOn, "Location") },
                    title = { Text("사용자 위치") },
                    subtitle = {
                        if (isUserInfoPresent) {
                            Text(userLocation?.let { "위도: ${it.latitude}\n경도: ${it.longitude}" }
                                ?: "위치 정보 없음")
                        }
                    },
                    onClick = onLocationChooseClicked,
                )
                SettingsMenuLink(
                    icon = { Icon(imageVector = Icons.Filled.Message, "KakaoTalk Link") },
                    title = { Text("카카오톡 오픈채팅 링크") },
                    subtitle = {
                        if (isUserInfoPresent) {
                            Text(
                                kakaoTalkLink ?: "링크 정보 없음"
                            )
                        }
                    },
                    onClick = viewModel::editUserKakaoTalkLink,
                )
                SettingsMenuLink(
                    icon = { Icon(imageVector = Icons.Filled.Settings, "API Address") },
                    title = { Text("API 주소") },
                    subtitle = { Text("${serverApiAddress}\n${aiApiAddress}") },
                    onClick = viewModel::editApiAddress,
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun EditApiAddress(
    serverApiAddress: String,
    aiApiAddress: String,
    onEditDone: (String, String) -> Unit = { _, _ -> },
) {
    Dialog(
        onDismissRequest = {},
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true),
    ) {
        var newServerApiAddress by remember { mutableStateOf(serverApiAddress) }
        var newAiApiAddress by remember { mutableStateOf(aiApiAddress) }

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
            ) {
                Text("서버 API")
                CustomTextField(
                    value = newServerApiAddress,
                    onValueChange = { newServerApiAddress = it },
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text("AI API")
                CustomTextField(
                    value = newAiApiAddress,
                    onValueChange = { newAiApiAddress = it },
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { onEditDone(newServerApiAddress, newAiApiAddress) },
                    modifier = Modifier.align(Alignment.End)
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
private fun EditApiAddressPreview() {
    val serverApi = "https://api.kykint.com"
    val aiApi = "https://api.kykint.com"
    EditApiAddress(serverApi, aiApi)
}
