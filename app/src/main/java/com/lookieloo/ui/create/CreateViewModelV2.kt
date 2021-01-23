package com.lookieloo.ui.create

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.airbnb.mvrx.MavericksViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.lookieloo.ui.model.Filter

class CreateViewModelV2(initialState: CreateStateV2) :
    MavericksViewModel<CreateStateV2>(initialState) {

    private val defaultLatLng = LatLng(-33.8523341, 151.2106085)
    private val defaultZoom = 15F

    private var _locationMap: GoogleMap? = null

    private val _lastKnownLocation = MutableLiveData<Location>()
    val lastKnownLocation: LiveData<Location>
        get() = _lastKnownLocation

    fun setLocationSelectMap(map: GoogleMap?) {
        _locationMap = map
    }

    fun updateLocationMap() {
        if (_lastKnownLocation.value == null) {
            resetLocationMap()
        } else {
            _locationMap?.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        _lastKnownLocation.value!!.latitude,
                        _lastKnownLocation.value!!.longitude
                    ), defaultZoom
                )
            )
        }
    }


    fun setLastKnownLocation(loc: Location?) {
        _lastKnownLocation.value = loc
        updateLocationMap()
    }

    fun resetLocationMap() {
        _locationMap?.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                defaultLatLng,
                defaultZoom
            )
        )
    }

    fun updateLooTitle(title: String?) {
        setState { copy(title = title) }
    }

    fun updateLooDescription(description: String?) {
        setState { copy(description = description) }
    }

    fun saveLoo() {

    }

    fun saveLocation() {
        val latlng = _locationMap?.cameraPosition?.target
        setState { copy(location = latlng) }
    }

    fun updateFilter(f: Filter, enabled: Boolean) {
        setState {
            copy(filters = filters.map {
                if (it == f) {
                    f.copy(enabled = enabled)
                } else it
            })
        }
    }
}