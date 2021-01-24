package com.lookieloo.ui.shared


import android.Manifest
import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.airbnb.epoxy.CarouselModelBuilder
import com.airbnb.epoxy.CarouselModel_
import com.airbnb.epoxy.EpoxyController
import com.airbnb.epoxy.EpoxyModel
import com.lookieloo.ui.model.CustomSnappingCarouselModelBuilder
import com.lookieloo.ui.model.CustomSnappingCarouselModel_
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.PermissionRequest


fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun Activity.hideKeyboard() {
    if (currentFocus == null) View(this) else currentFocus?.let { hideKeyboard(it) }
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun Fragment.checkFineLocation(context: Context) {
    if (!EasyPermissions.hasPermissions(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    ) {
        EasyPermissions.requestPermissions(
            PermissionRequest.Builder(
                this, RequestCodes.PERMISSIONS_RC_LOCATION.code,
                Manifest.permission.ACCESS_FINE_LOCATION
            ).build()
        )
    }
}

/** For use in the buildModels method of EpoxyController. A shortcut for creating a Carousel model, initializing it, and adding it to the controller.
 *
 */
inline fun EpoxyController.carousel(modelInitializer: CarouselModelBuilder.() -> Unit) {
    CarouselModel_().apply {
        modelInitializer()
    }.addTo(this)
}

/** For use in the buildModels method of EpoxyController. A shortcut for creating a Carousel model, initializing it, and adding it to the controller.
 *
 */
inline fun EpoxyController.snappingCarousel(modelInitializer: CustomSnappingCarouselModelBuilder.() -> Unit) {
    CustomSnappingCarouselModel_().apply {
        modelInitializer()
    }.addTo(this)
}

inline fun <T> CustomSnappingCarouselModelBuilder.withModelsFrom(
    items: List<T>,
    modelBuilder: (T) -> EpoxyModel<*>
) {
    models(items.map { modelBuilder(it) })
}

/** Add models to a CarouselModel_ by transforming a list of items into EpoxyModels.
 *
 * @param items The items to transform to models
 * @param modelBuilder A function that take an item and returns a new EpoxyModel for that item.
 */
inline fun <T> CarouselModelBuilder.withModelsFrom(
    items: List<T>,
    modelBuilder: (T) -> EpoxyModel<*>
) {
    models(items.map { modelBuilder(it) })
}

fun Fragment.longToast(text: String?) {
    text?.let {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show()
    }
}

fun Fragment.toast(text: String?) {
    text?.let {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show()
    }
}