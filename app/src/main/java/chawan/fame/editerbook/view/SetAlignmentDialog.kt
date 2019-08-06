package chawan.fame.editerbook.view

import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.core.content.ContextCompat
import android.view.*
import androidx.fragment.app.DialogFragment
import chawan.fame.editerbook.R
import chawan.fame.editerbook.ScreenUtil
import kotlinx.android.synthetic.main.popup_alignment.*


/**
 * Created by fame on 8/2/2018 AD.
 */


class SetAlignmentDialog : DialogFragment() {

    var sourceX = 0
    var sourceY = 0

    companion object {
        var source: View? = null
        fun newInstance(source: View): SetAlignmentDialog {
            val fragment = SetAlignmentDialog()
            this.source = source
            return fragment
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog.window.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.window.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        setDialogPosition()
        return inflater.inflate(R.layout.popup_alignment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupView()
    }

    override fun onStart() {
        super.onStart()
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun setupView() {
        try {
            val wm = context?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val display = wm.defaultDisplay
            val size = Point()
            display.getSize(size)

            val displayMetrics = context?.resources?.displayMetrics
            val dpHeight = displayMetrics!!.heightPixels / displayMetrics.density
            val dpWidth = displayMetrics.widthPixels / displayMetrics.density

            ScreenUtil.dpToPx(dpWidth - 270F)
            bubble.arrowPosition = sourceX - ScreenUtil.dpToPx(dpWidth - 270F).toFloat() + ScreenUtil.dpToPx(12.5f)

            alignLeft.setOnClickListener {
                (activity as OnClick).onClickAlignLeft()
            }

            alignCenter.setOnClickListener {
                (activity as OnClick).onClickAlignCenter()
            }

            alignRight.setOnClickListener {
                (activity as OnClick).onClickAlignRight()
            }

            indent.setOnClickListener {
                (activity as OnClick).onClickIndent()
            }


        } catch (ex: Exception) {
            bubble.arrowPosition = 120f
        }
    }

    private fun setDialogPosition() {
        if (source == null) {
            return  // Leave the dialog in default position
        }
        val location = IntArray(2)
        source!!.getLocationOnScreen(location)
        sourceX = location[0]
        sourceY = location[1]

        val window = dialog.window
        window!!.setGravity(Gravity.TOP or Gravity.LEFT)
        val params = window.attributes
        params.x = sourceX  // about half of confirm button size left of source view
        params.y = sourceY -  ScreenUtil.dpToPx(100f) // above source view
        window.attributes = params
    }


    interface OnClick {
        fun onClickAlignLeft()
        fun onClickAlignCenter()
        fun onClickAlignRight()
        fun onClickIndent()
    }

    class Builder {

        fun build(source: View): SetAlignmentDialog {
            val fragment = SetAlignmentDialog.newInstance(source)
            return fragment
        }
    }

}