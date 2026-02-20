package uk.co.savills.stonewood.util

fun <T : Any> MutableList<T>.addAll(itemMap: Map<Int, List<T>>) {
    var addCount = 0
    itemMap.forEach { set ->
        set.value.forEachIndexed { index, value ->
            add(set.key + index + addCount, value)
        }

        addCount += set.value.size
    }
}
