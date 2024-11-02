package com.advait.org.assignment.utils

import kotlinx.coroutines.flow.Flow


interface ConnectivityObserver {
    val connectionState: Flow<ConnectionState>
    val currentConnectionState: ConnectionState
}
