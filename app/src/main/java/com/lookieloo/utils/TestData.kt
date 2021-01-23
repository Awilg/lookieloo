package com.lookieloo.utils

import com.google.android.gms.maps.model.LatLng
import com.lookieloo.model.Loo
import com.lookieloo.ui.model.Filter
import com.lookieloo.ui.model.FilterType

val testLoos = listOf(
	Loo(LatLng(47.6062, -122.3321), description = "first", id = "id-1", title = "title1"),
	Loo(LatLng(47.62, -122.34), description = "second", id = "id-2", title = "title2")
)

val testFilters = listOf(
	Filter("Clean", FilterType.Clean),
	Filter("Accessible", FilterType.Accessible),
	Filter("Public", FilterType.Public),
	Filter("Mixed Gender", FilterType.Genderless),
	Filter("Baby Friendly", FilterType.Baby),
)