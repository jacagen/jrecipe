package com.jacagen.jrecipe

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform