package com.example.storylog_editor.extension

fun <T> Iterable<T>.filterGetIndex(predicate: (T) -> Boolean): Int {
    return filterTo(predicate)
}

fun <T> Iterable<T>.filterTo(predicate: (T) -> Boolean): Int {
    for ((i, element) in this.withIndex()) {
        if (predicate(element)) {
            return i
        }
    }
    return 0
}

fun <T> Iterable<T>.filterGetArrayIndex(predicate: (T) -> Boolean): MutableList<Int> {
    return filterToReturnIndex(predicate)
}

fun <T> Iterable<T>.filterToReturnIndex(predicate: (T) -> Boolean): MutableList<Int> {
    var integerList: MutableList<Int> = mutableListOf()
    for ((i, element) in this.withIndex()) {
        if (predicate(element)) {
            integerList.add(i)
        }
    }
    return integerList
}
