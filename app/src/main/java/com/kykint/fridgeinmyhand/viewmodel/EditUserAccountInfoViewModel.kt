package com.kykint.fridgeinmyhand.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.kykint.fridgeinmyhand.repository.UserAccountInfoRepository
import com.kykint.fridgeinmyhand.utils.Prefs
import com.naver.maps.geometry.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for EditUserAccountInfoActivity
 */
abstract class IEditUserAccountInfoViewModel : ViewModel() {

    sealed class UiState {
        object Normal : UiState()

        object Success : UiState()

        object Failure : UiState()

        object Loading : UiState()
    }

    abstract val uiState: StateFlow<UiState>

    sealed class EditingState {
        object Normal : EditingState()

        object EditingKakaoTalkLink : EditingState()
    }

    abstract val editingState: StateFlow<EditingState>
    abstract val userLocation: LiveData<LatLng>
    abstract val kakaoTalkLink: LiveData<String>

    abstract fun loadInfos()
    abstract fun onEditUserLocationDone(newLoc: LatLng)
    abstract fun editUserKakaoTalkLink()
    abstract fun onEditUserKakaoTalkLinkDone(newLink: String)
}

class EditUserAccountInfoViewModel(
    private val repository: UserAccountInfoRepository,
) : IEditUserAccountInfoViewModel() {

    // https://developer.android.com/topic/libraries/architecture/viewmodel/viewmodel-factories#kotlin_1
    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val repo = UserAccountInfoRepository()
                return EditUserAccountInfoViewModel(repo) as T
            }
        }
    }

    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Normal)
    override val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _editingState: MutableStateFlow<EditingState> =
        MutableStateFlow(EditingState.Normal)
    override val editingState: StateFlow<EditingState> = _editingState.asStateFlow()

    private val _userLocation: MutableLiveData<LatLng> = MutableLiveData()
    override val userLocation: LiveData<LatLng>
        get() = _userLocation

    private val _kakaoTalkLink: MutableLiveData<String> = MutableLiveData()
    override val kakaoTalkLink: LiveData<String>
        get() = _kakaoTalkLink

    override fun loadInfos() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            repository.fetchUserAccountInfo(Prefs.uuid)?.let { userAccountInfo ->
                if (userAccountInfo.lat != null && userAccountInfo.long != null) {
                    // TODO: Change setValue() from background threads to postValue()
                    // This one runs on a main thread, so it's the right place for setValue()
                    _userLocation.value = LatLng(userAccountInfo.lat, userAccountInfo.long)
                    Log.e("COROUTINE", Thread.currentThread().toString())
                }
                userAccountInfo.url?.let { _kakaoTalkLink.value = it }

                _uiState.value = UiState.Normal
            } ?: run {
                _uiState.value = UiState.Failure
            }
        }
    }

    override fun onEditUserLocationDone(newLatLng: LatLng) {
        _uiState.value = UiState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            repository.saveMyLocation(
                newLatLng,
                onSuccess = {
                    loadInfos()
                },
                onFailure = {
                    _uiState.value = UiState.Failure
                },
            )
        }
    }

    override fun editUserKakaoTalkLink() {
        _editingState.value = EditingState.EditingKakaoTalkLink
    }

    override fun onEditUserKakaoTalkLinkDone(newLink: String) {
        // _kakaoTalkLink.value = newLink
        _editingState.value = EditingState.Normal
        _uiState.value = UiState.Loading

        viewModelScope.launch(Dispatchers.IO) {
            repository.saveMyKakaoTalkLink(
                newLink,
                onSuccess = {
                    loadInfos()
                },
                onFailure = {
                    _uiState.value = UiState.Failure
                },
            )
        }
    }
}

/**
 * Used for preview
 */
class DummyEditUserAccountInfoViewModel : IEditUserAccountInfoViewModel() {
    override val uiState: StateFlow<UiState> = MutableStateFlow(UiState.Normal).asStateFlow()
    override val editingState: StateFlow<EditingState> =
        MutableStateFlow(EditingState.Normal).asStateFlow()
    override val userLocation: LiveData<LatLng> = MutableLiveData()
    override val kakaoTalkLink: LiveData<String> = MutableLiveData()

    override fun loadInfos() {}
    override fun onEditUserLocationDone(newLoc: LatLng) {}
    override fun editUserKakaoTalkLink() {}
    override fun onEditUserKakaoTalkLinkDone(newLink: String) {}
}
