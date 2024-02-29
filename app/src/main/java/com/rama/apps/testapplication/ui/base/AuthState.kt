package com.rama.apps.testapplication.ui.base


/**
 * Created by Dinesh on 12/02/24.
 * dineshkumarcse0060@gmail.com
 */
sealed class AuthState {
    object Loading : AuthState()
    object CodeSent : AuthState()
    object Authenticated : AuthState()
    object LoggedOut : AuthState()
    data class Error(val message: String) : AuthState()
}