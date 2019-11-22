package com.example.storylog_editor.util

import android.graphics.Typeface
import android.text.SpannableStringBuilder
import android.text.style.CharacterStyle
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.util.Log
import android.widget.EditText
import com.example.storylog_editor.model.InlineStyleRanges
import com.example.storylog_editor.model.TextStyle


object CheckStyle {

    fun checkSpan(editText: EditText?, text: CharSequence): MutableList<InlineStyleRanges> {

        val currentStringSSB = if (editText != null) SpannableStringBuilder(editText.text)
        else
            SpannableStringBuilder(text)

        var inlineStyleRangesList: MutableList<InlineStyleRanges> = mutableListOf()

        var typeface = currentStringSSB.getSpans(0, currentStringSSB.length, CharacterStyle::class.java)

        if (typeface.isNotEmpty()) {
            typeface.forEach {
                var inlineStyleRanges = InlineStyleRanges()
                val textTypeFaceStartPosition = currentStringSSB.getSpanStart(it)
                val textTypeFaceEndPosition = currentStringSSB.getSpanEnd(it)
                Log.e("typeFaceStartPosition", textTypeFaceStartPosition.toString())
                Log.e("typeFaceEndPosition", textTypeFaceEndPosition.toString())
                Log.e("typeFace", it.toString())

                if (it is StyleSpan && it.style == Typeface.BOLD) {
                    inlineStyleRanges.style = "BOLD"
                    inlineStyleRanges.offset = textTypeFaceStartPosition
                    inlineStyleRanges.lenght = textTypeFaceEndPosition - textTypeFaceStartPosition
                    var filterList = inlineStyleRangesList.filter {
                        it.lenght == inlineStyleRanges.lenght &&
                                it.offset == inlineStyleRanges.offset &&
                                it.style == inlineStyleRanges.style
                    }
                    if (filterList.isEmpty())
                        inlineStyleRangesList.add(inlineStyleRanges)

                } else if (it is StyleSpan && it.style == Typeface.ITALIC) {
                    inlineStyleRanges.style = "ITALIC"
                    inlineStyleRanges.offset = textTypeFaceStartPosition
                    inlineStyleRanges.lenght = textTypeFaceEndPosition - textTypeFaceStartPosition
                    var filterList = inlineStyleRangesList.filter {
                        it.lenght == inlineStyleRanges.lenght &&
                                it.offset == inlineStyleRanges.offset &&
                                it.style == inlineStyleRanges.style
                    }
                    if (filterList.isEmpty())
                        inlineStyleRangesList.add(inlineStyleRanges)
                } else if (it is UnderlineSpan) {
                    inlineStyleRanges.style = "UNDERLINE"
                    inlineStyleRanges.offset = textTypeFaceStartPosition
                    inlineStyleRanges.lenght = textTypeFaceEndPosition - textTypeFaceStartPosition
                    var filterList = inlineStyleRangesList.filter {
                        it.lenght == inlineStyleRanges.lenght &&
                                it.offset == inlineStyleRanges.offset &&
                                it.style == inlineStyleRanges.style
                    }
                    if (filterList.isEmpty())
                        inlineStyleRangesList.add(inlineStyleRanges)

                } else if (it is StrikethroughSpan) {
                    inlineStyleRanges.style = "STRIKETHROUGH"
                    inlineStyleRanges.offset = textTypeFaceStartPosition
                    inlineStyleRanges.lenght = textTypeFaceEndPosition - textTypeFaceStartPosition
                    var filterList = inlineStyleRangesList.filter {
                        it.lenght == inlineStyleRanges.lenght &&
                                it.offset == inlineStyleRanges.offset &&
                                it.style == inlineStyleRanges.style
                    }
                    if (filterList.isEmpty())
                        inlineStyleRangesList.add(inlineStyleRanges)

                } else {
                    inlineStyleRanges.style = TextStyle.NORMAL
                    inlineStyleRanges.offset = textTypeFaceStartPosition
                    inlineStyleRanges.lenght = textTypeFaceEndPosition - textTypeFaceStartPosition
                    var filterList = inlineStyleRangesList.filter {
                        it.lenght == inlineStyleRanges.lenght &&
                                it.offset == inlineStyleRanges.offset &&
                                it.style == inlineStyleRanges.style
                    }
                    if (filterList.isEmpty())
                        inlineStyleRangesList.add(inlineStyleRanges)
                }
            }
        }
        return inlineStyleRangesList
    }

}