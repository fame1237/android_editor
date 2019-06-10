package chawan.fame.editerbook

import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned.*
import android.text.style.CharacterStyle
import android.text.style.StyleSpan
import android.text.style.TypefaceSpan
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.R.attr.editable
import android.text.Editable
import android.text.Spanned
import android.R.attr.editable


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
                var typeface = ssb.getSpans(start, end, CharacterStyle::class.java)


                if (typeface.isNotEmpty()) {
                    typeface.forEach {
                        if (it is StyleSpan) {
                            val ess = bodyView!!.editableText.getSpanStart(it)
                            val ese = bodyView!!.editableText.getSpanEnd(it)

                            if (it.style == Typeface.BOLD) {
                                if (end > start) {
                                    if (start >= ese) {
                                        // User inputs to the end of the existing e span
                                        // End existing e span
                                        ssb.removeSpan(it)
                                        ssb.setSpan(it, ess, start - 1, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
                                    } else if (start === ess && end === ese) {
                                        // Case 1 desc:
                                        // *BBBBBB*
                                        // All selected, and un-check e
                                        ssb.removeSpan(it)
                                    } else if (start > ess && end < ese) {
                                        // Case 2 desc:
                                        // BB*BB*BB
                                        // *BB* is selected, and un-check e
                                        ssb.removeSpan(it)
                                        ssb.setSpan(it, ess, start, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
                                        ssb.setSpan(it, end, ese, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
                                    } else if (start === ess && end < ese) {
                                        // Case 3 desc:
                                        // *BBBB*BB
                                        // *BBBB* is selected, and un-check e
                                        ssb.removeSpan(it)
                                        ssb.setSpan(it, end, ese, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
                                    } else if (start > ess && end === ese) {
                                        // Case 4 desc:
                                        // BB*BBBB*
                                        // *BBBB* is selected, and un-check e
                                        ssb.removeSpan(it)
                                        ssb.setSpan(it, ess, start, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)
                                    }
                                }
                            } else {
                                ssb.setSpan(cs, start, end, SPAN_EXCLUSIVE_INCLUSIVE)
                            }
                        } else {
                            ssb.setSpan(cs, start, end, SPAN_EXCLUSIVE_INCLUSIVE)
                        }
                    }

                } else {
                    ssb.setSpan(cs, start, end, SPAN_EXCLUSIVE_INCLUSIVE)
                }
                bodyView!!.text = ssb
                return true
            }

            R.id.italic -> {
                cs = StyleSpan(Typeface.ITALIC)
                Log.e("mySpanItalic", ssb.getSpans(start, end, Typeface::class.java).toString())
                var typeface = ssb.getSpans(start, end, StyleSpan::class.java)
                ssb.setSpan(cs, start, end, SPAN_EXCLUSIVE_INCLUSIVE)
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