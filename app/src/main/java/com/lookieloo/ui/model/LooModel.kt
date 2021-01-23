package com.lookieloo.ui.model

import com.airbnb.epoxy.EpoxyAttribute
import com.airbnb.epoxy.EpoxyModelClass
import com.airbnb.epoxy.EpoxyModelWithHolder
import com.google.android.material.textview.MaterialTextView
import com.lookieloo.R
import com.lookieloo.model.Loo
import com.lookieloo.utils.KotlinEpoxyHolder

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
        }
    }

    override fun unbind(holder: Holder) {}

    class Holder : KotlinEpoxyHolder() {
        val title by bind<MaterialTextView>(R.id.loo_title)
        val description by bind<MaterialTextView>(R.id.loo_description)
    }
}
