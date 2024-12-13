package data.model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import data.repository.UserRepository
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MedicalProfileViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _userId = MutableLiveData<String?>()
    val userId: LiveData<String?> = _userId

    private val _updateResult = MutableLiveData<String?>()
    val updateResult: LiveData<String?> = _updateResult

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _userProfile = MutableLiveData<MedicalProfileRequest?>()
    val userProfile: LiveData<MedicalProfileRequest?> = _userProfile

    fun setUserId(userId: String) {
        _userId.value = userId
    }

    fun updateUserProfile(userId: String, profileRequest: MedicalProfileRequest) {
        _isLoading.value = true
        userRepository.updateUserProfile(userId, profileRequest).enqueue(object : Callback<UpdateResponse> {
            override fun onResponse(
                call: Call<UpdateResponse>,
                response: Response<UpdateResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _updateResult.value = response.body()?.msg
                } else {
                    _updateResult.value = "Failed to update profile."
                }
            }

            override fun onFailure(call: Call<UpdateResponse>, t: Throwable) {
                _isLoading.value = false
                _updateResult.value = "An error occurred: ${t.message}"
            }
        })
    }

    fun getUserProfile(authToken: String) {
        _isLoading.value = true
        userRepository.getUserProfile().enqueue(object : Callback<MedicalProfileRequest> {
            override fun onResponse(
                call: Call<MedicalProfileRequest>,
                response: Response<MedicalProfileRequest>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _userProfile.value = response.body()
                } else {
                    _errorMessage.value = "Failed to fetch user profile: ${response.message()}"
                    Log.e("API_REQUEST", "Error: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<MedicalProfileRequest>, t: Throwable) {
                _isLoading.value = false
                _errorMessage.value = "An error occurred: ${t.message}"
                Log.e("API_REQUEST", "Failure: ${t.message}")
            }
        })
    }

}
