package chawan.fame.editerbook.view

import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import androidx.core.content.ContextCompat
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
        var align: Int? = null
        var isIndent = false
        fun newInstance(source: View, align: Int?,isIndent:Boolean): SetAlignmentDialog {
            val fragment = SetAlignmentDialog()
            this.source = source
            this.align = align
            this.isIndent = isIndent
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog.window.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window.setDimAmount(0f)
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
        dialog.window.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
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


            alignLeft.setOnClickListener {
                (activity as OnClick).onClickAlignLeft()
                dismiss()
            }

            alignCenter.setOnClickListener {
                (activity as OnClick).onClickAlignCenter()
                dismiss()
            }

            alignRight.setOnClickListener {
                (activity as OnClick).onClickAlignRight()
                dismiss()
            }

            indent.setOnClickListener {
                (activity as OnClick).onClickIndent()
                dismiss()
            }

            if (!isIndent) {
                when (align) {
                    Gravity.START -> {
                        context?.let {
                            alignLeft.setColorFilter(
                                ContextCompat.getColor(it, R.color.colorOrange)
                                , PorterDuff.Mode.SRC_IN
                            )
                            alignCenter.setColorFilter(
                                ContextCompat.getColor(it, R.color.grey)
                                , PorterDuff.Mode.SRC_IN
                            )

                            alignRight.setColorFilter(
                                ContextCompat.getColor(it, R.color.grey)
                                , PorterDuff.Mode.SRC_IN
                            )
                            indent.setColorFilter(
                                ContextCompat.getColor(it, R.color.grey)
                                , PorterDuff.Mode.SRC_IN
                            )
                        }
                    }
                    Gravity.CENTER -> {
                        context?.let {
                            alignLeft.setColorFilter(
                                ContextCompat.getColor(it, R.color.grey)
                                , PorterDuff.Mode.SRC_IN
                            )
                            alignCenter.setColorFilter(
                                ContextCompat.getColor(it, R.color.colorOrange)
                                , PorterDuff.Mode.SRC_IN
                            )

                            alignRight.setColorFilter(
                                ContextCompat.getColor(it, R.color.grey)
                                , PorterDuff.Mode.SRC_IN
                            )
                            indent.setColorFilter(
                                ContextCompat.getColor(it, R.color.grey)
                                , PorterDuff.Mode.SRC_IN
                            )
                        }
                    }
                    Gravity.END -> {
                        context?.let {
                            alignLeft.setColorFilter(
                                ContextCompat.getColor(it, R.color.grey)
                                , PorterDuff.Mode.SRC_IN
                            )
                            alignCenter.setColorFilter(
                                ContextCompat.getColor(it, R.color.grey)
                                , PorterDuff.Mode.SRC_IN
                            )

                            alignRight.setColorFilter(
                                ContextCompat.getColor(it, R.color.colorOrange)
                                , PorterDuff.Mode.SRC_IN
                            )
                            indent.setColorFilter(
                                ContextCompat.getColor(it, R.color.grey)
                                , PorterDuff.Mode.SRC_IN
                            )
                        }
                    }
                    else -> {
                        context?.let {
                            alignLeft.setColorFilter(
                                ContextCompat.getColor(it, R.color.grey)
                                , PorterDuff.Mode.SRC_IN
                            )
                            alignCenter.setColorFilter(
                                ContextCompat.getColor(it, R.color.grey)
                                , PorterDuff.Mode.SRC_IN
                            )

                            alignRight.setColorFilter(
                                ContextCompat.getColor(it, R.color.grey)
                                , PorterDuff.Mode.SRC_IN
                            )
                            indent.setColorFilter(
                                ContextCompat.getColor(it, R.color.grey)
                                , PorterDuff.Mode.SRC_IN
                            )
                        }
                    }
                }
            }
            else{
                context?.let {
                    alignLeft.setColorFilter(
                        ContextCompat.getColor(it, R.color.grey)
                        , PorterDuff.Mode.SRC_IN
                    )
                    alignCenter.setColorFilter(
                        ContextCompat.getColor(it, R.color.grey)
                        , PorterDuff.Mode.SRC_IN
                    )

                    alignRight.setColorFilter(
                        ContextCompat.getColor(it, R.color.grey)
                        , PorterDuff.Mode.SRC_IN
                    )
                    indent.setColorFilter(
                        ContextCompat.getColor(it, R.color.colorOrange)
                        , PorterDuff.Mode.SRC_IN
                    )
                }
            }

        } catch (ex: Exception) {
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
        params.y = sourceY - ScreenUtil.dpToPx(100f) // above source view
        window.attributes = params
    }


    interface OnClick {
        fun onClickAlignLeft()
        fun onClickAlignCenter()
        fun onClickAlignRight()
        fun onClickIndent()
    }

    class Builder {

        fun build(source: View, align: Int?, isIndent: Boolean): SetAlignmentDialog {
            val fragment = SetAlignmentDialog.newInstance(source, align, isIndent)
            return fragment
        }
    }

}