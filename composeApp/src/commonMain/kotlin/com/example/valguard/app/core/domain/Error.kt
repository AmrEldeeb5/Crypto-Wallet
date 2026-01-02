package com.example.valguard.app.core.domain

interface Error {
    val message: String
    val cause: Throwable?
}