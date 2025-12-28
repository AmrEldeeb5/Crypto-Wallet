package com.example.cryptovault

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform