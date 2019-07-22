package chawan.fame.editerbook.model.editor

import android.view.Gravity


class Data {
    var text = ""
    var src = ""
    var style: TextStyle? = null
    var inlineStyleRange: MutableList<InlineStyleRanges> = mutableListOf()
    var alight = Gravity.START

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Data) return false

        if (text != other.text) return false
        if (style != other.style) return false
        if (inlineStyleRange != other.inlineStyleRange) return false

        return true
    }

    override fun hashCode(): Int {
        var result = text.hashCode()
        result = 31 * result + (style?.hashCode() ?: 0)
        result = 31 * result + inlineStyleRange.hashCode()
        return result
    }

    override fun toString(): String {
        return "Data(text='$text', style=$style, inlineStyleRange=$inlineStyleRange)"
    }


}

enum class TextStyle {
    BOLD, ITALIC, NORMAL, UNDERLINE
}

