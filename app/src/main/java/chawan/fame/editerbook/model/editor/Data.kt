package chawan.fame.editerbook.model.editor


class Data {
    var text = ""
    var src = ""
    var selection: Int = 0
    var style: TextStyle? = null
    var inlineStyleRange: MutableList<InlineStyleRanges> = mutableListOf()
    var alight: Alignment = Alignment.START

    override fun toString(): String {
        return "Data(text='$text', src='$src', selection=$selection, style=$style, inlineStyleRange=$inlineStyleRange, alight=$alight)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Data) return false

        if (text != other.text) return false
        if (src != other.src) return false
        if (selection != other.selection) return false
        if (style != other.style) return false
        if (inlineStyleRange != other.inlineStyleRange) return false
        if (alight != other.alight) return false

        return true
    }

    override fun hashCode(): Int {
        var result = text.hashCode()
        result = 31 * result + src.hashCode()
        result = 31 * result + selection
        result = 31 * result + (style?.hashCode() ?: 0)
        result = 31 * result + inlineStyleRange.hashCode()
        result = 31 * result + alight.hashCode()
        return result
    }


}

enum class TextStyle {
    BOLD, ITALIC, NORMAL, UNDERLINE, STRIKE_THROUGH
}

enum class Alignment {
    START, CENTER, END, INDENT
}

