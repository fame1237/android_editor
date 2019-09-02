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


}

enum class TextStyle {
    BOLD, ITALIC, NORMAL, UNDERLINE, STRIKE_THROUGH
}

enum class Alignment {
    START, CENTER, END, INDENT
}

