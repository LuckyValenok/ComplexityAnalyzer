package net.luckyvalenok.analyzer

import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import java.util.function.Function
import kotlin.math.pow
import kotlin.random.Random

fun main() {
    // f(v) = 1
    functionExecutionWithArray("1", 2000, 5) {
        return@functionExecutionWithArray 1
    }
    // f(v) = Σ(от 1 до n) v(n)
    functionExecutionWithArray("2", 2000, 5) {
        return@functionExecutionWithArray it.sum()
    }
    // f(v) = П(от 1 до n) v(n)
    functionExecutionWithArray("3", 2000, 5) {
        var result = 1
        for (i in it) {
            result *= i
        }
        return@functionExecutionWithArray result
    }
    // P(x) = Σ(от 1 до n) v(n) * x^(n - 1); x = 1.5
    functionExecutionWithArray("4.1", 2000, 5) {
        val x = 1.5
        var result = 0.0
        for (i in it.indices) {
            result += it[i] * x.pow(i)
        }
        return@functionExecutionWithArray result
    }
    // P(x) = v1 + x(x2 + x(v3 + ...)); x = 1.5
    functionExecutionWithArray("4.2", 2000, 5) {
        val x = 1.5
        var result = 0.0
        for (i in it.size downTo 1) {
            result *= x
            result += it[i - 1]
        }
        return@functionExecutionWithArray result
    }
    // Bubble Sort
    functionExecutionWithArray("5", 2000, 5) {
        bubbleSort(it)
        return@functionExecutionWithArray 0
    }
    // Quick Sort
    functionExecutionWithArray("6", 2000, 5) {
        quickSort(it)
        return@functionExecutionWithArray 0
    }
    // Tim Sort
    functionExecutionWithArray("7", 2000, 5) {
        it.sort() // (((
        return@functionExecutionWithArray 0
    }
    // f(x) = x * ... * x
    functionExecutionWithCount("8", 2000, 5) {
        val x = 5
        var result = 1
        for (i in 0 until it) {
            result *= x
        }
        return@functionExecutionWithCount result
    }
    /*
     f(x) = 1, если n = 0
     f(x) = x * (x^(n div 2)) ^ 2, если n - нечетное
     f(x) = (x^(n div 2)) ^ 2, если n - четное
     */
    functionExecutionWithCount("9", 2000, 5) {
        return@functionExecutionWithCount recPow(5, it)
    }
    // быстрый алгоритм возведения в степень
    functionExecutionWithCount("10", 2000, 5) {
        return@functionExecutionWithCount quickPow(5, it)
    }
    // классический алгоритм быстрого возведения в степень
    functionExecutionWithCount("11", 2000, 5) {
        return@functionExecutionWithCount quickPow1(5, it)
    }
    // Обычное матричное произведение матриц A (n x n) и B (n x n)
    functionExecutionWithCount("12", 2000, 1) {
        val size = it
        val a = generateMatrix(it)
        val b = generateMatrix(it)
        val c = Array(it) { IntArray(size) { 0 } }
        for (i in 0 until it) {
            for (j in 0 until it) {
                for (k in 0 until it) {
                    c[i][j] += a[i][k] * b[k][j]
                }
            }
        }
        return@functionExecutionWithCount 0
    }
    // Сортировка Шелла О(n * log^2 n)
    functionExecutionWithArray("13", 2000, 5) {
        shellSort(it)
        return@functionExecutionWithArray 0
    }
}

fun functionExecutionWithCount(
    name: String,
    maxCount: Int,
    countRepeatable: Int,
    function: Function<Int, Number>
) {
    val lists = ArrayList<List<String>>(maxCount)
    for (i in 1 until maxCount + 1) {
        var result = 0L
        for (j in 1 until countRepeatable + 1) {
            val start = System.nanoTime()
            function.apply(i)
            val stop = System.nanoTime()
            result += stop - start
        }
        result /= countRepeatable
        lists += listOf(i.toString(), result.toString())
    }
    csvWriter().writeAll(lists, "$name.csv", false)
}

fun functionExecutionWithArray(
    name: String,
    maxCount: Int,
    countRepeatable: Int,
    function: Function<IntArray, Number>
) {
    val lists = ArrayList<List<String>>(maxCount)
    for (i in 1 until maxCount + 1) {
        val array = generateVector(i)
        var result = 0L
        for (j in 1 until countRepeatable + 1) {
            val clonedArray = array.clone()
            val start = System.nanoTime()
            function.apply(clonedArray)
            val stop = System.nanoTime()
            result += stop - start
        }
        result /= countRepeatable
        lists += listOf(i.toString(), result.toString())
    }
    csvWriter().writeAll(lists, "$name.csv", false)
}

fun generateVector(size: Int): IntArray {
    return IntArray(size) {
        return@IntArray Random.nextInt()
    }
}

fun bubbleSort(arr: IntArray): IntArray {
    var swap = true
    while (swap) {
        swap = false
        for (i in 0 until arr.size - 1) {
            if (arr[i] > arr[i + 1]) {
                val temp = arr[i]
                arr[i] = arr[i + 1]
                arr[i + 1] = temp

                swap = true
            }
        }
    }
    return arr
}

fun quickSort(arr: IntArray): IntArray {
    if (arr.count() < 2) {
        return arr
    }

    val pivot = arr[arr.count() / 2]
    val equal = arr.filter { it == pivot }
    val less = arr.filter { it < pivot }
    val greater = arr.filter { it > pivot }

    return quickSort(less.toIntArray()) + equal + quickSort(greater.toIntArray())
}

fun recPow(x: Int, n: Int): Int {
    return if (n == 0) {
        1
    } else {
        val divedN = n / 2
        var f = recPow(x, divedN)
        f *= if (divedN % 2 == 1) {
            f * x
        } else {
            f
        }
        f
    }
}

fun quickPow(x: Int, n: Int): Int {
    var c = x
    var k = n
    var f = if (k % 2 == 1) {
        c
    } else {
        1
    }
    while (k != 0) {
        k /= 2
        c *= c
        if (k % 2 == 1) {
            f *= c
        }
    }
    return f
}

fun quickPow1(x: Int, n: Int): Int {
    var c = x
    var f = 1
    var k = n
    while (k != 0) {
        if (k % 2 == 0) {
            c *= c
            k /= 2
        } else {
            f *= c
            k--
        }
    }
    return f
}

fun generateMatrix(size: Int): Array<IntArray> {
    return Array(size) { IntArray(size) { Random.nextInt() } }
}

fun shellSort(arr: IntArray): Int {
    val n = arr.size

    var gap = n / 2
    while (gap > 0) {
        var i = gap
        while (i < n) {
            val temp = arr[i]

            var j = i
            while (j >= gap && arr[j - gap] > temp) {
                arr[j] = arr[j - gap]
                j -= gap
            }

            arr[j] = temp
            i += 1
        }
        gap /= 2
    }
    return 0
}