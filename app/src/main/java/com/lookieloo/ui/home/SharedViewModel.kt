package com.lookieloo.ui.home

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.airbnb.mvrx.MavericksViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.lookieloo.model.Loo
import com.lookieloo.model.LooCreateRequest
import com.lookieloo.model.LooLocationRequest
import com.lookieloo.network.LooApi
import com.lookieloo.ui.model.Filter
import com.lookieloo.utils.testLoos
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList

val DEFAULT_LOCATION = LatLng(47.60357, -122.32945)
const val DEFAULT_ZOOM = 17F

class SharedViewModel(initialState: HomeState) : MavericksViewModel<HomeState>(initialState) {

    private var _map: GoogleMap? = null

    private val defaultLatLng = LatLng(-33.8523341, 151.2106085)
    private val defaultZoom = 15F

    private val _lastKnownLocation = MutableLiveData<Location>()
    val lastKnownLocation: LiveData<Location>
        get() = _lastKnownLocation

    private val _nearbyLoos = MutableLiveData<List<Loo>>()
    val nearbyLoos: LiveData<List<Loo>>
        get() = _nearbyLoos

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    // Currently selected Loo
    private val _currentSelectedLoo = MutableLiveData<Loo>()
    val currentSelectedLoo: LiveData<Loo>
        get() = _currentSelectedLoo

    // Filters
    private val _filters = MutableLiveData<List<String>>()
    val filters: LiveData<List<String>>
        get() = _filters

    init {
        Timber.i("VIEWMODEL INIT....")
        _nearbyLoos.value = testLoos
        _lastKnownLocation.observeForever { location ->
            getNearbyLoos(location)
            updateMap()
        }
    }

    private fun getNearbyLoos(loc: Location) {
        Timber.i("Getting nearby Loos....")
        coroutineScope.launch {
            // TODO (04) Add filter to getProperties() with filter.value
            val getLoos = LooApi.looService.getNearbyLoos(
                LooLocationRequest(
                    latitude = loc.latitude,
                    longitude = loc.longitude
                )
            )
            try {
                // this will run on a thread managed by Retrofit
                val listResult = getLoos.await()
                _nearbyLoos.value = listResult
            } catch (e: Exception) {
                Timber.e("Network request failed with exception $e")
                _nearbyLoos.value = testLoos
            }
        }
    }

    fun createLoo(description : String){
        _lastKnownLocation.value?.let {loc ->
            coroutineScope.launch {
                val getLoos = LooApi.looService.createLoo(
                    LooCreateRequest(
                        description = description,
                        location = LatLng(loc.latitude, loc.longitude),
                        attributes = emptySet())
                )
                try {
                    getLoos.await()
                } catch (e: Exception) {
                    Timber.e("Network request failed with exception $e")
                }
            }
        }
    }

    fun setMap(map: GoogleMap) {
        _map = map.also {
            it.setMinZoomPreference(10.0f)
            it.setMaxZoomPreference(19.0f)
        }
    }

    fun setLastKnownLocation(loc: Location?) {
        _lastKnownLocation.value = loc
    }

    fun updateMap() {
        if (_lastKnownLocation.value == null) {
            resetMap()
        } else {
            _map?.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        _lastKnownLocation.value!!.latitude,
                        _lastKnownLocation.value!!.longitude
                    ), defaultZoom
                )
            )
        }
    }

    fun moveMapToLocation(loc :LatLng) {
        _map?.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                loc, DEFAULT_ZOOM
            )
        )
    }

    private fun resetMap() {
        _map?.moveCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, DEFAULT_ZOOM))
    }

    fun updateFilters(text: CharSequence, isChecked: Boolean) {
        if (_filters.value == null) {
            _filters.value = ArrayList()
        }
        if (isChecked) {
            _filters.value =
                _filters.value?.plus(text.toString().toLowerCase(Locale.ROOT).replace(" ", "-"))
        } else {
            _filters.value =
                _filters.value?.minus(text.toString().toLowerCase(Locale.ROOT).replace(" ", "-"))
        }
    }

    fun setCurrentLoo(loo: Loo) {
        _currentSelectedLoo.value = loo
    }

    fun updateFilter(f: Filter, b: Boolean) {
        setState {
            copy(filters = filters.map {
                if (it == f) {
                    f.copy(enabled = b)
                } else it
            })
        }
    }
}
