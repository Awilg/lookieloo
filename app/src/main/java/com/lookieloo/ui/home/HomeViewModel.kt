package com.lookieloo.ui.home

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng

val DEFAULT_LOCATION = LatLng(-33.8523341, 151.2106085)
const val DEFAULT_ZOOM = 15F

class HomeViewModel : ViewModel() {

	private var _map: GoogleMap? = null

	private val _lastKnownLocation = MutableLiveData<Location>()
	val lastKnownLocation: LiveData<Location>
		get() = _lastKnownLocation

	fun setMap(map: GoogleMap) {
		_map = map.also {
			it.setMinZoomPreference(6.0f)
			it.setMaxZoomPreference(14.0f)
		}
	}

	fun setLastKnownLocation(loc: Location?) {
		_lastKnownLocation.value = loc
	}

	fun updateMap() {
		_lastKnownLocation.value?.let { location ->
			_map?.moveCamera(
				CameraUpdateFactory.newLatLngZoom(
					LatLng(
						location.latitude,
						location.longitude
					), DEFAULT_ZOOM
				)
			)
		}
	}

	fun resetMap() {
		_map?.moveCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, DEFAULT_ZOOM))
	}
}
