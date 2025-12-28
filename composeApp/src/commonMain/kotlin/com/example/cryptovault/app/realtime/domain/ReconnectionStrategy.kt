package com.example.cryptovault.app.realtime.domain

interface ReconnectionStrategy {

    fun nextDelay(attemptNumber: Int): Long


    fun shouldFallback(attemptNumber: Int): Boolean


    fun reset()
}
