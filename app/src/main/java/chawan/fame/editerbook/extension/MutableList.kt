package chawan.fame.editerbook.extension

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