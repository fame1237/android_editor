package com.example.storylog_editor.model


class Data {
    var src = ""
    var selection: Int = 0
    var style: String? = null
    var alight: Alignment = Alignment.START
}



enum class Alignment {
    START, CENTER, END, INDENT
}

