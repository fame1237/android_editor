package com.example.storylog_editor.ui.editor

import android.app.Activity
import android.content.ClipboardManager
import android.content.Context
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
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.storylog_editor.util.CheckStyle
import com.example.storylog_editor.util.KeyboardHelper
import com.example.storylog_editor.R
import com.example.storylog_editor.ScreenUtil.createIndentedText
import com.example.storylog_editor.ScreenUtil.dpToPx
import com.example.storylog_editor.StyleCallback
import com.example.storylog_editor.extension.filterGetArrayIndex
import com.example.storylog_editor.extension.filterGetIndex
import com.example.storylog_editor.model.*
import com.example.storylog_editor.view.ediitext.CutCopyPasteEditText
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.lang.Exception


/**
 * Created by fame on 14/2/2018 AD.
 */


class EditerAdapter(
    val context: Context,
    val activity: Activity,
    var listener: OnChange,
    var model: MutableList<EditerModel>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    interface OnChange {
        fun onNextLine(position: Int, text: CharSequence)
        fun onPreviousLine(position: Int, text: CharSequence, selection: Int)
        fun onCursorChange(
            position: Int,
            startPosition: Int,
            endPosition: Int,
            edt: AppCompatEditText
        )

        fun onDeleteRow(position: Int)
        fun onUpdateText(position: Int, text: CharSequence, updateStyle: Boolean)
        fun updateCursorPosition(position: Int, view: View, imageIndex: MutableList<Int>)
        fun onUpdateBold()
        fun setShowBorderFalse(position: Int)
        fun clearFocus(position: Int)
        fun onPasteText(position: Int, textList: MutableList<String>)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            1 -> {
                val itemView =
                    LayoutInflater.from(viewGroup.context)
                        .inflate(R.layout.item_editor_edittext, viewGroup, false)
                return MyEditTextViewHolder(itemView, MyCustomEditTextListener())
            }
            2 -> {
                val itemView =
                    LayoutInflater.from(viewGroup.context)
                        .inflate(R.layout.item_editor_image, viewGroup, false)
                return MyImageViewHolder(itemView, MyCustomImageLayoutEditTextListener())
            }
            3 -> {
                val itemView =
                    LayoutInflater.from(viewGroup.context)
                        .inflate(R.layout.item_editor_line, viewGroup, false)
                return MyLineViewHolder(itemView)
            }
            4 -> {
                val itemView =
                    LayoutInflater.from(viewGroup.context)
                        .inflate(R.layout.item_editor_quote, viewGroup, false)
                return MyQuoteViewHolder(itemView, MyCustomEditTextListener())
            }
            5 -> {
                val itemView =
                    LayoutInflater.from(viewGroup.context)
                        .inflate(R.layout.item_editor_header, viewGroup, false)
                return MyHeaderViewHolder(itemView, MyCustomEditTextListener())
            }
            else -> {
                val itemView =
                    LayoutInflater.from(viewGroup.context)
                        .inflate(R.layout.item_editor, viewGroup, false)
                return MyViewHolder(itemView, MyCustomEditTextListener())
            }
        }

    }

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        if (viewHolder is MyEditTextViewHolder) {
            viewHolder.myCustomEditTextListener.updatePosition(model[position].id)

            if (model[position].isFocus) {
                viewHolder.edt.post {
                    if (viewHolder.edt.requestFocus()) {
                        model[position].isFocus = false
                    }
                }
            } else {
                viewHolder.edt.post {
                    viewHolder.edt.clearFocus()
                }
            }

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


            when (model[position].data!!.alight) {
                Alignment.START -> {
                    viewHolder.edt.gravity = Gravity.START
                    viewHolder.edt.setText(
                        createIndentedText(
                            viewHolder.edt.text as CharSequence,
                            0,
                            0
                        )
                    )
                }
                Alignment.CENTER -> {
                    viewHolder.edt.gravity = Gravity.CENTER
                    viewHolder.edt.setText(
                        createIndentedText(
                            viewHolder.edt.text as CharSequence,
                            0,
                            0
                        )
                    )
                }
                Alignment.END -> {
                    viewHolder.edt.gravity = Gravity.END
                    viewHolder.edt.setText(
                        createIndentedText(
                            viewHolder.edt.text as CharSequence,
                            0,
                            0
                        )
                    )
                }
                Alignment.INDENT -> {
                    viewHolder.edt.gravity = Gravity.START
                    viewHolder.edt.setText(
                        createIndentedText(
                            viewHolder.edt.text as CharSequence,
                            dpToPx(30f, context),
                            0
                        )
                    )
                }
            }

            try {
                viewHolder.edt.setSelection(model[position].data!!.selection)
            } catch (ex: Exception) {
                model[position].data!!.selection = 0
            }
            model[position].data!!.selection = 0
        } else if (viewHolder is MyImageViewHolder) {
            viewHolder.myCustomEditTextListener.updatePosition(model[position].id, viewHolder)
            if (model[position].showBorder) {
                viewHolder.btnDeleteImage.visibility = View.VISIBLE
                viewHolder.imageBackgroud.background =
                    (context.resources.getDrawable(R.drawable.border))
            } else {
                viewHolder.btnDeleteImage.visibility = View.GONE
                viewHolder.imageBackgroud.background =
                    (context.resources.getDrawable(R.drawable.transaparent))
//                viewHolder.border.visibility = View.GONE
            }

            viewHolder.layoutImage.setOnClickListener {
                if (viewHolder.btnDeleteImage.visibility == View.VISIBLE) {
                    viewHolder.btnDeleteImage.visibility = View.GONE
                    viewHolder.imageBackgroud.background =
                        (context.resources.getDrawable(R.drawable.transaparent))
                } else {
                    viewHolder.btnDeleteImage.visibility = View.VISIBLE
                    viewHolder.imageBackgroud.background =
                        (context.resources.getDrawable(R.drawable.border))
                    if (!viewHolder.edtImage.isFocused) {
                        listener.clearFocus(position)
                    } else {
                        viewHolder.edtImage.clearFocus()
                    }
//                    (context as EditerActivity).hideKeyboard()
                }
            }

            viewHolder.btnDeleteImage.setOnClickListener {
                var index = model.filterGetIndex {
                    it.id == viewHolder.myCustomEditTextListener.keyId
                }
                listener.onDeleteRow(index)
            }

            Glide.with(context)
                .load(("https://s3.ap-southeast-1.amazonaws.com/media.fictionlog/statics/5d5bc72ewyIGManx.jpeg"))
                .placeholder(ColorDrawable(context.resources.getColor(R.color.grey)))
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                .into(viewHolder.image)


            viewHolder.layoutRecycle?.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            viewHolder.edtImage.gravity = Gravity.CENTER

            if (model[position].data != null) {
                viewHolder.edtImage.post {
                    viewHolder.edtImage.setText(model[position].data!!.text)
                }
            }

            if (model[position].isFocus) {
                viewHolder.edtImage.post {
                    if (viewHolder.edtImage.requestFocus()) {
                        model[position].isFocus = false
                    }
                }
            }


        } else if (viewHolder is MyQuoteViewHolder) {
            viewHolder.myCustomEditTextListener.updatePosition(model[position].id)
            if (model[position].data != null) {
                viewHolder.edtQuote.post {
                    viewHolder.edtQuote.setText(model[position].data!!.text)
                }
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
        } else if (viewHolder is MyHeaderViewHolder) {
            viewHolder.myCustomEditTextListener.updatePosition(model[position].id)
            if (model[position].data != null) {
                viewHolder.edtHeader.setText(model[position].data!!.text)
            }

            when (model[position].data!!.alight) {
                Alignment.START -> {
                    viewHolder.edtHeader.gravity = Gravity.START
                    viewHolder.edtHeader.setText(
                        createIndentedText(
                            viewHolder.edtHeader.text.toString(),
                            0,
                            0
                        )
                    )
                }
                Alignment.CENTER -> {
                    viewHolder.edtHeader.gravity = Gravity.CENTER
                    viewHolder.edtHeader.setText(
                        createIndentedText(
                            viewHolder.edtHeader.text as CharSequence,
                            0,
                            0
                        )
                    )
                }
                Alignment.END -> {
                    viewHolder.edtHeader.gravity = Gravity.END
                    viewHolder.edtHeader.setText(
                        createIndentedText(
                            viewHolder.edtHeader.text as CharSequence,
                            0,
                            0
                        )
                    )
                }
                Alignment.INDENT -> {
                    viewHolder.edtHeader.gravity = Gravity.START
                    viewHolder.edtHeader.setText(
                        createIndentedText(
                            viewHolder.edtHeader.text as CharSequence,
                            dpToPx(30f, context),
                            0
                        )
                    )
                }
            }

            if (model[position].isFocus) {
                viewHolder.edtHeader.post {
                    if (viewHolder.edtHeader.requestFocus()) {
                        model[position].isFocus = false
                    }
                }
            }
        }
    }

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)
        when (holder) {
            is MyEditTextViewHolder -> {
                holder.edt.isEnabled = false
                holder.edt.isEnabled = true
            }
            is MyImageViewHolder -> {
                holder.edtImage.isEnabled = false
                holder.edtImage.isEnabled = true
            }
            is MyQuoteViewHolder -> {
                holder.edtQuote.isEnabled = false
                holder.edtQuote.isEnabled = true
            }
            is MyHeaderViewHolder -> {
                holder.edtHeader.isEnabled = false
                holder.edtHeader.isEnabled = true
            }
            else -> {
            }
        }
    }

    fun upDateItem(position: Int) {
        notifyItemChanged(position - 1, false)
        notifyItemInserted(position)
    }

    fun upDateItemRange(position: Int,range:Int) {
        notifyItemRangeChanged(position, range)
    }

    fun upDateLineItem(position: Int) {
        notifyItemInserted(position + 1)
    }

    fun upDateLineItemWithEditText(position: Int) {
        notifyItemRangeChanged(position + 1, 2)
    }

    fun upDateImageItem(position: Int) {
        notifyItemRangeChanged(position, 2)
        notifyItemChanged(position - 1, false)
    }

    fun upDateRemoveItem(position: Int) {
        notifyItemChanged(position - 1, false)
        notifyItemRemoved(position)
    }

    fun upDateRemoveItemWithoutCurrentChange(position: Int) {
        notifyItemRemoved(position)
    }

    fun updateCurrentItem(position: Int) {
        notifyItemChanged(position, false)
    }


    override fun getItemCount(): Int {
        return model.size
    }

    override fun getItemId(position: Int): Long {
        return model[position].id
    }

    override fun setHasStableIds(hasStableIds: Boolean) {
        super.setHasStableIds(true)
    }

    override fun getItemViewType(position: Int): Int {
        when (model[position].viewType) {
            EditerViewType.EDIT_TEXT -> {
                return 1
            }
            EditerViewType.IMAGE -> {
                return 2
            }
            EditerViewType.LINE -> {
                return 3
            }
            EditerViewType.QUOTE -> {
                return 4
            }
            EditerViewType.HEADER -> {
                return 5
            }
            else -> {
                return -1
            }
        }
    }

    inner class MyEditTextViewHolder(v: View, customEditTextListener: MyCustomEditTextListener) :
        RecyclerView.ViewHolder(v) {
        var edt = v.findViewById<CutCopyPasteEditText>(R.id.edt)
        var myCustomEditTextListener = customEditTextListener

        init {
            edt.addTextChangedListener(myCustomEditTextListener)
            edt.onFocusChangeListener = myCustomEditTextListener
            edt.setOnKeyListener(myCustomEditTextListener)
            edt.accessibilityDelegate = myCustomEditTextListener
            edt.customSelectionActionModeCallback = StyleCallback(edt, listener, activity)

            edt.setOnCutCopyPasteListener(myCustomEditTextListener)
        }
    }

    inner class MyEditTextIndentViewHolder(
        v: View,
        customEditTextListener: MyCustomEditTextListener
    ) :
        RecyclerView.ViewHolder(v) {
        var edt =
            v.findViewById<com.example.storylog_editor.view.ediitext.CutCopyPasteEditText>(R.id.edt)
        var myCustomEditTextListener = customEditTextListener

        init {
            edt.addTextChangedListener(myCustomEditTextListener)
            edt.onFocusChangeListener = myCustomEditTextListener
            edt.setOnKeyListener(myCustomEditTextListener)
            edt.accessibilityDelegate = myCustomEditTextListener
            edt.customSelectionActionModeCallback = StyleCallback(edt, listener, activity)
        }
    }

    inner class MyHeaderViewHolder(v: View, customEditTextListener: MyCustomEditTextListener) :
        RecyclerView.ViewHolder(v) {
        var edtHeader = v.findViewById<AppCompatEditText>(R.id.edtHeader)
        var myCustomEditTextListener = customEditTextListener

        init {
            edtHeader.addTextChangedListener(myCustomEditTextListener)
            edtHeader.onFocusChangeListener = myCustomEditTextListener
            edtHeader.setOnKeyListener(myCustomEditTextListener)
            edtHeader.accessibilityDelegate = myCustomEditTextListener
        }
    }

    inner class MyQuoteViewHolder(v: View, customEditTextListener: MyCustomEditTextListener) :
        RecyclerView.ViewHolder(v) {
        var edtQuote =
            v.findViewById<com.example.storylog_editor.view.ediitext.CutCopyPasteEditText>(R.id.edtQuote)
        var myCustomEditTextListener = customEditTextListener

        init {
            edtQuote.addTextChangedListener(myCustomEditTextListener)
            edtQuote.onFocusChangeListener = myCustomEditTextListener
            edtQuote.setOnKeyListener(myCustomEditTextListener)
            edtQuote.accessibilityDelegate = myCustomEditTextListener
        }
    }

    inner class MyImageViewHolder(
        v: View,
        customEditTextListener: MyCustomImageLayoutEditTextListener
    ) :
        RecyclerView.ViewHolder(v) {
        var layoutImage = v.findViewById<RelativeLayout>(R.id.layoutImage)
        var imageBackgroud = v.findViewById<View>(R.id.imageBackgroud)
        var btnDeleteImage = v.findViewById<RelativeLayout>(R.id.btnDeleteImage)
        var layoutRecycle = v.findViewById<LinearLayout>(R.id.layoutRecycle)
        var image = v.findViewById<ImageView>(R.id.image)
        var edtImage =
            v.findViewById<com.example.storylog_editor.view.ediitext.CutCopyPasteEditText>(R.id.edtImage)
        var myCustomEditTextListener = customEditTextListener

        init {
            edtImage.addTextChangedListener(customEditTextListener)
            edtImage.onFocusChangeListener = customEditTextListener
            edtImage.setOnKeyListener(customEditTextListener)
            edtImage.accessibilityDelegate = customEditTextListener

        }
    }

    inner class MyLineViewHolder(v: View) :
        RecyclerView.ViewHolder(v) {
        var layoutRecycle = v.findViewById<LinearLayout>(R.id.layoutRecycle)
        var layoutLine = v.findViewById<LinearLayout>(R.id.layoutLine)
    }

    inner class MyViewHolder(v: View, customEditTextListener: MyCustomEditTextListener) :
        RecyclerView.ViewHolder(v)

    inner class MyCustomImageLayoutEditTextListener : TextWatcher,
        com.example.storylog_editor.view.EditTextSelectable.onSelectionChangedListener,
        View.OnFocusChangeListener,
        View.OnKeyListener,
        View.AccessibilityDelegate() {

        var keyId: Long = -1
        var viewHolder: MyImageViewHolder? = null

        fun updatePosition(keyId: Long, viewHolder: MyImageViewHolder) {
            this.keyId = keyId
            this.viewHolder = viewHolder
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
                return false
            } else {
                return false
            }
        }

        override fun onFocusChange(p0: View?, p1: Boolean) {
            if (p1) {
                if (p0 is EditText) {
                    KeyboardHelper.showSoftKeyboardForcefully(context, p0)
                    if (viewHolder?.btnDeleteImage?.visibility == View.VISIBLE) {
                        viewHolder?.btnDeleteImage?.visibility = View.GONE
                        viewHolder?.imageBackgroud?.background =
                            (context.resources.getDrawable(R.drawable.transaparent))
                    }
                }
                Single.fromCallable {
                    var index = model.filterGetIndex {
                        it.id == keyId
                    }
                    var imageIndex = model.filterGetArrayIndex {
                        it.viewType == EditerViewType.IMAGE
                    }
                    var indexDataModel = IndexData()
                    indexDataModel.index = index
                    indexDataModel.imageIndex = imageIndex

                    indexDataModel
                }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.computation())
                    .subscribe { indexDataModel ->
                        listener.updateCursorPosition(
                            indexDataModel.index,
                            p0!!,
                            indexDataModel.imageIndex
                        )
                    }
            } else {
                if (p0 is EditText) {
//                    KeyboardHelper.hideSoftKeyboard2(context as EditerActivity)
                }
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

    inner class MyCustomEditTextListener : TextWatcher,
        com.example.storylog_editor.view.EditTextSelectable.onSelectionChangedListener,
        View.OnFocusChangeListener,
        View.OnKeyListener,
        View.AccessibilityDelegate(),
        CutCopyPasteEditText.OnCutCopyPasteListener {


        var keyId: Long = -1

        fun updatePosition(keyId: Long) {
            this.keyId = keyId
        }

        override fun onCut() {

        }

        override fun onCopy() {
        }

        override fun onPaste() {
            var clipboard =
                activity?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            var clipData = clipboard.primaryClip
            var itemCount = clipData?.itemCount ?: 0
            if (itemCount > 0) {
                var item2 = clipData?.getItemAt(0)
                var text = item2?.text?.split("\n")
                var index = model.filterGetIndex {
                    it.id == keyId
                }
                text?.let {
                    listener.onPasteText(index, text.toMutableList())
                }
            }
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
                        if (offset + lenght <= ssPrevios.length) {
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
                    var selection = ssPrevios.length
                    listener.onPreviousLine(index, ssPrevios.append(ssCurrent), selection)
                }
                return false
            } else {
                return false
            }
        }

        override fun onFocusChange(p0: View?, p1: Boolean) {
            if (p1) {
                if (p0 is EditText) {
                    KeyboardHelper.showSoftKeyboardForcefully(context, p0)
                }
                Single.fromCallable {
                    var index = model.filterGetIndex {
                        it.id == keyId
                    }
                    var imageIndex = model.filterGetArrayIndex {
                        it.viewType == EditerViewType.IMAGE
                    }
                    var indexDataModel = IndexData()
                    indexDataModel.index = index
                    indexDataModel.imageIndex = imageIndex

                    indexDataModel
                }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.computation())
                    .subscribe { indexDataModel ->
                        listener.updateCursorPosition(
                            indexDataModel.index,
                            p0!!,
                            indexDataModel.imageIndex
                        )
                    }
            } else {
                if (p0 is EditText) {
//                    KeyboardHelper.hideSoftKeyboard2(context as EditerActivity)
                }
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