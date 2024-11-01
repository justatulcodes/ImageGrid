package com.advait.org.assignment.data.cache

class Config {
    companion object {
        val maxMemory = Runtime.getRuntime().maxMemory() / 1024
        val memoryCacheSize = (maxMemory/8).toInt()
        val diskCacheSize = 30 * 1024 * 1024L // 30MB

    }
}