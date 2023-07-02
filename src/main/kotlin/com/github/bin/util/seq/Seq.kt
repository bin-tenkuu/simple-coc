package com.github.bin.util.seq

import java.util.*
import java.util.concurrent.ForkJoinPool
import java.util.concurrent.ForkJoinTask
import java.util.function.BiFunction
import java.util.function.Consumer
import java.util.function.Predicate
import java.util.function.UnaryOperator


/**
 * @author bin
 * @version 1.0.0
 * @since 2023/7/2
 */
private object StopException : RuntimeException() {

    override fun fillInStackTrace(): Throwable {
        return this
    }
}

fun interface Seq<T> : (Consumer<T>) -> Unit {
    override operator fun invoke(consumer: Consumer<T>)

    fun <E> map(function: java.util.function.Function<T, E>): Seq<E> {
        return Seq { c -> invoke { t -> c.accept(function.apply(t)) } }
    }

    fun <E> flatMap(function: java.util.function.Function<T, Seq<E>>): Seq<E> {
        return Seq { c -> invoke { t -> function.apply(t).invoke(c) } }
    }

    fun filter(predicate: Predicate<T>): Seq<T> {
        return Seq { c ->
            invoke { t ->
                if (predicate.test(t)) {
                    c.accept(t)
                }
            }
        }
    }

    fun consumeTillStop(consumer: Consumer<T>) {
        try {
            invoke(consumer)
        } catch (ignore: StopException) {
        }
    }

    fun take(n: Int): Seq<T> {
        return Seq { c ->
            val i = intArrayOf(n)
            consumeTillStop { t ->
                if (i[0]-- > 0) {
                    c.accept(t)
                } else {
                    stop()
                }
            }
        }
    }

    fun drop(n: Int): Seq<T> {
        return Seq { c ->
            val a = intArrayOf(n - 1)
            invoke { t ->
                if (a[0] < 0) {
                    c.accept(t)
                } else {
                    a[0]--
                }
            }
        }
    }

    fun onEach(consumer: Consumer<T>): Seq<T> {
        return Seq { c -> invoke(consumer.andThen(c)) }
    }

    fun <E, R> zip(iterable: Iterable<E>, function: BiFunction<T, E, R>): Seq<R> {
        return Seq { c ->
            val iterator = iterable.iterator()
            consumeTillStop { t ->
                if (iterator.hasNext()) {
                    c.accept(function.apply(t, iterator.next()))
                } else {
                    stop()
                }
            }
        }
    }

    fun join(sep: String?): String {
        val joiner = StringJoiner(sep)
        invoke { t -> joiner.add(t.toString()) }
        return joiner.toString()
    }

    fun toList(): List<T> {
        val list = ArrayList<T>()
        invoke(list::add)
        return list
    }

    fun cache(): Seq<T> {
        val arraySeq: ArraySeq<T> = ArraySeq()
        invoke { t -> arraySeq.add(t) }
        return arraySeq
    }

    fun parallel(): Seq<T> {
        val pool = ForkJoinPool.commonPool()
        return Seq { c ->
            map { t ->
                pool.submit { c.accept(t) }
            }.cache().invoke { obj: ForkJoinTask<*> -> obj.join() }
        }
    }

    fun asyncConsume(consumer: Consumer<T>) {
        val pool = ForkJoinPool.commonPool()
        map { t -> pool.submit { consumer.accept(t) } }.cache().invoke { obj -> obj.join() }
    }

    companion object {
        fun <T> unit(t: T): Seq<T> {
            return Seq { c ->
                c.accept(t)
            }
        }

        fun <T> stop(): T {
            throw StopException
        }

        fun underscoreToCamel(str: String): String {
            // Java没有首字母大写方法，随便现写一个
            val capitalize = UnaryOperator { s: String -> s.substring(0, 1).uppercase(Locale.getDefault()) + s.substring(1).lowercase(Locale.getDefault()) }
            // 利用生成器构造一个方法的流
            val seq = Seq<UnaryOperator<String>> { c ->
                // yield第一个小写函数
                c.accept { it.lowercase(Locale.getDefault()) }
                // 这里IDEA会告警，提示死循环风险，无视即可
                while (true) {
                    // 按需yield首字母大写函数
                    c.accept(capitalize)
                }
            }
            val split: List<String> = str.split("_".toRegex()).dropLastWhile { it.isEmpty() }.toList()
            // 这里的zip和join都在上文给出了实现
            return seq.zip(split) { f, sub -> f.apply(sub) }.join("")
        }

        fun <T> of(vararg ts: T): Seq<T> {
            val asList = listOf(*ts)
            return Seq { c -> asList.forEach(c) }
        }

        fun ofJson(node: Any?): Seq<Any?> {
            return ofTree(node) { n ->
                Seq { c ->
                    if (n is Iterable<*>) {
                        n.forEach(c)
                    } else if (n is Map<*, *>) {
                        n.values.forEach(c)
                    }
                }
            }
        }

        // 递归函数
        fun <N> scanTree(c: Consumer<N>, node: N, sub: java.util.function.Function<N, Seq<N>>) {
            c.accept(node)
            sub.apply(node).invoke { n ->
                if (n != null) {
                    scanTree(c, n, sub)
                }
            }
        }

        // 通用方法，可以遍历任何树
        fun <N> ofTree(node: N, sub: java.util.function.Function<N, Seq<N>>): Seq<N> {
            return Seq { c -> scanTree(c, node, sub) }
        }

        fun cartesian(vararg lists: List<Int>): Seq<Int> {
            return Seq { c ->
                val size = lists.size
                val sums = IntArray(size - 1)
                val indexs = IntArray(size)
                var i = 0
                while (i >= 0) {
                    val list = lists[i]
                    if (i + 1 == size) {
                        for (n in list) {
                            c.accept(sums[size - 2] + n)
                        }
                        i--
                    } else {
                        if (indexs[i] >= list.size) {
                            i--
                            continue
                        }
                        if (i == 0) {
                            sums[i] = list[indexs[i]]
                        } else {
                            sums[i] = list[indexs[i]] + sums[i - 1]
                        }
                        indexs[i]++
                        for (n in (i + 1) until size - 1) {
                            indexs[n] = 0
                        }
                        i++
                    }
                }
            }
        }
    }
}


class ArraySeq<T> : ArrayList<T>(), Seq<T> {
    override fun invoke(consumer: Consumer<T>) {
        forEach(consumer)
    }
}

fun main() {
    Seq.cartesian(
            listOf(1, 2, 3),
            listOf(4, 5, 6),
            listOf(7, 8, 9)
    ).invoke {
        println(it)
    }
}
