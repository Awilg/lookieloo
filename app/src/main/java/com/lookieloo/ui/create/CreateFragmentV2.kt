package com.lookieloo.ui.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.airbnb.epoxy.EpoxyRecyclerView
import com.airbnb.mvrx.MavericksView
import com.airbnb.mvrx.activityViewModel
import com.lookieloo.*
import com.lookieloo.databinding.FragmentCreateV2Binding
import com.lookieloo.ui.model.filters
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
                onclick { _ -> findNavController().navigateUp() }
            }
            createSectionHeader {
                id("titleHeader")
                title("Title")
            }
            createTextInputRow {
                id("titleTextEdit")
                text(state.title)
                hint("e.g. Walmart on Pine and 15th")
                onEditTextChanged { viewModel.updateLooTitle(it) }
            }
            createSectionHeader {
                id("descriptionHeader")
                title("Description")
            }
            createTextInputRow {
                id("descTextEdit")
                text(state.description)
                hint("e.g. Clean bathroom in the back of walmart. No purchase necessary and good lighting.")
                onEditTextChanged { viewModel.updateLooDescription(it) }
            }
            createSectionHeader {
                id("locationHeader")
                title("Location")
            }
            state.location?.let {
                mapviewStatic {
                    id("mapview")
                    latlng(it)
                    mapsApiKey(getString(R.string.google_maps_key))
                }
            }
            buttonOutlined {
                id("selectOnMapBtn")
                buttonText("Select on map")
                onclick { _ ->
                    findNavController().navigate(
                        CreateFragmentV2Directions.actionCreateFragmentV2ToLocationSelectFragment()
                    )
                }
            }
            createSectionHeader {
                id("filterHeader")
                title("Filters")
            }
            filters {
                id("filters")
                filters(state.filters)
                onFilterCallback { filter, enabled -> viewModel.updateFilter(filter, enabled) }
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