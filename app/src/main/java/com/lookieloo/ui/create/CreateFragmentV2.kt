package com.lookieloo.ui.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.airbnb.epoxy.EpoxyRecyclerView
import com.airbnb.mvrx.MavericksView
import com.airbnb.mvrx.activityViewModel
import com.lookieloo.R
import com.lookieloo.backButton
import com.lookieloo.buttonOutlined
import com.lookieloo.createSectionHeader
import com.lookieloo.databinding.FragmentCreateV2Binding
import com.lookieloo.utils.simpleController

class CreateFragmentV2 : Fragment(), MavericksView {

    private lateinit var recyclerView: EpoxyRecyclerView

    private val viewModel: CreateViewModelV2 by activityViewModel()
    lateinit var binding: FragmentCreateV2Binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateV2Binding.inflate(inflater)
        recyclerView = binding.recyclerView
        recyclerView.setController(simpleController(viewModel) { state ->

            backButton {
                id("back")
            }
            createSectionHeader {
                id("titleHeader")
                title("Title")
            }
            createTextInputRow {
                id("titleTextEdit")
                text(state.title)
                hint("Walmart on Pine and 15th")
                onEditTextChanged { viewModel.updateLooTitle(it) }
            }
            createSectionHeader {
                id("descriptionHeader")
                title("Description")
            }
            createTextInputRow {
                id("descTextEdit")
                text(state.description)
                hint("Clean bathroom in the back of walmart. No purchase necessary and good lighting.")
                onEditTextChanged { viewModel.updateLooDescription(it) }
            }
            createSectionHeader {
                id("locationHeader")
                title("Location")
            }
            buttonOutlined {
                id("selectOnMapBtn")
                buttonText("Select on map")
            }
            createSectionHeader {
                id("filterHeader")
                title("Filters")
            }
        })
        recyclerView.requestModelBuild()
        recyclerView.setItemSpacingDp(12)
        binding.actionButtonSection.buttonText = getString(R.string.create_action_btn_text)
        binding.actionButtonSection.onclick =
            View.OnClickListener { viewModel.saveLoo() }
        return binding.root
    }



    override fun invalidate() {
        recyclerView.requestModelBuild()
    }
}