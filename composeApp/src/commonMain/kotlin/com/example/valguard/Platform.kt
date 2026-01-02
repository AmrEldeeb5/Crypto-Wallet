package com.example.valguard

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform