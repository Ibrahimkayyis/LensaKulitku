package data.model

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.capstone.lensakulitku.view.history.HistoryViewModel
import com.capstone.lensakulitku.view.login.LoginViewModel
import com.capstone.lensakulitku.view.signup.SignupViewModel
import com.capstone.lensakulitku.view.loading.AnalysisViewModel
import com.capstone.lensakulitku.view.home.HomeViewModel
import com.capstone.lensakulitku.view.tracking.TrackingDatabase
import com.capstone.lensakulitku.view.tracking.TrackingRepository
import com.capstone.lensakulitku.view.tracking.TrackingViewModel
import data.repository.UserRepository

class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    private val userRepository: UserRepository by lazy {
        UserRepository(context)
    }

    private val trackingRepository: TrackingRepository by lazy {
        val database = TrackingDatabase.getDatabase(context)
        TrackingRepository(database.trackingDao())
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(SignupViewModel::class.java) -> {
                SignupViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(MedicalProfileViewModel::class.java) -> {
                MedicalProfileViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(AnalysisViewModel::class.java) -> {
                AnalysisViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(HistoryViewModel::class.java) -> {
                HistoryViewModel(userRepository) as T
            }
            modelClass.isAssignableFrom(HomeViewModel::class.java) -> {
                HomeViewModel(context) as T
            }
            modelClass.isAssignableFrom(TrackingViewModel::class.java) -> {
                TrackingViewModel(trackingRepository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
}
