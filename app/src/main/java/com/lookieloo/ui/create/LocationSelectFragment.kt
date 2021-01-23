package com.lookieloo.ui.create

import android.app.Activity
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.airbnb.mvrx.MavericksView
import com.airbnb.mvrx.activityViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.lookieloo.R
import com.lookieloo.databinding.FragmentCreateLocationSelectBinding
import com.lookieloo.utils.RequestCodes
import com.lookieloo.utils.checkFineLocation
import pub.devrel.easypermissions.EasyPermissions
import timber.log.Timber

class LocationSelectFragment : Fragment(), MavericksView, OnMapReadyCallback, EasyPermissions.PermissionCallbacks {

    private val viewModel: CreateViewModelV2 by activityViewModel()
    lateinit var binding: FragmentCreateLocationSelectBinding

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateLocationSelectBinding.inflate(inflater)
        binding.actionButtonSection.buttonText = getString(R.string.select_location_btn_text)
        binding.actionButtonSection.onclick = View.OnClickListener {
            viewModel.saveLocation()
            findNavController().navigate(
                LocationSelectFragmentDirections.actionLocationSelectFragmentToCreateFragmentV2()
            )
        }

        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(activity as Activity)
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        return binding.root
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        when (requestCode) {
            RequestCodes.PERMISSIONS_RC_LOCATION.code -> {
                Timber.i("TEST: Refreshing the map")
            }
        }
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        TODO("Not yet implemented")
    }

    override fun onMapReady(map: GoogleMap?) {
        checkFineLocation(requireContext())
        viewModel.setLocationSelectMap(map)
        getDeviceLocation()
    }

    private fun getDeviceLocation() {
        try {
            fusedLocationProviderClient.lastLocation
                .addOnSuccessListener { location : Location? ->
                    location?.let {
                        viewModel.setLastKnownLocation(it)
                        viewModel.updateLocationMap()
                    }
                }

        } catch (e: SecurityException) {
            Timber.e("Exception: ${e.message}")
        }
    }


    override fun invalidate() {}
}