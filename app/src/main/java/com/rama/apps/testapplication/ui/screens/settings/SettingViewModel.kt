package com.rama.apps.testapplication.ui.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.rama.apps.testapplication.data.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


/**
 * Created by Dinesh on 11/02/24.
 * dkcse0060@gmail.com
 */

@HiltViewModel
class SettingViewModel @Inject constructor(
    private val appRepository: AppRepository,
): ViewModel(){


    fun logOut(){
        viewModelScope.launch {
            appRepository.logOut()

        }
    }




}