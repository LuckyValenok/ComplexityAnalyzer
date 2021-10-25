package net.luckyvalenok.analyzer

import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import java.util.function.Consumer
import java.util.function.Function
import kotlin.math.ceil
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.random.Random

fun main() {
    // f(v) = 1
    analyzeWithArray("1", 2000, 5) {}
    // f(v) = Σ(от 1 до n) v(n)
    analyzeWithArray("2", 2000, 5) {
        it.sum()
    }
    // f(v) = П(от 1 до n) v(n)
    analyzeWithArray("3", 2000, 5) {
        var result = 1
        for (i in it) {
            result *= i
        }
    }
    // P(x) = Σ(от 1 до n) v(n) * x^(n - 1); x = 1.5
    analyzeWithArray("4.1", 2000, 5) {
        val x = 1.5
        var result = 0.0
        for (i in 1..it.size) {
            result += it[i - 1] * x.pow(i)
        }
    }
    // P(x) = v1 + x(x2 + x(v3 + ...)); x = 1.5
    analyzeWithArray("4.2", 2000, 5) {
        val x = 1.5
        var result = 0.0
        for (i in it.size downTo 1) {
            result *= x
            result += it[i - 1]
        }
    }
    // Bubble Sort
    analyzeWithArray("5", 2000, 5) {
        bubbleSort(it)
    }
    // Quick Sort
    analyzeWithArray("6", 2000, 5) {
        quickSort(it)
    }
    // Tim Sort
    analyzeWithArray("7", 2000, 5) {
        timSort(it)
    }
    // f(x) = x * ... * x
    analyzeWithCount("8", 2000, 5) {
        val x = 5
        var result = 1
        for (i in 0 until it) {
            result *= x
        }
    }
    /*
     f(x) = 1, если n = 0
     f(x) = x * (x^(n div 2)) ^ 2, если n - нечетное
     f(x) = (x^(n div 2)) ^ 2, если n - четное
     */
    analyzeWithCount("9", 2000, 5) {
        recPow(5, it)
    }
    // быстрый алгоритм возведения в степень
    analyzeWithCount("10", 2000, 5) {
        quickPow(5, it)
    }
    // классический алгоритм быстрого возведения в степень
    analyzeWithCount("11", 2000, 5) {
        quickPow1(5, it)
    }
    // Обычное матричное произведение матриц A (n x n) и B (n x n)
    analyzeWithCount("12", 500, 1) {
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
    }
    // Сортировка Шелла О(n * log^2 n)
    analyzeWithArray("13", 20000, 5) {
        shellSort(it)
    }
    analyze("14", 20000, 5, { generatePositiveVector(it, 100000) }) {
        countSort(it, it.size)
    }
    analyze("15", 20000, 5, { Random.nextInt(1, Int.MAX_VALUE) }) {
        factorsOf(it)
    }
    analyze("16", 10, 1, { generateRandomString(it) }) {
        permutation(it)
    }
    analyzeWithArray("17", 20000, 5) {
        selectionSort(it)
    }
    val list = SkipList<Int>()
    analyze("18", 20000, 5, { Random.nextInt() }) {
        list.insert(it)
    }
}

fun analyzeWithCount(name: String, maxCount: Int, countRepeatable: Int, consumer: Consumer<Int>) {
    analyze(name, maxCount, countRepeatable, { it }, consumer)
}

fun analyzeWithArray(name: String, maxCount: Int, countRepeatable: Int, consumer: Consumer<IntArray>) {
    analyze(name, maxCount, countRepeatable, { generateVector(it) }, consumer)
}

fun <T> analyze(name: String, maxCount: Int, countRepeatable: Int, function: Function<Int, T>, consumer: Consumer<T>) {
    val lists = ArrayList<List<String>>(maxCount)
    for (i in (1 until maxCount + 1)) {
        var result = 0L
        for (j in 1 until countRepeatable + 1) {
            val arg = function.apply(i)
            val time = timer { consumer.accept(arg) }
            if (result == 0L || result > time) {
                result = time
            }
        }
        result /= countRepeatable
        lists += listOf(i.toString(), result.toString())
    }
    csvWriter().writeAll(lists, "$name.csv", false)
}

fun timer(method: Runnable): Long {
    var time = System.nanoTime()
    method.run()
    time = System.nanoTime() - time
    return time / 100
}

fun generateVector(size: Int): IntArray {
    return IntArray(size) {
        return@IntArray Random.nextInt()
    }
}

