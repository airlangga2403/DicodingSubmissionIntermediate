package com.pa.submissionaplikasistoryapp.ui.custom_view

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.pa.submissionaplikasistoryapp.R

class MyButton : AppCompatButton {

    private lateinit var enableBackground: Drawable
    private lateinit var disableBackground: Drawable
    private var txtColor: Int = 0

    private var originalText: CharSequence = ""

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
        originalText = text // menyimpan nilai teks asli
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
        originalText = text // menyimpan nilai teks asli
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        background = if (isEnabled) enableBackground else disableBackground
        setTextColor(txtColor)
        textSize = 12f
        gravity = Gravity.CENTER
        text =
            if (isEnabled) originalText else context.getString(R.string.fill) // mengembalikan nilai teks asli jika tombol di-disable
    }

    private fun init() {
        txtColor = ContextCompat.getColor(context, android.R.color.background_light)
        enableBackground = ContextCompat.getDrawable(context, R.drawable.bg_button) as Drawable
        disableBackground =
            ContextCompat.getDrawable(context, R.drawable.bg_button_disable) as Drawable
    }
}
