package com.github.bin.util

import java.util.LinkedList

class BTree<T> {

    private val children = HashMap<Char, BTree<T>>()

    private var target: T? = null

    private operator fun get(key: Char): BTree<T>? = children[key]
    private operator fun get(key: String): BTree<T>? {
        var tree = this
        for (c in key) {
            tree = tree.children[c] ?: return null
        }
        return tree
    }

    private fun getOrPut(key: String): BTree<T> {
        var tree = this
        for (c in key) {
            tree = tree.children.computeIfAbsent(c) { BTree() }
        }
        return tree
    }

    operator fun set(string: String, target: T) {
        val bTree = getOrPut(string)
        bTree.target = target
    }

    fun getTarget(): T? {
        return target
    }

    fun toMap(): Map<String, T> {
        val map = LinkedHashMap<String, T>()
        val queue = LinkedList<Pair<String, Set<MutableMap.MutableEntry<Char, BTree<T>>>>>()
        queue.add("" to children.entries)
        // root 没有 target
        while (queue.isNotEmpty()) {
            val (str, set) = queue.removeFirst()
            for ((c, bTree) in set) {
                val s = str + c
                if (bTree.target !== null) {
                    map[s] = bTree.target!!
                }
                if (bTree.children.isNotEmpty()) {
                    queue.add(s to bTree.children.entries)
                }
            }
        }

        return map
    }
}
