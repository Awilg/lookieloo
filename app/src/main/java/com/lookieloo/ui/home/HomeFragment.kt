package com.lookieloo.ui.home

import android.annotation.SuppressLint
import android.app.Activity
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.Carousel
import com.airbnb.epoxy.Carousel.setDefaultGlobalSnapHelperFactory
import com.airbnb.epoxy.EpoxyRecyclerView
import com.airbnb.epoxy.carousel
import com.airbnb.mvrx.MavericksView
import com.airbnb.mvrx.activityViewModel
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.*
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.material.button.MaterialButton
import com.google.gson.GsonBuilder
import com.lookieloo.*
import com.lookieloo.databinding.FragmentHomeBinding
import com.lookieloo.model.GeocodingResult
import com.lookieloo.model.Loo
import com.lookieloo.ui.model.FilterModel_
import com.lookieloo.ui.model.LooModel_
import com.lookieloo.ui.model.customSnappingCarousel
import com.lookieloo.ui.shared.*
import org.json.JSONArray
import org.json.JSONException
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber

class HomeFragment : Fragment(), MavericksView, OnMapReadyCallback,
    GoogleMap.OnMarkerClickListener,
    EasyPermissions.PermissionCallbacks,
    MaterialButton.OnCheckedChangeListener,
    PlacePredictionAdapter.OnPlaceClickListener,
    GoogleMap.OnMyLocationButtonClickListener,
    GoogleMap.OnMyLocationClickListener {

    private lateinit var homeBinding: FragmentHomeBinding
    private lateinit var filterRecyclerView: EpoxyRecyclerView
    private lateinit var looRecyclerView: EpoxyRecyclerView
    private val sharedViewModel: SharedViewModel by activityViewModel()

    private val handler = Handler()

    private val adapter = PlacePredictionAdapter()
    private var sessionToken: AutocompleteSessionToken? = null
    private val gson =
        GsonBuilder().registerTypeAdapter(LatLng::class.java, LatLngAdapter()).create()
    private lateinit var queue: RequestQueue

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var placesClient: PlacesClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        placesClient = Places.createClient(requireContext())
        sessionToken = AutocompleteSessionToken.newInstance()
        queue = Volley.newRequestQueue(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        homeBinding = FragmentHomeBinding.inflate(inflater)
        homeBinding.viewmodel = sharedViewModel
        homeBinding.lifecycleOwner = this

        filterRecyclerView = homeBinding.filterRecyclerView
        looRecyclerView = homeBinding.looRecyclerView
        looRecyclerView.setController(simpleController(sharedViewModel) { state ->
            customSnappingCarousel {
                id("looCarousel")
                padding(Carousel.Padding.dp(24, 8, 24, 8, 8))
                snapHelperCallback {
                    sharedViewModel.moveMapToLocation(state.loos[it].location)
                }
                withModelsFrom(state.loos) {
                    LooModel_()
                        .id(it.id)
                        .loo(it)
                }
            }
        })
        filterRecyclerView.setController(simpleController(sharedViewModel) { state ->
            carousel {
                id("filterCarousel")
                padding(Carousel.Padding.dp(24, 8, 24, 8, 8))
                setDefaultGlobalSnapHelperFactory(null)

                withModelsFrom(state.filters) {
                    FilterModel_()
                        .id(it.name)
                        .filter(it)
                        .onFilterCallback { filter, b -> sharedViewModel.updateFilter(filter, b) }
                }
            }
        })
        filterRecyclerView.requestModelBuild()
        looRecyclerView.requestModelBuild()
        looRecyclerView.visibility = View.INVISIBLE

        // this is jank
        sharedViewModel.selectedLooIndex.observeForever { index ->
            index?.let {
                if (looRecyclerView.childCount != 0) {
                    looRecyclerView.visibility = View.VISIBLE
                    (looRecyclerView[0] as RecyclerView).scrollToPosition(it)
                }
            }
        }

        return homeBinding.root
    }

    @SuppressLint("ResourceType")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

//        val myLocationButton = mapFragment?.requireView()?.findViewById<View>(2)
//
//        if (myLocationButton != null && myLocationButton.layoutParams is RelativeLayout.LayoutParams) {
//            // location button is inside of RelativeLayout
//            val params = myLocationButton.layoutParams as RelativeLayout.LayoutParams
//
//            // Align it to - parent BOTTOM|LEFT
//            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
//            params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
//            params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE)
//
//            // TODO: adjust for insets here
//            params.setMargins(0, view.dpToPx(150f), 0, 0)
//            myLocationButton.layoutParams = params
//        }


        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(activity as Activity)

        homeBinding.searchBarEdittext.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) {
                    homeBinding.searchBarEdittext.clearFocus()
                    adapter.setPredictions(emptyList())
                }
                handler.postDelayed({ getPlacePredictions(s.toString()) }, 300)
            }

        })

        homeBinding.searchBarEdittext.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                homeBinding.filterRecyclerView.visibility = View.GONE
            } else {
                homeBinding.searchBarEdittext.setText("")
                homeBinding.filterRecyclerView.visibility = View.VISIBLE
            }
        }

        homeBinding.backButton.setOnClickListener {
            hideDetailElements()
        }

        setUpFabMenu()
        initRecyclerView()
        setupFocusListenersUI(homeBinding.root)
    }

    override fun onMapReady(map: GoogleMap) {
        // remove the direction buttons
        map.uiSettings.isMapToolbarEnabled = false
        map.uiSettings.isZoomControlsEnabled = false
        //map.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.greyscale_map))


        // add markers for each
        sharedViewModel.nearbyLoos.observeForever { loos ->
            map.let { map ->
                loos.forEach { loo ->
                    val marker = map.addMarker(
                        MarkerOptions()
                            .position(loo.location)
                            .icon(
                                BitmapDescriptorFactory.fromBitmap(
                                    ResourcesCompat.getDrawable(
                                        resources,
                                        R.drawable.ic_toilet_paper,
                                        context?.theme
                                    )?.toBitmap()
                                )
                            )
                    )
                    marker.tag = loo
                }
            }
        }

        checkFineLocation(requireContext())
        sharedViewModel.setMap(map)
        getDeviceLocation()

        // Set a listener for marker click.
        map.setOnMarkerClickListener(this)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        when (requestCode) {
            RequestCodes.PERMISSIONS_RC_LOCATION.code -> {
                getDeviceLocation()
            }
        }
    }


    private fun getDeviceLocation() {
        try {
            fusedLocationProviderClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    location?.let {
                        sharedViewModel.setLastKnownLocation(it)
                    }
                    sharedViewModel.updateMap()
                }
        } catch (e: SecurityException) {
            Timber.e("Exception: ${e.message}")
        }
    }

    private fun initRecyclerView() {
        val recyclerView = homeBinding.recyclerView
        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(DividerItemDecoration(context, layoutManager.orientation))
        adapter.onPlaceClickListener = { onPlaceClicked(it) }
    }


    private fun setUpFabMenu() {
        homeBinding.createFab.setOnClickListener {
            checkBackStack()
            findNavController().navigate(HomeFragmentDirections.actionHomeFragmentToCreateFragmentV2())
        }
    }

    private fun checkBackStack() {
        if (findNavController().currentDestination?.id != R.id.homeFragment) {
            findNavController().popBackStack()
        }
    }


    override fun onMarkerClick(p0: Marker?): Boolean {
        p0?.let {marker ->
            val loo = marker.tag as Loo
            showDetailElements()
            sharedViewModel.setCurrentLooIndex(loo)
            sharedViewModel.moveMapToLocation(loo.location)
            //Toast.makeText(context, "test-marker-description: ${(marker.tag as Loo).description}", Toast.LENGTH_SHORT).show()
        }
        return true
    }

    /**
     * This method demonstrates the programmatic approach to getting place predictions. The
     * parameters in this request are currently biased to Kolkata, India.
     *
     * @param query the plus code query string (e.g. "GCG2+3M K")
     */
    private fun getPlacePredictions(query: String) {
        // The value of 'bias' biases prediction results to the rectangular region provided
        // (currently Seattle). Modify these values to get results for another area. Make sure to
        // pass in the appropriate value/s for .setCountries() in the
        // FindAutocompletePredictionsRequest.Builder object as well.
        // 47.60357° N, -122.32945° E
        val bias: LocationBias = RectangularBounds.newInstance(
            LatLng(47.458744, -122.208162),  // SW lat, lng
            LatLng(47.830671, -122.524896) // NE lat, lng
        )

        // Create a new programmatic Place Autocomplete request in Places SDK for Android
        val newRequest = FindAutocompletePredictionsRequest
            .builder()
            .setSessionToken(sessionToken)
            .setLocationBias(bias)
            .setTypeFilter(TypeFilter.ESTABLISHMENT)
            .setQuery(query)
            .setCountry("US")
            .build()

        // Perform autocomplete predictions request
        placesClient.findAutocompletePredictions(newRequest).addOnSuccessListener { response ->
            val predictions = response.autocompletePredictions
            adapter.setPredictions(predictions)
        }.addOnFailureListener { exception: Exception? ->
            if (exception is ApiException) {
                Timber.e("Place not found: %s", exception.statusCode)
            }
        }
    }

    override fun onPlaceClicked(place: AutocompletePrediction) {
        Toast.makeText(context, "Clicked something!", Toast.LENGTH_SHORT).show()
        // Construct the request URL
        val apiKey = getString(R.string.google_maps_key)
        val requestURL =
            "https://maps.googleapis.com/maps/api/geocode/json?place_id=${place.placeId}&key=$apiKey"

        Timber.i("TEST CLICKING ON A PLACE REQUEST!")
        // Use the HTTP request URL for Geocoding API to get geographic coordinates for the place
        val request = JsonObjectRequest(Request.Method.GET, requestURL, null, { response ->
            try {
                Timber.i("TEST SUCCESS REQUEST!")
                // Inspect the value of "results" and make sure it's not empty
                val results: JSONArray = response.getJSONArray("results")
                if (results.length() == 0) {
                    Timber.w("No results from geocoding request.")
                    return@JsonObjectRequest
                }

                // Use Gson to convert the response JSON object to a POJO
                val result: GeocodingResult = gson.fromJson(results.getString(0), GeocodingResult::class.java)

                homeBinding.searchBar.clearFocus()
                adapter.setPredictions(emptyList())

                result.geometry?.location?.let {
                    sharedViewModel.moveMapToLocation(it)
                }
            } catch (e: JSONException) {
                Timber.i("TEST FAIL REQUEST!")
                e.printStackTrace()
            }
        }, { error ->
            Timber.e("Request failed $error")
        })

        // Add the request to the Request queue.
        queue.add(request)
    }



    fun setupFocusListenersUI(view: View) {
        // Set up touch listener for non-text box views to hide keyboard.
        if (view !is EditText) {
            view.setOnTouchListener { _, _ ->
                hideKeyboard()
                if(homeBinding.searchBarEdittext.text.isNullOrEmpty()) {
                    homeBinding.searchBar.clearFocus()
                    adapter.setPredictions(emptyList())
                }
                false
            }
        }

        //If a layout container, iterate over children and seed recursion.
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val innerView = view.getChildAt(i)
                setupFocusListenersUI(innerView)
            }
        }
    }

    override fun onCheckedChanged(button: MaterialButton?, isChecked: Boolean) {
        button?.let {
            sharedViewModel.updateFilters(button.text, isChecked)
        }
    }

    override fun onMyLocationClick(location: Location) {
        Toast.makeText(context, "Current location:\n$location", Toast.LENGTH_LONG).show()
    }

    override fun onMyLocationButtonClick(): Boolean {
        Toast.makeText(context, "MyLocation button clicked", Toast.LENGTH_SHORT).show()
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false
    }

    private fun showDetailElements() {
        looRecyclerView.visibility = View.VISIBLE
        filterRecyclerView.visibility = View.INVISIBLE
        homeBinding.searchBarCard.visibility = View.INVISIBLE
        homeBinding.createFab.visibility = View.INVISIBLE
        homeBinding.backButton.visibility = View.VISIBLE
    }

    private fun hideDetailElements() {
        looRecyclerView.visibility = View.INVISIBLE
        filterRecyclerView.visibility = View.VISIBLE
        homeBinding.searchBarCard.visibility = View.VISIBLE
        homeBinding.createFab.visibility = View.VISIBLE
        homeBinding.backButton.visibility = View.INVISIBLE
    }

    override fun invalidate() {
        filterRecyclerView.requestModelBuild()
        looRecyclerView.requestModelBuild()
    }
}
