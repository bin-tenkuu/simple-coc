package com.github.bin.util

/**
 * @author bin
 * @version 1.0.0
 * @since 2023/7/9
 */

private fun String.split(): ArrayList<String> {
    val result = ArrayList<String>(10)
    val length = length

    var currentOffset = 0
    var nextIndex = indexOf(' ', currentOffset)
    while (nextIndex >= 0) {
        if (currentOffset < nextIndex) {
            result.add(substring(currentOffset, nextIndex))
        }
        for (i in currentOffset + 1 until length) {
            if (this[i] != ' ') {
                currentOffset = i
                break
            }
        }
        nextIndex = indexOf(' ', currentOffset)
    }
    if (currentOffset < length) {
        result.add(substring(currentOffset, length))
    }
    return result
}
