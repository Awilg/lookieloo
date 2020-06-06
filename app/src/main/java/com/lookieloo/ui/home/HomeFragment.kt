package com.lookieloo.ui.home

import android.Manifest
import android.app.Activity
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.lookieloo.R
import com.lookieloo.model.Loo
import com.lookieloo.utils.RequestCodes
import kotlinx.android.synthetic.main.fragment_home.*
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.PermissionRequest
import timber.log.Timber


class HomeFragment : Fragment(), OnMapReadyCallback,
    GoogleMap.OnMarkerClickListener,
    EasyPermissions.PermissionCallbacks {

    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    private var isFABOpen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

        fab_menu.setOnClickListener {
            Toast.makeText(context, "Menu opened!", Toast.LENGTH_LONG).show()

            if(!isFABOpen){
                showFABMenu();
            }else{
                closeFABMenu();
            }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        // remove the direction buttons
        map.uiSettings.isMapToolbarEnabled = false
        map.uiSettings.isZoomControlsEnabled = false
        map.setMapStyle(MapStyleOptions.loadRawResourceStyle(context, R.raw.greyscale_map))
        sharedViewModel.setMap(map)

        // add markers for each
        sharedViewModel.nearbyLoos.observeForever { loos ->
            map.let { map ->
                loos.forEach { loo ->
                    val marker = map.addMarker(
                        MarkerOptions()
                            .position(loo.location)
                            .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher_foreground))
                    )
                    marker.tag = loo
                }
            }
        }
        checkLocationPermission()
        getDeviceLocation()

        // Set a listener for marker click.
        map.setOnMarkerClickListener(this);
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
                    sharedViewModel.setLastKnownLocation(task.result as Location)
                } else {
                    Timber.d("Current location is null. Using defaults.")
                    Timber.e("Exception: ${task.exception}")
                    sharedViewModel.resetMap()
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

    private fun showFABMenu() {
        isFABOpen=true
        settings_fab.animate().translationY(-getResources().getDimension(R.dimen.standard_55))
        create_fab.animate().translationY(-getResources().getDimension(R.dimen.standard_105))
        filter_fab.animate().translationY(-getResources().getDimension(R.dimen.standard_155))
    }

    private fun closeFABMenu() {
        isFABOpen=false
        filter_fab.animate().translationY(0f)
        settings_fab.animate().translationY(0f)
        create_fab.animate().translationY(0f)
    }

    override fun onMarkerClick(p0: Marker?): Boolean {
        p0?.let {marker ->
            Toast.makeText(context, "test-marker-description: ${(marker.tag as Loo).description}", Toast.LENGTH_SHORT).show()
        }
        return true
    }
}
