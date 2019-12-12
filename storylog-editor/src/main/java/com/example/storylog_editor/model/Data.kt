package com.example.storylog_editor.model

import android.net.Uri


class Data {
    var src = ""
    var uri : String? = null
    var selection: Int = 0
//    var alight: Alignment =
//        Alignment.START

    override fun toString(): String {
        return "Data( src='$src', selection=$selection"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Data) return false

        if (src != other.src) return false
        if (selection != other.selection) return false

        return true
    }

}

enum class TextStyle {
    BOLD, ITALIC, NORMAL, UNDERLINE, STRIKE_THROUGH
}

//enum class Alignment {
//    START, CENTER, END, INDENT
//}

