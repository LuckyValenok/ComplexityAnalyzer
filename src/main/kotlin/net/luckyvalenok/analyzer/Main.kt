import com.github.doyaaaaaken.kotlincsv.dsl.csvWriter
import java.util.function.Function
import kotlin.math.pow
import kotlin.random.Random

fun main() {
    // f(v) = 1
    functionExecution("one", 2000, 5) {
        return@functionExecution 1
    }
    // f(v) = Σ(от 1 до n) v(n)
    functionExecution("two", 2000, 5) {
        return@functionExecution it.sum()
    }
    // f(v) = П(от 1 до n) v(n)
    functionExecution("three", 2000, 5) {
        var result = 1
        for (i in it) {
            result *= i
        }
        return@functionExecution result
    }
    // P(x) = Σ(от 1 до n) v(n) * x^(n - 1); x = 1.5
    functionExecution("four1", 2000, 5) {
        val x = 1.5
        var result = 0.0
        for (i in it.indices) {
            result += it[i] * x.pow(i)
        }
        return@functionExecution result
    }
    // P(x) = v1 + x(x2 + x(v3 + ...)); x = 1.5
    functionExecution("four2", 2000, 5) {
        val x = 1.5
        var result = 0.0
        for (i in it.size downTo 1) {
            result *= x
            result += it[i - 1]
        }
        return@functionExecution result
    }
    // Bubble Sort
    functionExecution("five", 2000, 5) {
        bubbleSort(it)
        return@functionExecution 0
    }
    // Quick Sort
    functionExecution("six", 2000, 5) {
        quickSort(it)
        return@functionExecution 0
    }
    // Tim Sort
    functionExecution("seven", 2000, 5) {
        it.sort() // (((
        return@functionExecution 0
    }
}

fun functionExecution(
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
            val clonedArray = array.clone();
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