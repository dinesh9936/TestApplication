package com.rama.apps.testapplication.utils.InternetConnectivity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log


/**
 * Created by Dinesh on 19/02/24.
 * dineshkumarcse0060@gmail.com
 */
class NetworkChangeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (NetworkUtils.isInternetAvailable()){

        }
    }
}