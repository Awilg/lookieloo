package com.lookieloo.ui.home

import com.airbnb.mvrx.MavericksState
import com.lookieloo.model.Loo
import com.lookieloo.ui.model.Filter
import com.lookieloo.utils.testFilters
import com.lookieloo.utils.testLoos

data class HomeState(
    val test: String? = null,
    val filters: List<Filter> = testFilters,
    val loos: List<Loo> = testLoos
) : MavericksState