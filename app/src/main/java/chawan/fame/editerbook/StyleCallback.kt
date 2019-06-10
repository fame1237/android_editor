package chawan.fame.editerbook

import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
import android.text.Spanned.SPAN_INTERMEDIATE
import android.text.style.CharacterStyle
import android.text.style.StyleSpan
import android.text.style.TypefaceSpan
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText

class StyleCallback : ActionMode.Callback {

    var bodyView: EditText? = null

    constructor(bodyView: EditText) {
        this.bodyView = bodyView
    }


    override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
        val inflater = mode.menuInflater
        inflater.inflate(R.menu.style, menu)
        menu.removeItem(android.R.id.selectAll)
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
        return false
    }

    override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
        val cs: CharacterStyle
        val start = bodyView!!.selectionStart
        val end = bodyView!!.selectionEnd
        val ssb = SpannableStringBuilder(bodyView!!.text)


        when (item.itemId) {

            R.id.bold -> {
                cs = StyleSpan(Typeface.BOLD)
//                var typeface = ssb.getSpans(start, end, Typeface::class.java)

                var typeface = ssb.getSpans(start, end, StyleSpan::class.java)

                if (typeface.size == 1) {
                    ssb.setSpan(StyleSpan(Typeface.NORMAL), start, end, SPAN_EXCLUSIVE_EXCLUSIVE)
                } else {
                    ssb.setSpan(cs, start, end, SPAN_EXCLUSIVE_EXCLUSIVE)
                }
                bodyView!!.text = ssb
                return true
            }

            R.id.italic -> {
                cs = StyleSpan(Typeface.ITALIC)
                Log.e("mySpanItalic", ssb.getSpans(start, end, Typeface::class.java).toString())
                var typeface = ssb.getSpans(start, end, StyleSpan::class.java)
                if (typeface.size == 1) {
                    ssb.setSpan(null, start, end, SPAN_EXCLUSIVE_EXCLUSIVE)
                } else {
                    ssb.setSpan(cs, start, end, SPAN_EXCLUSIVE_EXCLUSIVE)
                }
                bodyView!!.text = ssb
                return true
            }

            R.id.underline -> {
                cs = UnderlineSpan()
                Log.e("mySpanUnderline", ssb.getSpans(start, end, UnderlineSpan::class.java).toString())
                ssb.setSpan(cs, start, end, 1)
                bodyView!!.text = ssb
                return true
            }
        }
        return false
    }

    override fun onDestroyActionMode(mode: ActionMode) {}
}