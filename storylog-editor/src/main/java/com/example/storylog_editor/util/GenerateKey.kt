package com.example.storylog_editor.util

import com.example.storylog_editor.model.EditerModel
import kotlin.random.Random

object GenerateKey {

    fun getKey(model: MutableList<EditerModel>): Long {
        var generatedLong = Random.nextLong()
        var size: Int

        while (true) {
            size = model.filter {
                it.id == generatedLong
            }.size
            if (size == 0)
                break
            else if (size > 0) {
                generatedLong = Random.nextLong()
            }
        }
        return generatedLong
    }
}