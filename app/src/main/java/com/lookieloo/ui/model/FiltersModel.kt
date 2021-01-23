package com.lookieloo.ui.model

import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.google.android.material.button.MaterialButton
import com.lookieloo.R
import com.lookieloo.utils.KotlinEpoxyHolder

@EpoxyModelClass(layout = R.layout.button_filters)
abstract class FiltersModel : EpoxyModelWithHolder<FiltersModel.Holder>() {

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    var onFilterCallback: ((filter: Filter, enabled: Boolean) -> Unit)? = null

    @EpoxyAttribute
    var filters: List<Filter>? = emptyList()

    override fun bind(holder: Holder) {

        filters?.forEach {

            when (it.type) {
                FilterType.Accessible -> {
                    holder.handicapBtn.isChecked = it.enabled
                    holder.handicapBtn.addOnCheckedChangeListener { _, isChecked ->
                        onFilterCallback?.invoke(
                            it,
                            isChecked
                        )
                    }
                }
                FilterType.Baby -> {
                    holder.babyBtn.isChecked = it.enabled
                    holder.babyBtn.addOnCheckedChangeListener { _, isChecked ->
                        onFilterCallback?.invoke(
                            it,
                            isChecked
                        )
                    }
                }
                FilterType.Clean -> {
                    holder.cleanBtn.isChecked = it.enabled
                    holder.cleanBtn.addOnCheckedChangeListener { _, isChecked ->
                        onFilterCallback?.invoke(
                            it,
                            isChecked
                        )
                    }
                }
                FilterType.Genderless -> {
                    holder.genderlessBtn.isChecked = it.enabled
                    holder.genderlessBtn.addOnCheckedChangeListener { _, isChecked ->
                        onFilterCallback?.invoke(
                            it,
                            isChecked
                        )
                    }
                }
                FilterType.Public -> {
                    holder.publicBtn.isChecked = it.enabled
                    holder.publicBtn.addOnCheckedChangeListener { _, isChecked ->
                        onFilterCallback?.invoke(
                            it,
                            isChecked
                        )
                    }
                }
            }
        }
    }

    override fun unbind(holder: Holder) {
        holder.cleanBtn.clearOnCheckedChangeListeners()
        holder.babyBtn.clearOnCheckedChangeListeners()
        holder.handicapBtn.clearOnCheckedChangeListeners()
        holder.genderlessBtn.clearOnCheckedChangeListeners()
        holder.publicBtn.clearOnCheckedChangeListeners()
    }

    class Holder : KotlinEpoxyHolder() {
        val cleanBtn by bind<MaterialButton>(R.id.filter_clean)
        val babyBtn by bind<MaterialButton>(R.id.filter_baby)
        val handicapBtn by bind<MaterialButton>(R.id.filter_handicap)
        val genderlessBtn by bind<MaterialButton>(R.id.filter_mixed_gender)
        val publicBtn by bind<MaterialButton>(R.id.filter_public)
    }
}
