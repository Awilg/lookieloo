package com.lookieloo.ui.create

import com.airbnb.mvrx.MavericksState
import com.google.android.gms.maps.model.LatLng
import com.lookieloo.ui.model.Filter
import com.lookieloo.utils.testFilters

data class CreateState(
    val title: String? = null,
    val description: String? = null,
    val location: LatLng? = null,
    val filters: List<Filter> = testFilters
) : MavericksState