package com.lookieloo.ui.model

import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import com.lookieloo.R
import com.lookieloo.model.Loo
import com.lookieloo.ui.shared.KotlinEpoxyHolder

@EpoxyModelClass(layout = R.layout.e_loo_detail)
abstract class LooModel : EpoxyModelWithHolder<LooModel.Holder>() {

    @EpoxyAttribute(EpoxyAttribute.Option.DoNotHash)
    var onReport: (() -> Unit)? = null

    @EpoxyAttribute
    var loo: Loo? = null

    override fun bind(holder: Holder) {
        loo?.let {
            holder.title.text = it.title
            holder.description.text = it.description
            holder.reportBtn.text = "Report"

            it.filters?.let { filters ->
                filters.forEach { filter ->
                    if (filter.enabled) {
                        when (filter.type) {
                            FilterType.Accessible -> holder.handicapBtn.isChecked = true
                            FilterType.Baby -> holder.babyBtn.isChecked = true
                            FilterType.Clean -> holder.cleanBtn.isChecked = true
                            FilterType.Genderless -> holder.genderlessBtn.isChecked = true
                            FilterType.Public -> holder.publicBtn.isChecked = true

                        }
                    }
                }
            }

            holder.cleanBtn.isEnabled = false
            holder.babyBtn.isEnabled = false
            holder.handicapBtn.isEnabled = false
            holder.genderlessBtn.isEnabled = false
            holder.publicBtn.isEnabled = false
        }
    }

    override fun unbind(holder: Holder) {}

    class Holder : KotlinEpoxyHolder() {
        val title by bind<MaterialTextView>(R.id.loo_title)
        val description by bind<MaterialTextView>(R.id.loo_description)
        val reportBtn by bind<MaterialButton>(R.id.report_button)
        val cleanBtn by bind<MaterialButton>(R.id.filter_clean)
        val babyBtn by bind<MaterialButton>(R.id.filter_baby)
        val handicapBtn by bind<MaterialButton>(R.id.filter_handicap)
        val genderlessBtn by bind<MaterialButton>(R.id.filter_mixed_gender)
        val publicBtn by bind<MaterialButton>(R.id.filter_public)
    }
}
