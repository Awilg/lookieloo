package com.lookieloo.ui.create

import com.airbnb.mvrx.MavericksViewModel

class CreateViewModelV2(initialState: CreateStateV2) :
    MavericksViewModel<CreateStateV2>(initialState) {

    fun updateLooTitle(title: String?) {
        setState { copy(title = title) }
    }

    fun updateLooDescription(description: String?) {
        setState { copy(description = description) }
    }

    fun saveLoo() {

    }
}