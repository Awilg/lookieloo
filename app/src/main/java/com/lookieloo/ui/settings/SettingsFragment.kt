package com.lookieloo.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.lookieloo.R
import com.lookieloo.databinding.FragmentSettingsBinding

class SettingsFragment : Fragment() {

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		val binding = FragmentSettingsBinding.inflate(inflater)

		if (savedInstanceState == null) {
			fragmentManager?.beginTransaction()?.replace(R.id.settings, PreferencesFragment())
				?.commit()
		}

		// This callback will only be called when MyFragment is at least Started.
		requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,
			object : OnBackPressedCallback(true) {
				override fun handleOnBackPressed() {
					fragmentManager?.popBackStackImmediate()
				}
			})



		return binding.root
	}
}
