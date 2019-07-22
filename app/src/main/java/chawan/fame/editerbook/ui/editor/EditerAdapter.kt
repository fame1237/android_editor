package chawan.fame.editerbook.ui.editor

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityEvent
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import chawan.fame.editerbook.R
import chawan.fame.editerbook.extension.setSelectionChangedListener
import chawan.fame.editerbook.glide.GlideApp
import chawan.fame.editerbook.model.editor.EditerModel
import chawan.fame.editerbook.model.editor.EditerViewType
import chawan.fame.editerbook.util.ImageUtil
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions


/**
 * Created by fame on 14/2/2018 AD.
 */


class EditerAdapter(
    private val context: Context,
    var listener: OnChange,
    var model: MutableList<EditerModel>
) : RecyclerView.Adapter<EditerAdapter.MyViewHolder>() {


    interface OnChange {
        fun onNextLine(position: Int, text: String)
        fun onPreviousLine(position: Int, text: String)
        fun onUpdateText(position: Int, text: String)
        fun updateCursorPosition(position: Int)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_editor, viewGroup, false)
        return MyViewHolder(itemView, MyCustomEditTextListener())
    }

    override fun onBindViewHolder(viewHolder: MyViewHolder, position: Int) {
        viewHolder.layoutRecycle.setOnClickListener {
            listener.updateCursorPosition(position)
        }

        if (model[position].viewType == EditerViewType.EDIT_TEXT) {
            viewHolder.layoutTextView.visibility = View.VISIBLE
            viewHolder.layoutImage.visibility = View.GONE
            viewHolder.myCustomEditTextListener.updatePosition(viewHolder.adapterPosition)

            if (model[position].data != null) {
                viewHolder.edt.setText(model[position].data!!.text)
            }

            if (model[position].isFocus) {
                viewHolder.edt.post {
                    if (viewHolder.edt.requestFocus()) {
                        model[position].isFocus = false
                    }
                }
            }
            viewHolder.edt.gravity = model[position].data!!.alight

        } else if (model[position].viewType == EditerViewType.IMAGE) {
            viewHolder.layoutTextView.visibility = View.GONE
            viewHolder.layoutImage.visibility = View.VISIBLE

            GlideApp
                .with(context)
                .load(ImageUtil.decodeBase64(model[position].data!!.src))
                .apply(RequestOptions.diskCacheStrategyOf(DiskCacheStrategy.ALL))
                .into(viewHolder.image)
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

    fun upDateRemoveItem(position: Int) {
        model.forEachIndexed { index, editerModel ->
            editerModel.data?.let {
                Log.e("data:$index", it.toString())
            }
        }
        notifyItemRemoved(position)
        notifyItemChanged(position - 1, false)
    }

    fun updateCurrentItem(position: Int) {
        notifyItemChanged(position, false)
    }


    override fun getItemCount(): Int {
        return model.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    class MyViewHolder(v: View, customEditTextListener: MyCustomEditTextListener) : RecyclerView.ViewHolder(v) {
        var layoutRecycle = v.findViewById<LinearLayout>(R.id.layoutRecycle)
        var layoutTextView = v.findViewById<LinearLayout>(R.id.layoutTextView)
        var layoutImage = v.findViewById<LinearLayout>(R.id.layoutImage)
        var image = v.findViewById<ImageView>(R.id.image)
        var edt = v.findViewById<EditText>(R.id.edt)
        var myCustomEditTextListener = customEditTextListener

        init {
            edt.addTextChangedListener(myCustomEditTextListener)
            edt.onFocusChangeListener = myCustomEditTextListener
            edt.setOnKeyListener(myCustomEditTextListener)
            edt.setAccessibilityDelegate(myCustomEditTextListener)
        }
    }

    inner class MyCustomEditTextListener : TextWatcher,
        View.OnFocusChangeListener,
        View.OnKeyListener,
        View.AccessibilityDelegate() {

        private var position: Int = 0

        fun updatePosition(position: Int) {
            this.position = position
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
                listener.onNextLine(
                    position + 1,
                    (view as EditText).text.toString().substring(
                        view.selectionEnd,
                        view.text.toString().length
                    )
                )

                view.setText(
                    view.text.toString().substring(
                        0,
                        view.selectionEnd
                    )
                )

                return true
            } else if (keyEvent.action == KeyEvent.ACTION_DOWN &&
                keyEvent.keyCode == KeyEvent.KEYCODE_DEL
            ) {
                if (position > 0 && (view as EditText).selectionEnd == 0) {
                    var text = view.text.toString().substring(
                        view.selectionStart,
                        view.text.toString().length
                    )
                    listener.onPreviousLine(position, model[position - 1].data!!.text + text)
                }
                return false
            } else {
                return false
            }
        }

        override fun onFocusChange(p0: View?, p1: Boolean) {
            if (p1) {
                listener.updateCursorPosition(position)
            }
        }

        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i2: Int, i3: Int) {
            // no op
        }

        override fun onTextChanged(charSequence: CharSequence, i: Int, i2: Int, i3: Int) {
            listener.onUpdateText(position, charSequence.toString())
        }

        override fun afterTextChanged(editable: Editable) {
            // no op
        }
    }
}