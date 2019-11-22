package com.example.storylog_editor.model


class EditerModel {
    var id: Long = 0
    var type: EditerViewType? = null
    var data: Data? = null
    var isFocus = false
    var showBorder = false
    var text = ""
    var inlineStyleRange: MutableList<InlineStyleRanges> = mutableListOf()

    override fun toString(): String {
        return "EditerModel(id=$id, type=$type, data=$data,  isFocus=$isFocus)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is EditerModel) return false

        if (id != other.id) return false
        if (type != other.type) return false
        if (data != other.data) return false
        if (isFocus != other.isFocus) return false
        if (showBorder != other.showBorder) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + (type?.hashCode() ?: 0)
        result = 31 * result + (data?.hashCode() ?: 0)
        result = 31 * result + isFocus.hashCode()
        result = 31 * result + showBorder.hashCode()
        return result
    }


}

enum class EditerViewType {
    QUOTE, IMAGE, LINE, EDIT_TEXT, HEADER
}