package com.lookieloo.ui.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.lookieloo.databinding.FragmentCreateBinding
import com.lookieloo.ui.home.SharedViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class CreateFragment : Fragment() {

	private val sharedViewModel: SharedViewModel by activityViewModels()
	// Create a Coroutine scope using a job to be able to cancel when needed
	private var viewModelJob = Job()

	// the Coroutine runs using the Main (UI) dispatcher
	private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		val binding = FragmentCreateBinding.inflate(inflater)

		binding.createLooButton.setOnClickListener {
			Toast.makeText(context, "Loo created!", Toast.LENGTH_SHORT).show()
			sharedViewModel.createLoo(binding.createLooDescription.text.toString())
		}
		return binding.root
	}
}
