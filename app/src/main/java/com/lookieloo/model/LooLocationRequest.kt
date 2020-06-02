package com.lookieloo.model

// Default to a 10k meter search
data class LooLocationRequest  @JvmOverloads constructor (
    val latitude: Double,
    val longitude: Double,
    val minDistance: Double? = 0.0,
    val maxDistance: Double? = 10000.0) {

}