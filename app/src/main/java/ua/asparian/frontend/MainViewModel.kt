package ua.asparian.frontend

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    private val _navigateToLogin = MutableLiveData(false)
    val navigateToLogin: LiveData<Boolean> = _navigateToLogin

    fun triggerLoginNavigation() {
        _navigateToLogin.value = true
    }

    fun resetNavigationState() {
        _navigateToLogin.value = false
    }
}
