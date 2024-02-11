package com.rama.apps.testapplication.ui.screens.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rama.apps.testapplication.data.model.Video
import com.rama.apps.testapplication.data.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val appRepository: AppRepository
) : ViewModel(){

    companion object {
        private const val TAG = "homeViewModel"
    }


    private val _videos = MutableStateFlow<List<Video>>(emptyList())
    val videos: StateFlow<List<Video>> get() = _videos.asStateFlow()

    init {
        fetchMovies()
    }

    private fun fetchMovies() {
        Log.d(TAG, "fetchMovies: ")
        viewModelScope.launch {
            appRepository.getMovies().collect {
                _videos.value = it
            }
        }
    }

}