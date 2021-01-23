package com.lookieloo.ui.create

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.airbnb.epoxy.*
import com.google.android.material.textfield.TextInputEditText
import com.lookieloo.R

@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
class CreateTextInputRow @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {


    private val editText by lazy { findViewById<TextInputEditText>(R.id.create_text_section_input) }
    private val editTextWatcher = SimpleTextWatcher { onEditTextChanged?.invoke(it) }

    @set:TextProp
    var text: CharSequence? = null
    @set:TextProp
    var hint: CharSequence? = null

    @set:CallbackProp
    var onEditTextChanged: ((newText: String) -> Unit)? = null

    init {
        inflate(context, R.layout.e_text_input_row, this)
    }

    @AfterPropsSet
    fun postBindSetup() {
        editText.also {
            it.setTextIfDifferent(text)
            it.hint = hint
            it.removeTextChangedListener(editTextWatcher)
            it.addTextChangedListener(editTextWatcher)
        }
    }

    @OnViewRecycled
    fun onViewRecycled() {
        editText.removeTextChangedListener(editTextWatcher)
    }

}