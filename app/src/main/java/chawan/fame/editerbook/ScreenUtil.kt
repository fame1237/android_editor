package chawan.fame.editerbook

import android.content.res.Resources
import android.util.TypedValue

object ScreenUtil{

    fun dpToPx(valueInDp: Float): Int {
        val metrics = Contextor.getContext()!!.resources.displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics).toInt()
    }

    fun pxToDp(px: Int): Int {
        return (px / Resources.getSystem().displayMetrics.density).toInt()
    }
}