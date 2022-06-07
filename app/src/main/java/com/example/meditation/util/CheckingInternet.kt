package com.example.meditation.util

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.getSystemService
import androidx.lifecycle.LiveData

class CheckingInternet(private val cm: ConnectivityManager) : LiveData<Boolean>() {
    constructor(application: Application): this(
        application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    )

    private val networkCallback = object : ConnectivityManager.NetworkCallback(){
        override fun onAvailable(network: Network) {
            super.onAvailable(network)
            postValue(true)
            Log.d("msg", "onAvailable")
        }

        override fun onLost(network: Network) {
            super.onLost(network)
            postValue(false)
            Log.d("msg", "onLost")
        }

        //BUG FROM Google
        override fun onUnavailable() {
            super.onUnavailable()
            postValue(false)
            Log.d("msg", "onUnavailable")
        }

        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            super.onCapabilitiesChanged(network, networkCapabilities)
            val isInternet = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)

            val isValidated = networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)

            postValue(isInternet && isValidated)
        }
    }

    override fun onActive() {
        super.onActive()
        val request = NetworkRequest.Builder()
        cm.registerNetworkCallback(request.build(), networkCallback)
    }

    override fun onInactive() {
        super.onInactive()
        cm.unregisterNetworkCallback(networkCallback)
    }

    //Replace onUnavailable method
    fun isConnected(application: Application) {
        var connected = false
        try {
            val cm = application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val nInfo = cm.activeNetworkInfo
            connected = nInfo != null && nInfo.isAvailable && nInfo.isConnected

            postValue(connected)
            return
        } catch (e: Exception) {
            Log.e("Connectivity Exception", e.message!!)
        }
        postValue(connected)
    }

}