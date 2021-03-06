package com.example.storylog_editor.model


class InlineStyleRanges {
    var offset = 0
    var length = 0
    var style: String? = null

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is InlineStyleRanges) return false

        if (offset != other.offset) return false
        if (length != other.length) return false
        if (style != other.style) return false

        return true
    }

    override fun hashCode(): Int {
        var result = offset
        result = 31 * result + length
        result = 31 * result + (style?.hashCode() ?: 0)
        return result
    }

    override fun toString(): String {
        return "InlineStyleRanges(offset=$offset, length=$length, style=$style)"
    }
}

