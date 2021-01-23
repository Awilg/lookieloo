package com.lookieloo.ui.model

import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.google.android.material.button.MaterialButton
import com.lookieloo.R
import com.lookieloo.utils.KotlinEpoxyHolder


@EpoxyModelClass(layout = R.layout.e_filter)
abstract class FilterModel : EpoxyModelWithHolder<FilterModel.Holder>() {

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    var onFilterCallback: ((filter: Filter, enabled: Boolean) -> Unit)? = null

    @EpoxyAttribute
    var filter: Filter? = null

    override fun bind(holder: Holder) {
        filter?.let {
            holder.filterBtn.isChecked = it.enabled
            holder.filterBtn.text = it.name
            holder.filterBtn.addOnCheckedChangeListener { _, isChecked ->
                onFilterCallback?.invoke(it, isChecked)
            }


            when (it.type) {
                FilterType.Accessible -> {
                    holder.filterBtn.setIconResource(R.drawable.ic_icons8_assistive_technology_50)
                }
                FilterType.Baby -> {
                    holder.filterBtn.setIconResource(R.drawable.ic_icons8_stroller_50)
                }
                FilterType.Clean -> {
                    holder.filterBtn.setIconResource(R.drawable.ic_icons8_broom_50)
                }
                FilterType.Genderless -> {
                    holder.filterBtn.setIconResource(R.drawable.ic_icons8_toilet_50)
                }
                FilterType.Public -> {
                    holder.filterBtn.setIconResource(R.drawable.ic_icons8_padlock_50)
                }
            }
        }

    }

    override fun unbind(holder: Holder) {
        holder.filterBtn.clearOnCheckedChangeListeners()
    }

    class Holder : KotlinEpoxyHolder() {
        val filterBtn by bind<MaterialButton>(R.id.filter)
    }
}