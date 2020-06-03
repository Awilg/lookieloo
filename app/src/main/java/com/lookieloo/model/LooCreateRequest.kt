package com.lookieloo.model

import com.google.android.gms.maps.model.LatLng

data class LooCreateRequest  @JvmOverloads constructor (
    val description: String,
    val location: LatLng,
    val attributes: Set<String>)