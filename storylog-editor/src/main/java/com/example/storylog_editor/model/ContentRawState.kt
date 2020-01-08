package com.example.storylog_editor.model

import com.google.gson.annotations.SerializedName

class ContentRawState {
    @SerializedName("blocks")
    var blocks: MutableList<EditerModel> = mutableListOf()

    @SerializedName("entityMap")
    var entityMap: EntityMap = EntityMap()

    override fun toString(): String {
        return "ContentRawState(blocks=$blocks, entityMap=$entityMap)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ContentRawState) return false

        if (blocks != other.blocks) return false
        if (entityMap != other.entityMap) return false

        return true
    }

    override fun hashCode(): Int {
        var result = blocks.hashCode()
        result = 31 * result + entityMap.hashCode()
        return result
    }


}