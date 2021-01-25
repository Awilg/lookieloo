package com.lookieloo.model

import com.google.android.gms.maps.model.LatLng
import com.lookieloo.ui.model.Filter

data class Loo(
	val location: LatLng,
	val id: String,
	val title: String,
	val description: String,
	val filters: List<Filter>? = null
)
