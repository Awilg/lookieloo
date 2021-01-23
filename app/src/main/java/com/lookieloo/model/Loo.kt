package com.lookieloo.model

import com.google.android.gms.maps.model.LatLng

data class Loo(
	val location: LatLng,
	val id: String,
	val title: String,
	val description: String
)
