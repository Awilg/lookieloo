package com.lookieloo.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
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