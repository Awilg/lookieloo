package com.lookieloo.ui.filter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.button.MaterialButton
import com.lookieloo.databinding.FragmentFilterBinding
import com.lookieloo.ui.home.SharedViewModel

class FilterFragment : Fragment(), MaterialButton.OnCheckedChangeListener {
    private val sharedViewModel: SharedViewModel by activityViewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentFilterBinding.inflate(inflater)

        return binding.root
    }

    override fun onCheckedChanged(button: MaterialButton?, isChecked: Boolean) {
        button?.let {
            sharedViewModel.updateFilters(button.text, isChecked)
        }
    }
}
