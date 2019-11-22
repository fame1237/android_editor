package com.example.storylog_editor.model.contentrawstate

import com.example.storylog_editor.model.EditerModel
import com.google.gson.annotations.SerializedName

class ContentRawState {
    @SerializedName("blocks")
    var blocks: MutableList<EditerModel> = mutableListOf()

    override fun toString(): String {
        return "ContentRawState{" +
                ",blocks = '" + blocks + '\''.toString() +
                "}"
    }
}