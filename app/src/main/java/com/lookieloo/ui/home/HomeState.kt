package com.lookieloo.ui.home

import com.airbnb.mvrx.MavericksState
import com.lookieloo.ui.model.Filter
import com.lookieloo.utils.testFilters

data class HomeState(
    val test: String? = null,
    val filters: List<Filter> = testFilters
) : MavericksState