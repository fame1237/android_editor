package chawan.fame.editerbook.model.editor


class Data {
    var text = ""
    var style: TextStyle? = null
}

enum class TextStyle {
    BOLD, ITALIC, NORMAL, UNDERLINE
}