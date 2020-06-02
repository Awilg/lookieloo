package com.lookieloo.ui.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.lookieloo.databinding.FragmentCreateBinding

class CreateFragment : Fragment() {

	private lateinit var createViewModel: CreateViewModel

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		createViewModel = ViewModelProviders.of(this).get(CreateViewModel::class.java)
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? {
		val binding = FragmentCreateBinding.inflate(inflater)
		return binding.root
	}
}
