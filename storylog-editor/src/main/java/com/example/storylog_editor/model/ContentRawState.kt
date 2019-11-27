package com.example.storylog_editor.model

import com.google.gson.annotations.SerializedName

class ContentRawState {
    @SerializedName("blocks")
    var blocks: MutableList<EditerModel> = mutableListOf()
}