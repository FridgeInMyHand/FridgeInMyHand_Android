package com.kykint.fridgeinmyhand.viewmodel

import android.content.SharedPreferences
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
import kotlinx.coroutines.Job
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

        object EditingApiAddress : EditingState()
    }

    abstract val editingState: StateFlow<EditingState>
    abstract val userLocation: LiveData<LatLng>
    abstract val kakaoTalkLink: LiveData<String>
    abstract val serverApiAddress: LiveData<String>
    abstract val aiApiAddress: LiveData<String>

    abstract fun loadInfos()
    abstract fun onNoUserInfoFound()
    abstract fun onEditUserLocationDone(newLoc: LatLng)
    abstract fun editUserKakaoTalkLink()
    abstract fun onEditUserKakaoTalkLinkDone(newLink: String)
    abstract fun editApiAddress()
    abstract fun onEditApiAddressDone(newServerApiAddress: String, newAiApiAddress: String)
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

    private val prefChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
            if (key == Prefs.Key.serverApiAddress) {
                _serverApiAddress.value = Prefs.serverApiAddress
            } else if (key == Prefs.Key.aiApiAddress) {
                _aiApiAddress.value = Prefs.aiApiAddress
            }
        }

    private val _userLocation: MutableLiveData<LatLng> = MutableLiveData()
    override val userLocation: LiveData<LatLng>
        get() = _userLocation

    private val _kakaoTalkLink: MutableLiveData<String> = MutableLiveData()
    override val kakaoTalkLink: LiveData<String>
        get() = _kakaoTalkLink

    private val _serverApiAddress = MutableLiveData(Prefs.serverApiAddress)
    override val serverApiAddress: LiveData<String> = _serverApiAddress

    private val _aiApiAddress = MutableLiveData(Prefs.aiApiAddress)
    override val aiApiAddress: LiveData<String> = _aiApiAddress

    private var loadInfosJob: Job? = null

    init {
        Prefs.registerPrefChangeListener(prefChangeListener)
    }

    override fun loadInfos() {
        if (loadInfosJob?.isActive == true) {
            return
        }

        loadInfosJob = viewModelScope.launch {
            _uiState.value = UiState.Loading
            repository.fetchUserAccountInfo(Prefs.uuid)?.let { userAccountInfo ->
                if (userAccountInfo.lat != null && userAccountInfo.long != null) {
                    // TODO: Change setValue() from background threads to postValue()
                    // This one runs on a main thread, so it's the right place for setValue()
                    _userLocation.value = LatLng(userAccountInfo.lat, userAccountInfo.long)
                    Log.e("COROUTINE", Thread.currentThread().toString())
                }
                userAccountInfo.url?.let { _kakaoTalkLink.value = it }

                loadInfosJob = null
                _uiState.value = UiState.Normal
            } ?: run {
                onNoUserInfoFound()
            }
        }
    }

    override fun onNoUserInfoFound() {
        loadInfosJob?.cancel()
        loadInfosJob = null
        _uiState.value = UiState.Failure
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

    override fun editApiAddress() {
        _editingState.value = EditingState.EditingApiAddress
    }

    override fun onEditApiAddressDone(newServerApiAddress: String, newAiApiAddress: String) {
        Prefs.serverApiAddress = newServerApiAddress
        Prefs.aiApiAddress = newAiApiAddress
        _editingState.value = EditingState.Normal
    }

    override fun onCleared() {
        Prefs.unregisterPrefChangeListener(prefChangeListener)
        super.onCleared()
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
    override val serverApiAddress: LiveData<String> = MutableLiveData()
    override val aiApiAddress: LiveData<String> = MutableLiveData()

    override fun loadInfos() {}
    override fun onNoUserInfoFound() {}
    override fun onEditUserLocationDone(newLoc: LatLng) {}
    override fun editUserKakaoTalkLink() {}
    override fun onEditUserKakaoTalkLinkDone(newLink: String) {}
    override fun editApiAddress() {}
    override fun onEditApiAddressDone(newServerApiAddress: String, newAiApiAddress: String) {}
}
