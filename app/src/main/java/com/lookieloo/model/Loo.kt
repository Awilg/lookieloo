package com.lookieloo.model

import com.google.android.gms.maps.model.LatLng

data class Loo(
	val location: LatLng,
	val name: String
)
