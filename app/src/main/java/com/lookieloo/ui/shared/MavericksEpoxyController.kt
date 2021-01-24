package com.lookieloo.ui.shared

import androidx.fragment.app.Fragment
import com.airbnb.epoxy.AsyncEpoxyController
import com.airbnb.epoxy.EpoxyController
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.withState


internal open class MavericksEpoxyController(
    val buildModelsCallback: EpoxyController.() -> Unit = {}
) : AsyncEpoxyController() {

    override fun buildModels() {
        buildModelsCallback()
    }
}

fun Fragment.simpleController(
    buildModels: EpoxyController.() -> Unit
): EpoxyController = MavericksEpoxyController {
    // Models are built asynchronously, so it is possible that this is called after the fragment
    // is detached under certain race conditions.
    if (view == null || isRemoving) return@MavericksEpoxyController
    buildModels()
}

fun <S : MavericksState, A : MavericksViewModel<S>> Fragment.simpleController(
    viewModel: A,
    buildModels: EpoxyController.(state: S) -> Unit
): EpoxyController = MavericksEpoxyController {
    if (view == null || isRemoving) return@MavericksEpoxyController
    withState(viewModel) { state ->
        buildModels(state)
    }
}