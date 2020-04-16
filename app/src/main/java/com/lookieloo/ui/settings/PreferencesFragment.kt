package com.lookieloo.ui.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import com.lookieloo.R

class PreferencesFragment : PreferenceFragmentCompat() {
	override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
		setPreferencesFromResource(R.xml.user_preferences, rootKey)
	}
}
