package chawan.fame.editerbook.ui.editor

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import chawan.fame.editerbook.R


/**
 * Created by fame on 14/2/2018 AD.
 */


class EditerAdapter(
    private val context: Context,
    var listener: OnChange,
    var mFocusPosition: Int
) : RecyclerView.Adapter<EditerAdapter.MyViewHolder>() {

    var edtList: MutableList<EditText> = mutableListOf()

    interface OnChange {
        fun onNextLine(position: Int)
        fun onPreviousLine(position: Int)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_editor, viewGroup, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(viewHolder: MyViewHolder, position: Int) {
        if (viewHolder is MyViewHolder) {
            edtList.add(viewHolder.edt)

            viewHolder.edt.setOnKeyListener { view, keyCode, keyEvent ->
                if (keyCode == EditorInfo.IME_ACTION_SEARCH ||
                    keyCode == EditorInfo.IME_ACTION_DONE ||
                    keyEvent.action == KeyEvent.ACTION_DOWN &&
                    keyEvent.keyCode == KeyEvent.KEYCODE_ENTER
                ) {
                    listener.onNextLine(position)
                    false
                }
                true
            }

            viewHolder.edt.setText("$position")
            if (position == mFocusPosition) {
                viewHolder.edt.requestFocus()
            }
        }
    }

    fun setFocusPosition(position: Int) {
        mFocusPosition = position
    }

    override fun getItemCount(): Int {
        return 30
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    class MyViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var layoutTextView = v.findViewById<LinearLayout>(R.id.layoutTextView)
        var layoutImage = v.findViewById<LinearLayout>(R.id.layoutImage)
        var edt = v.findViewById<EditText>(R.id.edt)
    }
}