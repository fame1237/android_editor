package chawan.fame.editerbook

import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned.*
import android.text.style.CharacterStyle
import android.text.style.StyleSpan
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
        val selectionStart = bodyView!!.selectionStart
        val selectionEnd = bodyView!!.selectionEnd
        val ssb = SpannableStringBuilder(bodyView!!.text)

        when (item.itemId) {

            R.id.bold -> {
                cs = StyleSpan(Typeface.BOLD)

                var typeface = ssb.getSpans(selectionStart, selectionEnd, CharacterStyle::class.java)
                if (typeface.isNotEmpty()) {

                    var isHaveItalic = typeface.filter {
                        it is StyleSpan && it.style == Typeface.BOLD
                    }

                    if (isHaveItalic.isEmpty()) {
                        ssb.setSpan(
                            cs,
                            selectionStart,
                            selectionEnd,
                            SPAN_EXCLUSIVE_INCLUSIVE
                        )
                    } else {
                        typeface.forEach {
                            if (it is StyleSpan) {
                                if (it.style == Typeface.BOLD)
                                    setSpan(it, selectionStart, selectionEnd, ssb)
                            }
                        }
                    }
                } else {
                    ssb.setSpan(
                        cs,
                        selectionStart,
                        selectionEnd,
                        SPAN_EXCLUSIVE_INCLUSIVE
                    )
                }

                bodyView!!.text = ssb
                return true
            }

            R.id.italic -> {
                cs = StyleSpan(Typeface.ITALIC)

                var typeface = ssb.getSpans(selectionStart, selectionEnd, CharacterStyle::class.java)
                if (typeface.isNotEmpty()) {
                    var isHaveItalic = typeface.filter {
                        it is StyleSpan && it.style == Typeface.ITALIC
                    }

                    if (isHaveItalic.isEmpty()) {
                        ssb.setSpan(
                            cs,
                            selectionStart,
                            selectionEnd,
                            SPAN_EXCLUSIVE_INCLUSIVE
                        )
                    } else {
                        typeface.forEach {
                            if (it is StyleSpan) {
                                if (it.style == Typeface.ITALIC)
                                    setSpan(it, selectionStart, selectionEnd, ssb)
                            }
                        }
                    }
                } else {
                    ssb.setSpan(
                        cs,
                        selectionStart,
                        selectionEnd,
                        SPAN_EXCLUSIVE_INCLUSIVE
                    )
                }
                bodyView!!.text = ssb
                return true
            }

            R.id.underline -> {
                cs = UnderlineSpan()

                var typeface = ssb.getSpans(selectionStart, selectionEnd, CharacterStyle::class.java)
                if (typeface.isNotEmpty()) {
                    var isHaveItalic = typeface.filter {
                        it is UnderlineSpan
                    }

                    if (isHaveItalic.isEmpty()) {
                        ssb.setSpan(
                            cs,
                            selectionStart,
                            selectionEnd,
                            SPAN_EXCLUSIVE_INCLUSIVE
                        )
                    } else {
                        typeface.forEach {
                            if (it is UnderlineSpan) {
                                setSpan(it, selectionStart, selectionEnd, ssb)
                            }
                        }
                    }
                } else {
                    ssb.setSpan(
                        cs,
                        selectionStart,
                        selectionEnd,
                        SPAN_EXCLUSIVE_INCLUSIVE
                    )
                }
                bodyView!!.text = ssb
                return true
            }
        }
        return false
    }

    fun setSpan(
        typeface: CharacterStyle, selectionStart: Int, selectionEnd: Int,
        ssb: SpannableStringBuilder
    ) {

        if (typeface is StyleSpan) {
            val textBoldStartPosition = bodyView!!.editableText.getSpanStart(typeface)
            val textBoldEndPosition = bodyView!!.editableText.getSpanEnd(typeface)
            if (selectionEnd > selectionStart) {
                when {
                    selectionStart >= textBoldEndPosition -> {
                        ssb.removeSpan(typeface)
                        ssb.setSpan(
                            typeface,
                            textBoldStartPosition,
                            selectionStart - 1,
                            SPAN_EXCLUSIVE_INCLUSIVE
                        )
                    }
                    selectionStart == textBoldStartPosition && selectionEnd == textBoldEndPosition -> ssb.removeSpan(
                        typeface
                    )
                    selectionStart > textBoldStartPosition && selectionEnd < textBoldEndPosition -> {
                        ssb.removeSpan(typeface)

                        var spanLeft = StyleSpan(typeface.style)
                        var spanRight = StyleSpan(typeface.style)

                        ssb.setSpan(
                            spanLeft,
                            textBoldStartPosition,
                            selectionStart,
                            SPAN_EXCLUSIVE_EXCLUSIVE
                        )

                        ssb.setSpan(
                            spanRight,
                            selectionEnd,
                            textBoldEndPosition,
                            SPAN_EXCLUSIVE_EXCLUSIVE
                        )

                    }
                    selectionStart == textBoldStartPosition && selectionEnd < textBoldEndPosition -> {
                        ssb.removeSpan(typeface)

                        var newSpan = StyleSpan(typeface.style)
                        ssb.setSpan(
                            newSpan,
                            selectionEnd,
                            textBoldEndPosition,
                            SPAN_EXCLUSIVE_INCLUSIVE
                        )
                    }
                    selectionStart == textBoldStartPosition && selectionEnd > textBoldEndPosition -> {
                        ssb.removeSpan(typeface)
                        ssb.setSpan(
                            typeface,
                            selectionStart,
                            selectionEnd,
                            SPAN_EXCLUSIVE_INCLUSIVE
                        )
                    }
                    selectionStart < textBoldStartPosition && selectionEnd == textBoldEndPosition -> {
                        ssb.removeSpan(typeface)
                        ssb.setSpan(
                            typeface,
                            selectionStart,
                            selectionEnd,
                            SPAN_EXCLUSIVE_INCLUSIVE
                        )
                    }
                    selectionStart > textBoldStartPosition && selectionEnd == textBoldEndPosition -> {
                        ssb.removeSpan(typeface)

                        var newSpan = StyleSpan(typeface.style)
                        ssb.setSpan(
                            newSpan,
                            textBoldStartPosition,
                            selectionStart,
                            SPAN_EXCLUSIVE_INCLUSIVE
                        )
                    }
                    selectionStart < textBoldStartPosition && selectionEnd > textBoldEndPosition -> {
                        ssb.removeSpan(typeface)
                        ssb.setSpan(
                            typeface,
                            selectionStart,
                            selectionEnd,
                            SPAN_EXCLUSIVE_INCLUSIVE
                        )
                    }
                }
            }
        } else if (typeface is UnderlineSpan) {
            val textBoldStartPosition = bodyView!!.editableText.getSpanStart(typeface)
            val textBoldEndPosition = bodyView!!.editableText.getSpanEnd(typeface)
            if (selectionEnd > selectionStart) {
                when {
                    selectionStart >= textBoldEndPosition -> {
                        ssb.removeSpan(typeface)
                        ssb.setSpan(
                            typeface,
                            textBoldStartPosition,
                            selectionStart - 1,
                            SPAN_EXCLUSIVE_INCLUSIVE
                        )
                    }
                    selectionStart == textBoldStartPosition && selectionEnd == textBoldEndPosition -> {
                        ssb.removeSpan(typeface)
                    }
                    selectionStart > textBoldStartPosition && selectionEnd < textBoldEndPosition -> {
                        ssb.removeSpan(typeface)

                        var spanLeft = UnderlineSpan()
                        var spanRight = UnderlineSpan()

                        ssb.setSpan(
                            spanLeft,
                            textBoldStartPosition,
                            selectionStart,
                            SPAN_EXCLUSIVE_EXCLUSIVE
                        )

                        ssb.setSpan(
                            spanRight,
                            selectionEnd,
                            textBoldEndPosition,
                            SPAN_EXCLUSIVE_EXCLUSIVE
                        )

                    }
                    selectionStart == textBoldStartPosition && selectionEnd < textBoldEndPosition -> {
                        ssb.removeSpan(typeface)

                        var newSpan = UnderlineSpan()
                        ssb.setSpan(
                            newSpan,
                            selectionEnd,
                            textBoldEndPosition,
                            SPAN_EXCLUSIVE_INCLUSIVE
                        )
                    }
                    selectionStart == textBoldStartPosition && selectionEnd > textBoldEndPosition -> {
                        ssb.removeSpan(typeface)
                        ssb.setSpan(
                            typeface,
                            selectionStart,
                            selectionEnd,
                            SPAN_EXCLUSIVE_INCLUSIVE
                        )
                    }
                    selectionStart < textBoldStartPosition && selectionEnd == textBoldEndPosition -> {
                        ssb.removeSpan(typeface)
                        ssb.setSpan(
                            typeface,
                            selectionStart,
                            selectionEnd,
                            SPAN_EXCLUSIVE_INCLUSIVE
                        )
                    }
                    selectionStart > textBoldStartPosition && selectionEnd == textBoldEndPosition -> {
                        ssb.removeSpan(typeface)

                        var newSpan = UnderlineSpan()
                        ssb.setSpan(
                            newSpan,
                            textBoldStartPosition,
                            selectionStart,
                            SPAN_EXCLUSIVE_INCLUSIVE
                        )
                    }
                    selectionStart < textBoldStartPosition && selectionEnd > textBoldEndPosition -> {
                        ssb.removeSpan(typeface)
                        ssb.setSpan(
                            typeface,
                            selectionStart,
                            selectionEnd,
                            SPAN_EXCLUSIVE_INCLUSIVE
                        )
                    }
                }
            }
        }

    }


    override fun onDestroyActionMode(mode: ActionMode) {}
}