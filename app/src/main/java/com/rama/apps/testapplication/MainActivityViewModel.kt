package com.rama.apps.testapplication

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rama.apps.testapplication.data.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * Created by Dinesh on 23/02/24.
 * dineshkumarcse0060@gmail.com
 */

@HiltViewModel
class MainActivityViewModel @Inject constructor(private val appRepository: AppRepository): ViewModel() {

    val isLoggedIn : LiveData<Boolean> = appRepository.isLoggedIn


    init {
        checkUserLoggedIn()
    }

    private fun checkUserLoggedIn(){
        viewModelScope.launch {
            appRepository.checkLoggedIn()
        }
    }
}