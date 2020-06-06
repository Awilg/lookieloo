package com.lookieloo

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.libraries.places.api.Places

class MainActivity : AppCompatActivity(),
    PreferenceFragmentCompat.OnPreferenceStartFragmentCallback{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize the places SDK
        Places.initialize(applicationContext, resources.getString(R.string.google_maps_key))

        // Create a new Places client instance
        val placesClient = Places.createClient(this)

        hideSystemUI()
    }

    override fun onPreferenceStartFragment(
        caller: PreferenceFragmentCompat,
        pref: Preference
    ): Boolean {
        // Instantiate the new Fragment
        val args = pref.extras
        // Need to get the fragment manager of the child and not the nav host
        val fragManager = supportFragmentManager.primaryNavigationFragment!!.childFragmentManager
        val fragment = fragManager.fragmentFactory.instantiate(classLoader, pref.fragment).apply {
            arguments = args
            setTargetFragment(caller, 0)
        }
        // Replace the existing Fragment with the new Fragment
        fragManager.beginTransaction()
            .replace(R.id.settings, fragment)
            .addToBackStack(null)
            .commit()
        title = pref.title
        return true
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
