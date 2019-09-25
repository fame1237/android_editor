package com.example.storylog_editor.util

import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.CharacterStyle
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.widget.EditText


object SetStyle {


    fun setBold(bodyView: EditText) {
        val cs = StyleSpan(Typeface.BOLD)
        val selectionStart = bodyView.selectionStart
        val selectionEnd = bodyView!!.selectionEnd
        val ssb = SpannableStringBuilder(bodyView!!.text)

        if (selectionEnd == selectionStart) {
        } else {
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
                        Spanned.SPAN_EXCLUSIVE_INCLUSIVE
                    )
                } else {
                    typeface.forEach {
                        if (it is StyleSpan) {
                            if (it.style == Typeface.BOLD)
                                setSpan(it, selectionStart, selectionEnd, ssb, bodyView)
                        }
                    }
                }
            } else {
                ssb.setSpan(
                    cs,
                    selectionStart,
                    selectionEnd,
                    Spanned.SPAN_EXCLUSIVE_INCLUSIVE
                )
            }

            bodyView.text = ssb
            bodyView.setSelection(selectionEnd)
        }
    }

    fun setItalic(bodyView: EditText) {
        val cs = StyleSpan(Typeface.ITALIC)
        val selectionStart = bodyView.selectionStart
        val selectionEnd = bodyView!!.selectionEnd
        val ssb = SpannableStringBuilder(bodyView!!.text)

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
                    Spanned.SPAN_EXCLUSIVE_INCLUSIVE
                )
            } else {
                typeface.forEach {
                    if (it is StyleSpan) {
                        if (it.style == Typeface.ITALIC)
                            setSpan(it, selectionStart, selectionEnd, ssb, bodyView)
                    }
                }
            }
        } else {
            ssb.setSpan(
                cs,
                selectionStart,
                selectionEnd,
                Spanned.SPAN_EXCLUSIVE_INCLUSIVE
            )
        }
        bodyView!!.text = ssb
        bodyView.setSelection(selectionEnd)
    }

    fun setUnderLine(bodyView: EditText) {
        val cs = UnderlineSpan()
        val selectionStart = bodyView.selectionStart
        val selectionEnd = bodyView!!.selectionEnd
        val ssb = SpannableStringBuilder(bodyView!!.text)

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
                    Spanned.SPAN_EXCLUSIVE_INCLUSIVE
                )
            } else {
                typeface.forEach {
                    if (it is UnderlineSpan) {
                        setSpan(it, selectionStart, selectionEnd, ssb, bodyView)
                    }
                }
            }
        } else {
            ssb.setSpan(
                cs,
                selectionStart,
                selectionEnd,
                Spanned.SPAN_EXCLUSIVE_INCLUSIVE
            )
        }
        bodyView!!.text = ssb
        bodyView.setSelection(selectionEnd)
    }

    fun setStrikethrough(bodyView: EditText) {
        val cs = StrikethroughSpan()
        val selectionStart = bodyView.selectionStart
        val selectionEnd = bodyView!!.selectionEnd
        val ssb = SpannableStringBuilder(bodyView!!.text)

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
                    Spanned.SPAN_EXCLUSIVE_INCLUSIVE
                )
            } else {
                typeface.forEach {
                    if (it is UnderlineSpan) {
                        setSpan(it, selectionStart, selectionEnd, ssb, bodyView)
                    }
                }
            }
        } else {
            ssb.setSpan(
                cs,
                selectionStart,
                selectionEnd,
                Spanned.SPAN_EXCLUSIVE_INCLUSIVE
            )
        }
        bodyView!!.text = ssb
        bodyView.setSelection(selectionEnd)
    }

    fun setSpan(
        typeface: CharacterStyle, selectionStart: Int, selectionEnd: Int,
        ssb: SpannableStringBuilder, bodyView: EditText
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
                            Spanned.SPAN_EXCLUSIVE_INCLUSIVE
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
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        )

                        ssb.setSpan(
                            spanRight,
                            selectionEnd,
                            textBoldEndPosition,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        )

                    }
                    selectionStart == textBoldStartPosition && selectionEnd < textBoldEndPosition -> {
                        ssb.removeSpan(typeface)

                        var newSpan = StyleSpan(typeface.style)
                        ssb.setSpan(
                            newSpan,
                            selectionEnd,
                            textBoldEndPosition,
                            Spanned.SPAN_EXCLUSIVE_INCLUSIVE
                        )
                    }
                    selectionStart == textBoldStartPosition && selectionEnd > textBoldEndPosition -> {
                        ssb.removeSpan(typeface)
                        ssb.setSpan(
                            typeface,
                            selectionStart,
                            selectionEnd,
                            Spanned.SPAN_EXCLUSIVE_INCLUSIVE
                        )
                    }
                    selectionStart < textBoldStartPosition && selectionEnd == textBoldEndPosition -> {
                        ssb.removeSpan(typeface)
                        ssb.setSpan(
                            typeface,
                            selectionStart,
                            selectionEnd,
                            Spanned.SPAN_EXCLUSIVE_INCLUSIVE
                        )
                    }
                    selectionStart > textBoldStartPosition && selectionEnd == textBoldEndPosition -> {
                        ssb.removeSpan(typeface)

                        var newSpan = StyleSpan(typeface.style)
                        ssb.setSpan(
                            newSpan,
                            textBoldStartPosition,
                            selectionStart,
                            Spanned.SPAN_EXCLUSIVE_INCLUSIVE
                        )
                    }
                    selectionStart < textBoldStartPosition && selectionEnd > textBoldEndPosition -> {
                        ssb.removeSpan(typeface)
                        ssb.setSpan(
                            typeface,
                            selectionStart,
                            selectionEnd,
                            Spanned.SPAN_EXCLUSIVE_INCLUSIVE
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
                            Spanned.SPAN_EXCLUSIVE_INCLUSIVE
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
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        )

                        ssb.setSpan(
                            spanRight,
                            selectionEnd,
                            textBoldEndPosition,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        )

                    }
                    selectionStart == textBoldStartPosition && selectionEnd < textBoldEndPosition -> {
                        ssb.removeSpan(typeface)

                        var newSpan = UnderlineSpan()
                        ssb.setSpan(
                            newSpan,
                            selectionEnd,
                            textBoldEndPosition,
                            Spanned.SPAN_EXCLUSIVE_INCLUSIVE
                        )
                    }
                    selectionStart == textBoldStartPosition && selectionEnd > textBoldEndPosition -> {
                        ssb.removeSpan(typeface)
                        ssb.setSpan(
                            typeface,
                            selectionStart,
                            selectionEnd,
                            Spanned.SPAN_EXCLUSIVE_INCLUSIVE
                        )
                    }
                    selectionStart < textBoldStartPosition && selectionEnd == textBoldEndPosition -> {
                        ssb.removeSpan(typeface)
                        ssb.setSpan(
                            typeface,
                            selectionStart,
                            selectionEnd,
                            Spanned.SPAN_EXCLUSIVE_INCLUSIVE
                        )
                    }
                    selectionStart > textBoldStartPosition && selectionEnd == textBoldEndPosition -> {
                        ssb.removeSpan(typeface)

                        var newSpan = UnderlineSpan()
                        ssb.setSpan(
                            newSpan,
                            textBoldStartPosition,
                            selectionStart,
                            Spanned.SPAN_EXCLUSIVE_INCLUSIVE
                        )
                    }
                    selectionStart < textBoldStartPosition && selectionEnd > textBoldEndPosition -> {
                        ssb.removeSpan(typeface)
                        ssb.setSpan(
                            typeface,
                            selectionStart,
                            selectionEnd,
                            Spanned.SPAN_EXCLUSIVE_INCLUSIVE
                        )
                    }
                }
            }
        } else if (typeface is StrikethroughSpan) {
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
                            Spanned.SPAN_EXCLUSIVE_INCLUSIVE
                        )
                    }
                    selectionStart == textBoldStartPosition && selectionEnd == textBoldEndPosition -> {
                        ssb.removeSpan(typeface)
                    }
                    selectionStart > textBoldStartPosition && selectionEnd < textBoldEndPosition -> {
                        ssb.removeSpan(typeface)

                        var spanLeft = StrikethroughSpan()
                        var spanRight = StrikethroughSpan()

                        ssb.setSpan(
                            spanLeft,
                            textBoldStartPosition,
                            selectionStart,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        )

                        ssb.setSpan(
                            spanRight,
                            selectionEnd,
                            textBoldEndPosition,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        )

                    }
                    selectionStart == textBoldStartPosition && selectionEnd < textBoldEndPosition -> {
                        ssb.removeSpan(typeface)

                        var newSpan = StrikethroughSpan()
                        ssb.setSpan(
                            newSpan,
                            selectionEnd,
                            textBoldEndPosition,
                            Spanned.SPAN_EXCLUSIVE_INCLUSIVE
                        )
                    }
                    selectionStart == textBoldStartPosition && selectionEnd > textBoldEndPosition -> {
                        ssb.removeSpan(typeface)
                        ssb.setSpan(
                            typeface,
                            selectionStart,
                            selectionEnd,
                            Spanned.SPAN_EXCLUSIVE_INCLUSIVE
                        )
                    }
                    selectionStart < textBoldStartPosition && selectionEnd == textBoldEndPosition -> {
                        ssb.removeSpan(typeface)
                        ssb.setSpan(
                            typeface,
                            selectionStart,
                            selectionEnd,
                            Spanned.SPAN_EXCLUSIVE_INCLUSIVE
                        )
                    }
                    selectionStart > textBoldStartPosition && selectionEnd == textBoldEndPosition -> {
                        ssb.removeSpan(typeface)

                        var newSpan = StrikethroughSpan()
                        ssb.setSpan(
                            newSpan,
                            textBoldStartPosition,
                            selectionStart,
                            Spanned.SPAN_EXCLUSIVE_INCLUSIVE
                        )
                    }
                    selectionStart < textBoldStartPosition && selectionEnd > textBoldEndPosition -> {
                        ssb.removeSpan(typeface)
                        ssb.setSpan(
                            typeface,
                            selectionStart,
                            selectionEnd,
                            Spanned.SPAN_EXCLUSIVE_INCLUSIVE
                        )
                    }
                }
            }
        }
    }
}