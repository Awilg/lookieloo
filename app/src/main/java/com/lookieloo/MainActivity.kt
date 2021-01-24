package com.lookieloo

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.mvrx.Mavericks
import com.airbnb.mvrx.MavericksViewModelConfigFactory
import com.google.android.libraries.places.api.Places

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Mavericks
        Mavericks.initialize(this)
        Mavericks.viewModelConfigFactory = MavericksViewModelConfigFactory(applicationContext)

        // Initialize the places SDK
        Places.initialize(applicationContext, resources.getString(R.string.google_maps_key))

        // Create a new Places client instance
        val placesClient = Places.createClient(this)

        hideSystemUI()

        setContentView(R.layout.activity_main)
    }

    private fun hideSystemUI() {
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        window.decorView.systemUiVisibility = (
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
    }
}
