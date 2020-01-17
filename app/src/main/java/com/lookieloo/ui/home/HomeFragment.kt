package com.lookieloo.ui.home

import android.Manifest
import android.app.Activity
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.lookieloo.R
import com.lookieloo.utils.RequestCodes
import kotlinx.android.synthetic.main.fragment_home.*
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.PermissionRequest
import timber.log.Timber


class HomeFragment : Fragment(), OnMapReadyCallback,
    EasyPermissions.PermissionCallbacks {

    private lateinit var homeViewModel: HomeViewModel
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private lateinit var detailBottomSheetBehavior: BottomSheetBehavior<FrameLayout>
    private lateinit var searchBottomSheetBehavior: BottomSheetBehavior<FrameLayout>
    private lateinit var createBottomSheetBehavior: BottomSheetBehavior<FrameLayout>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(activity as Activity)

        createBottomSheetBehavior = BottomSheetBehavior.from(createLooBottomSheet)
        createBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN

        searchBottomSheetBehavior = BottomSheetBehavior.from(searchBottomSheet)

        detailBottomSheetBehavior = BottomSheetBehavior.from(detailBottomSheet)
        detailBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        detailBottomSheetBehavior.isFitToContents = true

        search_places.setOnQueryTextFocusChangeListener { _, _ ->
            searchBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }

//        homeViewModel.lastKnownLocation.observe(this, Observer {
//            homeViewModel.
//        })

//        search_places.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String?): Boolean {
//            }
//
//            override fun onQueryTextChange(newText: String?): Boolean {
//            }
//        })
    }

    override fun onMapReady(map: GoogleMap) {
        // Add the adapter for the custom markers
        map.setOnMarkerClickListener {
            createBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            detailBottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
            true
        }

        map.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.greyscale_map))
        homeViewModel.setMap(map)
        checkLocationPermission()
        getDeviceLocation()
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
            val locationResult = fusedLocationProviderClient.lastLocation
            locationResult.addOnCompleteListener(activity as Activity) { task ->
                if (task.isSuccessful) {
                    homeViewModel.setLastKnownLocation(task.result as Location)
                } else {
                    Timber.d("Current location is null. Using defaults.")
                    Timber.e("Exception: ${task.exception}")
                    homeViewModel.resetMap()
                }
            }
        } catch (e: SecurityException) {
            Timber.e("Exception: ${e.message}")
        }
    }

    private fun checkLocationPermission() {
        if (!EasyPermissions.hasPermissions(context!!, Manifest.permission.CAMERA)) {
            EasyPermissions.requestPermissions(
                PermissionRequest.Builder(
                    this, RequestCodes.PERMISSIONS_RC_LOCATION.code,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ).build()
            )
        }
    }
}
