package chawan.fame.editerbook.ui.editor

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.text.Editable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.TextWatcher
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.*
import android.view.accessibility.AccessibilityEvent
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.appcompat.widget.AppCompatEditText
import androidx.recyclerview.widget.RecyclerView
import chawan.fame.editerbook.R
import chawan.fame.editerbook.StyleCallback
import chawan.fame.editerbook.extension.filterGetIndex
import chawan.fame.editerbook.glide.GlideApp
import chawan.fame.editerbook.model.editor.EditerModel
import chawan.fame.editerbook.model.editor.EditerViewType
import chawan.fame.editerbook.model.editor.TextStyle
import chawan.fame.editerbook.util.CheckStyle
import chawan.fame.editerbook.util.ImageUtil
import chawan.fame.editerbook.view.EditTextSelectable
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions


/**
 * Created by fame on 14/2/2018 AD.
 */


class EditerAdapter(
    val context: Context,
    var listener: OnChange,
    var model: MutableList<EditerModel>
) : RecyclerView.Adapter<EditerAdapter.MyViewHolder>() {


    interface OnChange {
        fun onNextLine(position: Int, text: CharSequence)
        fun onPreviousLine(position: Int, text: CharSequence)
        fun onCursorChange(position: Int, startPosition: Int, endPosition: Int, edt: AppCompatEditText)
        fun onUpdateText(position: Int, text: CharSequence, updateStyle: Boolean)
        fun updateCursorPosition(position: Int, view: View)
        fun onUpdateBold()
        fun setShowBorderFalse(position: Int)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_editor, viewGroup, false)
        return MyViewHolder(itemView, MyCustomEditTextListener())
    }

    override fun onBindViewHolder(viewHolder: MyViewHolder, position: Int) {

        viewHolder.myCustomEditTextListener.updatePosition(model[position].id)

        if (model[position].viewType == EditerViewType.EDIT_TEXT) {
            viewHolder.layoutTextView.visibility = View.VISIBLE
            viewHolder.layoutImage.visibility = View.GONE
            viewHolder.layoutQuote.visibility = View.GONE
            viewHolder.layoutHeader.visibility = View.GONE
            viewHolder.layoutLine.visibility = View.GONE

            if (model[position].data != null) {
                val ss1 = SpannableString(model[position].data!!.text)
                model[position].data!!.inlineStyleRange.forEach {
                    var offset = it.offset
                    var lenght = it.lenght
                    when {
                        it.style == TextStyle.BOLD -> ss1.setSpan(
                            StyleSpan(Typeface.BOLD), offset,
                            offset + lenght, 0
                        )
                        it.style == TextStyle.ITALIC -> ss1.setSpan(
                            StyleSpan(Typeface.ITALIC), offset,
                            offset + lenght, 0
                        )
                        it.style == TextStyle.UNDERLINE -> ss1.setSpan(
                            UnderlineSpan(), offset,
                            offset + lenght, 0
                        )
                        it.style == TextStyle.STRIKE_THROUGH -> ss1.setSpan(
                            StrikethroughSpan(), offset,
                            offset + lenght, 0
                        )
                    }
                }
                viewHolder.edt.setText(ss1)
            }

            if (model[position].isFocus) {
                viewHolder.edt.post {
                    if (viewHolder.edt.requestFocus()) {
                        model[position].isFocus = false
                    }
                }
            } else {
                viewHolder.edt.clearFocus()
            }
            viewHolder.edt.gravity = model[position].data!!.alight
            viewHolder.edt.setSelection(model[position].data!!.selection)

        } else if (model[position].viewType == EditerViewType.IMAGE) {
            viewHolder.layoutTextView.visibility = View.GONE
            viewHolder.layoutQuote.visibility = View.GONE
            viewHolder.layoutLine.visibility = View.GONE
            viewHolder.layoutHeader.visibility = View.GONE
            viewHolder.layoutImage.visibility = View.VISIBLE

            GlideApp.with(context)
                .load(ImageUtil.decodeBase64(model[position].data!!.src))
                .fitCenter()
                .placeholder(ColorDrawable(context.resources.getColor(R.color.grey)))
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                .into(viewHolder.image)

            viewHolder.layoutRecycle?.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            viewHolder.layoutImage.setOnClickListener {
                if (!viewHolder.layoutImage.isFocused)
                    viewHolder.layoutImage.requestFocus()
                else
                    viewHolder.layoutImage.clearFocus()
            }

            if (model[position].showBorder)
                viewHolder.layoutImage.requestFocus()
            else
                viewHolder.layoutImage.clearFocus()


            viewHolder.layoutImage.setOnFocusChangeListener { view, b ->
                if (b) {
                    viewHolder.btnDeleteImage.visibility = View.VISIBLE
                } else {
                    viewHolder.btnDeleteImage.visibility = View.GONE
                }
            }

        } else if (model[position].viewType == EditerViewType.QUOTE) {
            viewHolder.layoutTextView.visibility = View.GONE
            viewHolder.layoutImage.visibility = View.GONE
            viewHolder.layoutLine.visibility = View.GONE
            viewHolder.layoutHeader.visibility = View.GONE
            viewHolder.layoutQuote.visibility = View.VISIBLE

            if (model[position].data != null) {
                viewHolder.edtQuote.setText(model[position].data!!.text)
            }

            viewHolder.edtQuote.gravity = Gravity.CENTER
            viewHolder.edtQuote.setTypeface(null, Typeface.ITALIC)

            if (model[position].isFocus) {
                viewHolder.edtQuote.post {
                    if (viewHolder.edtQuote.requestFocus()) {
                        model[position].isFocus = false
                    }
                }
            }
        } else if (model[position].viewType == EditerViewType.HEADER) {
            viewHolder.layoutTextView.visibility = View.GONE
            viewHolder.layoutImage.visibility = View.GONE
            viewHolder.layoutLine.visibility = View.GONE
            viewHolder.layoutQuote.visibility = View.GONE
            viewHolder.layoutHeader.visibility = View.VISIBLE

            if (model[position].data != null) {
                viewHolder.edtHeader.setText(model[position].data!!.text)
            }

            viewHolder.edtHeader.gravity = model[position].data!!.alight

            viewHolder.edtHeader.setTypeface(null, Typeface.BOLD)

            if (model[position].isFocus) {
                viewHolder.edtHeader.post {
                    if (viewHolder.edtHeader.requestFocus()) {
                        model[position].isFocus = false
                    }
                }
            }
        } else if (model[position].viewType == EditerViewType.LINE) {
            viewHolder.layoutTextView.visibility = View.GONE
            viewHolder.layoutQuote.visibility = View.GONE
            viewHolder.layoutImage.visibility = View.GONE
            viewHolder.layoutHeader.visibility = View.GONE
            viewHolder.layoutLine.visibility = View.VISIBLE
        }
    }


    fun upDateItem(position: Int) {
        model.forEachIndexed { index, editerModel ->
            editerModel.data?.let {
                Log.e("data:$index", it.toString())
            }
        }

        Log.e("data", model.toString())
        notifyItemInserted(position)
        notifyItemChanged(position - 1, false)
    }

    fun upDateLineItem(position: Int) {
        model.forEachIndexed { index, editerModel ->
            editerModel.data?.let {
                Log.e("data:$index", it.toString())
            }
        }

        Log.e("data", model.toString())
        notifyItemInserted(position + 1)
    }

    fun upDateLineItemWithEditText(position: Int) {
        model.forEachIndexed { index, editerModel ->
            editerModel.data?.let {
                Log.e("data:$index", it.toString())
            }
        }

        Log.e("data", model.toString())
        notifyItemInserted(position + 1)
        notifyItemInserted(position + 2)
    }

    fun upDateImageItem(position: Int) {
        model.forEachIndexed { index, editerModel ->
            editerModel.data?.let {
                Log.e("data:$index", it.toString())
            }
        }

        Log.e("data", model.toString())
        notifyItemInserted(position)
        notifyItemInserted(position + 1)
        notifyItemChanged(position - 1, false)
    }

    fun upDateRemoveItem(position: Int) {
        model.forEachIndexed { index, editerModel ->
            editerModel.data?.let {
                Log.e("data:$index", it.toString())
            }
        }
        notifyItemRemoved(position)
        notifyItemChanged(position - 1, false)
    }

    fun upDateRemoveItemWithoutCurrentChange(position: Int) {
        model.forEachIndexed { index, editerModel ->
            editerModel.data?.let {
                Log.e("data:$index", it.toString())
            }
        }
        notifyItemRemoved(position)
//        notifyItemChanged(position, false)
    }

    fun updateCurrentItem(position: Int) {
        notifyItemChanged(position, false)
    }


    override fun getItemCount(): Int {
        return model.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return model[position].id
    }

    override fun setHasStableIds(hasStableIds: Boolean) {
        super.setHasStableIds(true)
    }

    inner class MyViewHolder(v: View, customEditTextListener: MyCustomEditTextListener) : RecyclerView.ViewHolder(v) {
        var layoutTextView = v.findViewById<LinearLayout>(R.id.layoutTextView)
        var layoutHeader = v.findViewById<LinearLayout>(R.id.layoutHeader)
        var layoutRecycle = v.findViewById<LinearLayout>(R.id.layoutRecycle)
        var layoutQuote = v.findViewById<LinearLayout>(R.id.layoutQuote)
        var layoutLine = v.findViewById<LinearLayout>(R.id.layoutLine)
        var layoutImage = v.findViewById<RelativeLayout>(R.id.layoutImage)
        var btnDeleteImage = v.findViewById<RelativeLayout>(R.id.btnDeleteImage)
        var image = v.findViewById<ImageView>(R.id.image)
        var edt = v.findViewById<EditTextSelectable>(R.id.edt)
        var edtHeader = v.findViewById<EditText>(R.id.edtHeader)
        var edtQuote = v.findViewById<EditText>(R.id.edtQuote)
        var myCustomEditTextListener = customEditTextListener

        init {
            edt.addTextChangedListener(myCustomEditTextListener)
            edt.onFocusChangeListener = myCustomEditTextListener
            edt.setOnKeyListener(myCustomEditTextListener)
            edt.setAccessibilityDelegate(myCustomEditTextListener)
            edt.addOnSelectionChangedListener(myCustomEditTextListener)
            edt.customSelectionActionModeCallback = StyleCallback(edt, listener)

            edtHeader.addTextChangedListener(myCustomEditTextListener)
            edtHeader.onFocusChangeListener = myCustomEditTextListener
            edtHeader.setOnKeyListener(myCustomEditTextListener)
            edtHeader.setAccessibilityDelegate(myCustomEditTextListener)

            edtQuote.addTextChangedListener(myCustomEditTextListener)
            edtQuote.onFocusChangeListener = myCustomEditTextListener
            edtQuote.setOnKeyListener(myCustomEditTextListener)
            edtQuote.setAccessibilityDelegate(myCustomEditTextListener)

        }
    }

    inner class MyCustomViewListener : View.OnFocusChangeListener {
        override fun onFocusChange(p0: View?, p1: Boolean) {
            if (p1) {

            } else {

            }
        }

    }

    inner class MyCustomEditTextListener : TextWatcher,
        EditTextSelectable.onSelectionChangedListener,
        View.OnFocusChangeListener,
        View.OnKeyListener,
        View.AccessibilityDelegate() {

        private var keyId: Long = -1

        fun updatePosition(keyId: Long) {
            this.keyId = keyId
        }

        override fun sendAccessibilityEvent(host: View?, eventType: Int) {
            super.sendAccessibilityEvent(host, eventType)
            if (eventType == AccessibilityEvent.TYPE_VIEW_TEXT_SELECTION_CHANGED) {
                Log.e("selectionStart", (host as EditText).selectionStart.toString())
                Log.e("selectionEnd", host.selectionEnd.toString())
            }
        }

        override fun onKey(view: View, keyCode: Int, keyEvent: KeyEvent): Boolean {
            if (keyCode == EditorInfo.IME_ACTION_SEARCH ||
                keyCode == EditorInfo.IME_ACTION_DONE ||
                keyEvent.action == KeyEvent.ACTION_DOWN &&
                keyEvent.keyCode == KeyEvent.KEYCODE_ENTER
            ) {

                var index = model.filterGetIndex {
                    it.id == keyId
                }

                val ss1 = SpannableString(
                    (view as EditText).text.subSequence(0, view.selectionEnd)
                )

                CheckStyle.checkSpan(null, ss1).forEach {
                    var offset = it.offset
                    var lenght = it.lenght
                    when {
                        it.style == TextStyle.BOLD -> ss1.setSpan(
                            StyleSpan(Typeface.BOLD), offset,
                            offset + lenght, 0
                        )
                        it.style == TextStyle.ITALIC -> ss1.setSpan(
                            StyleSpan(Typeface.ITALIC), offset,
                            offset + lenght, 0
                        )
                        it.style == TextStyle.UNDERLINE -> ss1.setSpan(
                            UnderlineSpan(), offset,
                            offset + lenght, 0
                        )
                    }
                }

                val ss2 = SpannableString(
                    view.text.subSequence(
                        view.selectionEnd,
                        view.text.toString().length
                    )
                )

                listener.onNextLine(index + 1, ss2)

                view.setText(ss1)

                return true
            } else if (keyEvent.action == KeyEvent.ACTION_DOWN &&
                keyEvent.keyCode == KeyEvent.KEYCODE_DEL
            ) {

                var index = model.filterGetIndex {
                    it.id == keyId
                }
                if (index > 0 && (view as EditText).selectionEnd == 0) {
                    val ssPrevios = SpannableStringBuilder(model[index - 1].data!!.text)
                    model[index - 1].data!!.inlineStyleRange.forEach {
                        var offset = it.offset
                        var lenght = it.lenght
                        if (offset + lenght < ssPrevios.length) {
                            when {
                                it.style == TextStyle.BOLD -> ssPrevios.setSpan(
                                    StyleSpan(Typeface.BOLD), offset,
                                    offset + lenght, 0
                                )
                                it.style == TextStyle.ITALIC -> ssPrevios.setSpan(
                                    StyleSpan(Typeface.ITALIC), offset,
                                    offset + lenght, 0
                                )
                                it.style == TextStyle.UNDERLINE -> ssPrevios.setSpan(
                                    UnderlineSpan(), offset,
                                    offset + lenght, 0
                                )
                            }
                        }
                    }

                    val ssCurrent = SpannableStringBuilder(
                        view.text.subSequence(
                            view.selectionStart,
                            view.text.toString().length
                        )
                    )

                    listener.onPreviousLine(index, ssPrevios.append(ssCurrent))
                }
                return false
            } else {
                return false
            }
        }

        override fun onFocusChange(p0: View?, p1: Boolean) {
            if (p1) {
                var index = model.filterGetIndex {
                    it.id == keyId
                }
                listener.updateCursorPosition(index, p0!!)
            }
        }

        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i2: Int, i3: Int) {
            // no op
        }

        override fun onTextChanged(charSequence: CharSequence, i: Int, i2: Int, i3: Int) {
            var index = model.filterGetIndex {
                it.id == keyId
            }
            if (model[index].viewType == EditerViewType.QUOTE) {
                listener.onUpdateText(index, charSequence, false)
            } else {
                listener.onUpdateText(index, charSequence, true)
            }
        }

        override fun afterTextChanged(editable: Editable) {
            // no op
        }

        override fun onSelectionChanged(selStart: Int, selEnd: Int, editText: AppCompatEditText?) {
//            var index = model.filterGetIndex {
//                it.id == keyId
//            }
//            listener.onCursorChange(index, selStart, selEnd, editText!!)
        }

    }
}