fun generatePositiveVector(size: Int, limit: Int): IntArray {
    return IntArray(size) {
        var r = Random.nextInt(limit)
        if (r < 0)
            r *= -1
        return@IntArray r
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

var MIN_MERGE = 32

fun minRunLength(n: Int): Int {
    var j = n
    assert(j >= 0)

    var r = 0
    while (j >= MIN_MERGE) {
        r = r or (j and 1)
        j = j shr 1
    }
    return j + r
}

fun insertionSort(
    arr: IntArray, left: Int,
    right: Int
) {
    for (i in left + 1..right) {
        val temp = arr[i]
        var j = i - 1
        while (j >= left && arr[j] > temp) {
            arr[j + 1] = arr[j]
            j--
        }
        arr[j + 1] = temp
    }
}

fun merge(
    arr: IntArray, l: Int,
    m: Int, r: Int
) {
    val len1 = m - l + 1
    val len2 = r - m
    val left = IntArray(len1)
    val right = IntArray(len2)
    for (x in 0 until len1) {
        left[x] = arr[l + x]
    }
    for (x in 0 until len2) {
        right[x] = arr[m + 1 + x]
    }
    var i = 0
    var j = 0
    var k = l

    while (i < len1 && j < len2) {
        if (left[i] <= right[j]) {
            arr[k] = left[i]
            i++
        } else {
            arr[k] = right[j]
            j++
        }
        k++
    }

    while (i < len1) {
        arr[k] = left[i]
        k++
        i++
    }

    while (j < len2) {
        arr[k] = right[j]
        k++
        j++
    }
}

fun timSort(arr: IntArray) {
    val n = arr.size
    val minRun = minRunLength(MIN_MERGE)

    var i = 0
    while (i < n) {
        insertionSort(
            arr, i,
            (i + MIN_MERGE - 1).coerceAtMost(n - 1)
        )
        i += minRun
    }

    var size = minRun
    while (size < n) {
        var left = 0
        while (left < n) {
            val mid = left + size - 1
            val right = (left + 2 * size - 1).coerceAtMost(n - 1)

            if (mid < right) merge(arr, left, mid, right)
            left += 2 * size
        }
        size *= 2
    }
}

fun countSort(arr: IntArray, size: Int) {
    val output = IntArray(size + 1)
    var max = arr[0]
    for (i in 1 until size) {
        if (arr[i] > max) max = arr[i]
    }
    val count = IntArray(max + 1)
    for (i in 0 until max) {
        count[i] = 0
    }
    for (i in 0 until size) {
        count[arr[i]]++
    }
    for (i in 1..max) {
        count[i] += count[i - 1]
    }
    for (i in size - 1 downTo 0) {
        output[count[arr[i]] - 1] = arr[i]
        count[arr[i]]--
    }
    if (size >= 0) System.arraycopy(output, 0, arr, 0, size)
}

fun permutation(s: String): List<String> {
    var res = ArrayList<String>()
    if (s.length == 1) {
        res.add(s)
    } else if (s.length > 1) {
        val lastIndex = s.length - 1
        val last = s.substring(lastIndex)
        val rest = s.substring(0, lastIndex)
        res = merge(permutation(rest), last)
    }
    return res
}

fun merge(list: List<String>, c: String): ArrayList<String> {
    val res = ArrayList<String>()
    for (s in list) {
        for (i in 0..s.length) {
            val ps = StringBuffer(s).insert(i, c).toString()
            res.add(ps)
        }
    }
    return res
}

fun generateRandomString(length: Int): String {
    val random = java.util.Random()
    val password = StringBuilder()
    for (i in 0 until length) {
        password.append(random.nextInt(Character.MAX_VALUE.code).toChar())
    }
    return password.toString()
}

fun selectionSort(numbers: IntArray) {
    var min: Int
    var temp: Int
    for (index in 0 until numbers.size - 1) {
        min = index
        for (scan in index + 1 until numbers.size) {
            if (numbers[scan] < numbers[min]) min = scan
        }
        temp = numbers[min]
        numbers[min] = numbers[index]
        numbers[index] = temp
    }
}

fun factorsOf(arg: Int): IntArray {
    var arg = arg
    val limit = ceil(sqrt(arg.toDouble())).toInt()
    val numArray = IntArray(limit)
    var index = 0
    for (i in 1..limit) {
        if (arg % i == 0) {
            numArray[index++] = i
            arg /= i
        }
    }
    numArray[index] = arg
    return numArray
}