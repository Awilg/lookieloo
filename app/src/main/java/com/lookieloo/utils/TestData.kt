package com.lookieloo.utils

import com.google.android.gms.maps.model.LatLng
import com.lookieloo.model.Loo
import com.lookieloo.ui.model.Filter
import com.lookieloo.ui.model.FilterType

val testLoos = listOf(
	Loo(LatLng(47.6062, -122.3321), description = "first", id = "id-1", title = "title1"),
	Loo(LatLng(47.6142, -122.3421), description = "second", id = "id-2", title = "title2"),
	Loo(LatLng(47.6462, -122.3821), description = "third", id = "id-3", title = "title3"),
	Loo(LatLng(47.6262, -122.4000), description = "fourth", id = "id-4", title = "title4"),
	Loo(LatLng(47.6211, -122.3111), description = "fifth", id = "id-5", title = "title5")
)

val testFilters = listOf(
	Filter("Clean", FilterType.Clean),
	Filter("Accessible", FilterType.Accessible),
	Filter("Public", FilterType.Public),
	Filter("Mixed Gender", FilterType.Genderless),
	Filter("Baby Friendly", FilterType.Baby),
)