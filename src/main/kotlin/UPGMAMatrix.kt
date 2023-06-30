import kotlin.math.max
import kotlin.math.min

class UPGMAMatrix(
    private val header: Set<String>,
    private val matrix: List<List<Double>>
) {
    companion object {
        fun fromSequence(input: List<List<Char>>): UPGMAMatrix {
            val build = MutableList(input.size) { MutableList(input.size) { 0.0 } }
            input.forEachIndexed { idx, list ->
                val others = input.drop(idx)
                list.forEachIndexed { innerIdx, c ->
                    others.indices.forEach {
                        build[it + idx][idx] += if (c == others[it][innerIdx]) 0.0 else 1.0
                    }
                }
            }
            return UPGMAMatrix(
                List(input.size) { (it + 65).toChar().toString() }.toSet(),
                build
            )
        }
    }

    fun solveOneStep(): UPGMAMatrix {
        val smallestIdx = findSmallestIdx()
        val col = header.elementAt(smallestIdx.first)
        val row = header.elementAt(smallestIdx.second)
        val newHeader = if (col.first() < row.first()) col + row else row + col
        val cHeader = header.minus(header.elementAt(smallestIdx.first)).minus(header.elementAt(smallestIdx.second))

        val m = matrix.map { it.filterIndexed { idx, _ -> idx != smallestIdx.first && idx != smallestIdx.second } }
            .filterIndexed { index, _ -> index != smallestIdx.first && index != smallestIdx.second }
            .plus(listOf(cHeader.map {
                val idx = header.indexOf(it)
                (matrix[max(idx, smallestIdx.first)][min(idx, smallestIdx.first)] * col.length + matrix[max(
                    idx,
                    smallestIdx.second
                )][min(idx, smallestIdx.second)] * row.length) / (col.length + row.length)
            })).map { it + 0.0 }
        return UPGMAMatrix(cHeader.plus(newHeader), m)
    }

    fun solved(): Boolean = matrix.size == 1

    fun newLinkPlusHeight(): Pair<Pair<String, String>, Double> {
        val s = smallestIdxAndVal()
        val fst = header.elementAt(s.first.first)
        val snd = header.elementAt(s.first.second)
        val newLink = if (fst.first() < snd.first()) fst to snd else snd to fst
        return newLink to s.second
    }

    private fun findSmallestIdx(): Pair<Int, Int> = smallestIdxAndVal().first

    private fun smallestIdxAndVal(): Pair<Pair<Int, Int>, Double> {
        var min = Double.MAX_VALUE
        var rowIdx = 0
        var colIdx = 0
        matrix.forEachIndexed { cIdx, row ->
            row.forEachIndexed { rIdx, v ->
                if (v < min && v > 0) {
                    min = v
                    rowIdx = rIdx
                    colIdx = cIdx
                }
            }
        }
        return colIdx to rowIdx to min
    }

    override fun toString(): String {
        val colWidth = MutableList(matrix.size) {0}
        val maxHeaderWidth = header.fold(0) {acc, next -> max(acc, next.length)}
        val stringBuilders = List(matrix.size) {StringBuilder()}

        matrix.indices.forEach {col ->
            val maxWidth = matrix.indices.fold(header.elementAt(col).length) {acc, next ->
                max(acc, matrix[next][col].toString().length)
            }
            colWidth[col] = maxWidth

            matrix.indices.forEach { row ->
                val value = matrix[row][col].toString()
                val curWidth = value.length
                val diff = maxWidth - curWidth
                stringBuilders[row].append(" $value${" ".repeat(diff + 1)}|")
            }
        }

        val headerUnderline = colWidth.fold(StringBuilder("-".repeat(maxHeaderWidth + 1)).append("+")) { acc, next -> acc.append("-".repeat(next + 2)).append("+")}

        val result = StringBuilder(" ".repeat(maxHeaderWidth + 1)).append("|")
        colWidth.forEachIndexed {idx, width ->
            val value = header.elementAt(idx)
            val curWidth = value.length
            val diff = width - curWidth
            result.append(" $value${" ".repeat(diff + 1)}|")
        }
        result.append("\n")
        result.append(headerUnderline).append("\n")
        stringBuilders.forEachIndexed {idx, buffer ->
            val value = header.elementAt(idx)
            val curWidth = value.length
            val diff = maxHeaderWidth - curWidth
            result.append("$value${" ".repeat(diff + 1)}|").append(buffer).append("\n")
        }

        return result.toString()
    }
}
