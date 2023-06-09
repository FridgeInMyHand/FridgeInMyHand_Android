package com.kykint.fridgeinmyhand.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.kykint.fridgeinmyhand.data.Food
import com.kykint.fridgeinmyhand.data.UserLocation
import com.kykint.fridgeinmyhand.repository.FoodListRepositoryImpl
import com.kykint.fridgeinmyhand.repository.IFoodListRepository
import com.kykint.fridgeinmyhand.repository.IUserAccountInfoRepository
import com.kykint.fridgeinmyhand.repository.IUserRepository
import com.kykint.fridgeinmyhand.repository.UserAccountInfoRepository
import com.kykint.fridgeinmyhand.repository.UserRepository
import com.kykint.fridgeinmyhand.utils.isMainThread
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for FoodShareActivity
 */
abstract class IFoodShareViewModel : ViewModel() {
    sealed class UiState {
        object Normal : UiState()

        object Success : UiState()
        // data class Success(
        //     val list: List<Food>
        // ) : State()

        object Failure : UiState()

        object Loading : UiState()
    }

    abstract val uiState: StateFlow<UiState>

    sealed class NearbyUserInfoState {
        object Normal : NearbyUserInfoState()

        object Success : NearbyUserInfoState()

        object Failure : NearbyUserInfoState()

        object Loading : NearbyUserInfoState()
    }

    abstract val nearbyUserInfoState: StateFlow<NearbyUserInfoState>

    abstract val nearbyUsers: LiveData<List<UserLocation>>
    abstract val nearbyUserFoods: LiveData<List<Food>>
    abstract val nearbyUserUrl: LiveData<String?>

    abstract fun refreshNearbyUsers(lat: Double, lng: Double, latLimit: Double, lngLimit: Double)
    abstract fun fetchNearbyUserInfo(uuid: String)
}

class FoodShareViewModel(
    private val userRepository: IUserRepository,
    private val userAccountInfoRepository: IUserAccountInfoRepository,
    private val foodListRepository: IFoodListRepository,
) : IFoodShareViewModel() {
    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Normal)
    override val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private val _nearbyUserInfoState: MutableStateFlow<NearbyUserInfoState> =
        MutableStateFlow(NearbyUserInfoState.Normal)
    override val nearbyUserInfoState: StateFlow<NearbyUserInfoState> =
        _nearbyUserInfoState.asStateFlow()

    // https://developer.android.com/topic/libraries/architecture/viewmodel/viewmodel-factories#kotlin_1
    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                return FoodShareViewModel(
                    UserRepository(),
                    UserAccountInfoRepository(),
                    FoodListRepositoryImpl,
                ) as T
            }
        }
    }

    private val _nearbyUsers: MutableLiveData<List<UserLocation>> = MutableLiveData()
    override val nearbyUsers: LiveData<List<UserLocation>> = _nearbyUsers
    private val _nearbyUserFoods: MutableLiveData<List<Food>> = MutableLiveData()
    override val nearbyUserFoods: LiveData<List<Food>> = _nearbyUserFoods
    private val _nearbyUserUrl: MutableLiveData<String?> = MutableLiveData()
    override val nearbyUserUrl: LiveData<String?> = _nearbyUserUrl


    private var refreshJob: Job? = null

    override fun refreshNearbyUsers(
        lat: Double, lng: Double, latLimit: Double, lngLimit: Double,
    ) {
        if (refreshJob?.isActive == true) {
            return
        }

        refreshJob = viewModelScope.launch(Dispatchers.IO) {
            _uiState.value = UiState.Loading
            userRepository.getNearbyUsers(
                lat, lng, latLimit, lngLimit,
                onSuccess = { userLocations ->
                    _nearbyUsers.postValue(userLocations)
                    _uiState.value = UiState.Normal
                    refreshJob = null
                },
                onFailure = {
                    _uiState.value = UiState.Failure
                    refreshJob = null
                }
            )
        }
    }

    private var fetchJob: Job? = null

    override fun fetchNearbyUserInfo(
        uuid: String,
    ) {
        if (fetchJob?.isActive == true) {
            return
        }

        fetchJob = viewModelScope.launch {
            _nearbyUserInfoState.value = NearbyUserInfoState.Loading
            val fetchedFoods = foodListRepository.fetchFoodList(uuid)
            val fetchedUserInfo = userAccountInfoRepository.fetchUserAccountInfo(uuid)

            if (fetchedFoods != null && fetchedUserInfo != null) {
                if (isMainThread) {
                    _nearbyUserFoods.value = fetchedFoods!!
                    _nearbyUserUrl.value = fetchedUserInfo.url
                } else {
                    _nearbyUserFoods.postValue(fetchedFoods!!)
                    _nearbyUserUrl.postValue(fetchedUserInfo.url)
                }
                _nearbyUserInfoState.value = NearbyUserInfoState.Normal
                Log.e("fetchJob", "FETCH DONE WITH SUCCESS")
            } else {
                Log.e("fetchJob", "FETCH DONE WITH FAILURE")
            }
            fetchJob = null
        }
    }
}

/**
 * Used for preview
 */
class DummyFoodShareViewModel : IFoodShareViewModel() {
    override val uiState: StateFlow<UiState> = MutableStateFlow(UiState.Normal).asStateFlow()
    override val nearbyUserInfoState: StateFlow<NearbyUserInfoState> =
        MutableStateFlow(NearbyUserInfoState.Normal).asStateFlow()

    override val nearbyUsers: LiveData<List<UserLocation>> = MutableLiveData()
    override val nearbyUserFoods: LiveData<List<Food>> = MutableLiveData()
    override val nearbyUserUrl: LiveData<String?> = MutableLiveData()

    override fun refreshNearbyUsers(lat: Double, lng: Double, latLimit: Double, lngLimit: Double) {}
    override fun fetchNearbyUserInfo(uuid: String) {}

}
