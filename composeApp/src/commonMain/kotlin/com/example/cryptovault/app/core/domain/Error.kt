package com.example.cryptovault.app.core.domain

interface Error {
    val message: String
    val cause: Throwable?
}