package com.example.storylog_editor.model


class Data {
    var src = ""
    var selection: Int = 0
    var style: TextStyle? = null
    var inlineStyleRange: MutableList<InlineStyleRanges> = mutableListOf()
    var alight: Alignment =
        Alignment.START

    override fun toString(): String {
        return "Data( src='$src', selection=$selection, style=$style, inlineStyleRange=$inlineStyleRange, alight=$alight)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Data) return false

        if (src != other.src) return false
        if (selection != other.selection) return false
        if (style != other.style) return false
        if (inlineStyleRange != other.inlineStyleRange) return false
        if (alight != other.alight) return false

        return true
    }

}

enum class TextStyle {
    BOLD, ITALIC, NORMAL, UNDERLINE, STRIKE_THROUGH
}

enum class Alignment {
    START, CENTER, END, INDENT
}

