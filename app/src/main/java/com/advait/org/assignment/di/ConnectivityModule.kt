package com.advait.org.assignment.di

import android.app.Application
import com.advait.org.assignment.data.network.ConnectivityObserverImpl
import com.advait.org.assignment.data.network.connectivityManager
import com.advait.org.assignment.utils.ConnectivityObserver
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ConnectivityModule {

    @Singleton
    @Provides
    fun provideConnectivityObserver(application: Application): ConnectivityObserver {
        return ConnectivityObserverImpl(application.connectivityManager)
    }

}