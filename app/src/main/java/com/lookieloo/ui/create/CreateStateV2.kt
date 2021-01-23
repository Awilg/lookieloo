package com.lookieloo.ui.create

import com.airbnb.mvrx.MavericksState
import com.google.android.gms.maps.model.LatLng

data class CreateStateV2(
    val title: String? = null,
    val description: String? = null,
    val location: LatLng? = null
) : MavericksState{
}