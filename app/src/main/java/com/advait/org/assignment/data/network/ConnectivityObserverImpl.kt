package com.advait.org.assignment.data.network

import android.net.ConnectivityManager
import com.advait.org.assignment.utils.ConnectionState
import com.advait.org.assignment.utils.ConnectivityObserver
import kotlinx.coroutines.flow.Flow

/**
 * Implementation of [ConnectivityObserver] that uses [ConnectivityManager] to observe
 * Internet connectivity status.
 */
class ConnectivityObserverImpl(
    private val connectivityManager: ConnectivityManager
) : ConnectivityObserver {

    override val connectionState: Flow<ConnectionState>
        get() = connectivityManager.observeConnectivityAsFlow()

    override val currentConnectionState: ConnectionState
        get() = connectivityManager.currentConnectivityState
}
