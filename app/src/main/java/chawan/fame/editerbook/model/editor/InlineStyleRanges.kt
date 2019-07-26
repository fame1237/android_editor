package chawan.fame.editerbook.model.editor


class InlineStyleRanges {
    var offset = 0
    var lenght = 0
    var style: TextStyle? = null



    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is InlineStyleRanges) return false

        if (offset != other.offset) return false
        if (lenght != other.lenght) return false
        if (style != other.style) return false

        return true
    }

    override fun hashCode(): Int {
        var result = offset
        result = 31 * result + lenght
        result = 31 * result + (style?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "InlineStyleRanges(offset=$offset, lenght=$lenght, style=$style)"
    }
}

