package com.lookieloo.ui.model

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.AfterPropsSet
import com.airbnb.epoxy.CallbackProp
import com.airbnb.epoxy.Carousel
import com.airbnb.epoxy.ModelView


@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
class CustomSnappingCarousel @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : Carousel(context, attrs, defStyleAttr) {

    var selectedPosition = RecyclerView.NO_POSITION

    @set:CallbackProp
    var snapHelperCallback: ((Int) -> Unit)? = null

    @AfterPropsSet
    fun postBindSetup() {
        snapHelperCallback?.let {
            val snapHelper = CustomLinearSnapHelper(it)
            //workaround - do not remove
            //https://stackoverflow.com/questions/44043501/an-instance-of-onflinglistener-already-set-in-recyclerview/52850198
            this.onFlingListener = null
            snapHelper.attachToRecyclerView(this)
        }
    }

    inner class CustomLinearSnapHelper(private val callback: (Int) -> Unit) : LinearSnapHelper() {

        override fun findSnapView(layoutManager: LayoutManager): View? {
            val view = super.findSnapView(layoutManager)

            if (view != null) {
                val newPosition = layoutManager.getPosition(view)
                if (newPosition != selectedPosition) {
                    callback.invoke(newPosition)
                    selectedPosition = newPosition
                }
            }
            return view
        }
    }
}