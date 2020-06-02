package com.lookieloo.ui.home

import android.location.Location
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.lookieloo.model.Loo
import com.lookieloo.network.LooApi
import com.lookieloo.utils.testLoos
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber

val DEFAULT_LOCATION = LatLng(-33.8523341, 151.2106085)
const val DEFAULT_ZOOM = 17F

class HomeViewModel : ViewModel() {

    private var _map: GoogleMap? = null

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

    init {
        _lastKnownLocation.observeForever { location ->
            getNearbyLoos(location)
            updateMap()
        }
        _nearbyLoos.observeForever { loos ->
            _map?.let { map ->
                loos.forEach { loo ->
                    map.addMarker(
                        MarkerOptions()
                            .position(loo.location)
                    )
                }
            }
        }
    }

    private fun getNearbyLoos(loc: Location) {
        Timber.i("Getting nearby Loos....")
        coroutineScope.launch {
            // Get the Deferred object for our Retrofit request
            // TODO (04) Add filter to getProperties() with filter.value
            val getLoos = LooApi.looService.getLoos()
            try {
                // this will run on a thread managed by Retrofit
                val listResult = getLoos.await()
                //_treasureHuntsNearby.value = listResult
            } catch (e: Exception) {
                Timber.e("Network request failed with exception $e")
                _nearbyLoos.value = testLoos
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
