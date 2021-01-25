package com.lookieloo.ui.shared

import com.google.android.gms.maps.model.LatLng
import com.lookieloo.model.Loo
import com.lookieloo.ui.model.Filter
import com.lookieloo.ui.model.FilterType


val testFilters = listOf(
    Filter("Clean", FilterType.Clean),
    Filter("Accessible", FilterType.Accessible),
    Filter("Public", FilterType.Public),
    Filter("Mixed Gender", FilterType.Genderless),
    Filter("Baby Friendly", FilterType.Baby),
)


val testLoos = listOf(
    Loo(
        LatLng(47.6062, -122.3321),
        description = "This is a description. This is a description. This is a description. This is a description. This is a description. This is a description. ",
        id = "id-1",
        title = "Loo title #1",
        filters = testFilters.map { f ->
            if (f.type == FilterType.Genderless || f.type == FilterType.Clean) {
                return@map f.copy(enabled = true)
            }; return@map f
        }
    ),
    Loo(
        LatLng(47.6142, -122.3421),
        description = "This is a description. This is a description. This is a description. This is a description. This is a description. This is a description. ",
        id = "id-2",
        title = "Loo title #2",
        filters = testFilters.map { f ->
            if (f.type == FilterType.Accessible || f.type == FilterType.Clean || f.type == FilterType.Public) {
                return@map f.copy(enabled = true)
            }; return@map f
        }
    ),
    Loo(
        LatLng(47.6462, -122.3821),
        description = "This is a description. This is a description. This is a description. This is a description. This is a description. This is a description. ",
        id = "id-3",
        title = "Loo title #3",
        filters = testFilters.map { f -> f.copy(enabled = true) }
    ),
    Loo(
        LatLng(47.6262, -122.4000),
        description = "This is a description. This is a description. This is a description. This is a description. This is a description. This is a description. ",
        id = "id-4",
        title = "Loo title #4",
        filters = testFilters.map { f ->
            if (f.type == FilterType.Genderless || f.type == FilterType.Baby || f.type == FilterType.Public) {
                return@map f.copy(enabled = true)
            }; return@map f
        }
    ),
    Loo(
        LatLng(47.6211, -122.3111),
        description = "This is a description. This is a description. This is a description. This is a description. This is a description. This is a description. ",
        id = "id-5",
        title = "Loo title #5",
        filters = testFilters.map { f ->
            if (f.type == FilterType.Baby) {
                return@map f.copy(enabled = true)
            }; return@map f
        }
    )
)
