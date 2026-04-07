package fr.iutlens.mmi.demo.utils

/**
 * Min-heap générique pour commonMain KMP (java.util.PriorityQueue n'existe pas en Wasm/JS).
 * Complexité : add O(log n), poll O(log n).
 */
class MinHeap<T>(private val comparator: Comparator<T>) {
    private val data = ArrayList<T>()

    val isEmpty: Boolean get() = data.isEmpty()

    fun add(item: T) {
        data.add(item)
        siftUp(data.size - 1)
    }

    fun poll(): T {
        val result = data[0]
        val last = data.removeAt(data.size - 1)
        if (data.isNotEmpty()) {
            data[0] = last
            siftDown(0)
        }
        return result
    }

    private fun siftUp(index: Int) {
        var i = index
        while (i > 0) {
            val parent = (i - 1) ushr 1
            if (comparator.compare(data[i], data[parent]) < 0) {
                val tmp = data[i]; data[i] = data[parent]; data[parent] = tmp
                i = parent
            } else break
        }
    }

    private fun siftDown(index: Int) {
        var i = index
        val n = data.size
        while (true) {
            val left = (i shl 1) + 1
            val right = left + 1
            var smallest = i
            if (left < n && comparator.compare(data[left], data[smallest]) < 0) smallest = left
            if (right < n && comparator.compare(data[right], data[smallest]) < 0) smallest = right
            if (smallest == i) break
            val tmp = data[i]; data[i] = data[smallest]; data[smallest] = tmp
            i = smallest
        }
    }
}
