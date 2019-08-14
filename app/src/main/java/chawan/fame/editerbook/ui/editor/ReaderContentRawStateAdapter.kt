package chawan.fame.editerbook.ui.editor

import android.content.Context
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.text.SpannableString
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import chawan.fame.editerbook.R
import chawan.fame.editerbook.glide.GlideApp
import chawan.fame.editerbook.model.editor.EditerModel
import chawan.fame.editerbook.model.editor.EditerViewType
import chawan.fame.editerbook.model.editor.TextStyle
import chawan.fame.editerbook.util.ImageUtil
import chawan.fame.editerbook.view.EditTextSelectable
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions


/**
 * Created by fame on 14/2/2018 AD.
 */


class ReaderContentRawStateAdapter(
    val context: Context,
    var model: MutableList<EditerModel>
) : RecyclerView.Adapter<ReaderContentRawStateAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(viewGroup.context).inflate(R.layout.item_read_content, viewGroup, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(viewHolder: MyViewHolder, position: Int) {
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

    inner class MyViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        var layoutTextView = v.findViewById<LinearLayout>(R.id.layoutTextView)
        var layoutHeader = v.findViewById<LinearLayout>(R.id.layoutHeader)
        var layoutRecycle = v.findViewById<LinearLayout>(R.id.layoutRecycle)
        var layoutQuote = v.findViewById<LinearLayout>(R.id.layoutQuote)
        var layoutLine = v.findViewById<LinearLayout>(R.id.layoutLine)
        var layoutImage = v.findViewById<RelativeLayout>(R.id.layoutImage)
        var btnDeleteImage = v.findViewById<RelativeLayout>(R.id.btnDeleteImage)
        var image = v.findViewById<ImageView>(R.id.image)
        var edt = v.findViewById<TextView>(R.id.edt)
        var edtHeader = v.findViewById<TextView>(R.id.edtHeader)
        var edtQuote = v.findViewById<TextView>(R.id.edtQuote)
    }
}