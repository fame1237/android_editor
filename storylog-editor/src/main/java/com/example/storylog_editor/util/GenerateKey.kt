package com.example.storylog_editor.util

import com.example.storylog_editor.model.EditerModel
import org.apache.commons.lang3.RandomStringUtils
import kotlin.random.Random

object GenerateKey {

//    fun getKey(model: MutableList<EditerModel>): Long {
//        var generatedLong = Random.nextLong()
//        var size: Int
//
//        while (true) {
//            size = model.filter {
//                it.key == generatedLong
//            }.size
//            if (size == 0)
//                break
//            else if (size > 0) {
//                generatedLong = Random.nextLong()
//            }
//        }
//        return generatedLong
//    }

    fun getStringKey(model: MutableList<EditerModel>): String {
        var generateString = RandomStringUtils.randomAlphabetic(5)
        var size: Int

        while (true) {
            size = model.filter {
                it.key == generateString
            }.size
            if (size == 0)
                break
            else if (size > 0) {
                generateString = RandomStringUtils.randomAlphabetic(5)
            }
        }
        return generateString
    }
}