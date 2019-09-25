package com.example.storylog_editor

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue
import android.text.style.LeadingMarginSpan
import android.text.SpannableString


object ScreenUtil {

    fun dpToPx(valueInDp: Float,context: Context): Int {
        val metrics = context.resources.displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics).toInt()
    }

    fun pxToDp(px: Int): Int {
        return (px / Resources.getSystem().displayMetrics.density).toInt()
    }

    fun createIndentedText(
        text: CharSequence,
        marginFirstLine: Int,
        marginNextLines: Int
    ): SpannableString {
        val result = SpannableString(text)
        result.setSpan(
            LeadingMarginSpan.Standard(marginFirstLine, marginNextLines),
            0,
            text.length,
            0
        )
        return result
    }
}