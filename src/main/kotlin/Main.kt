fun main(args: Array<String>) {
    println("Kind of input: Sequence (S), Matrix (M):")
    val input = readln()
    when (input) {
        "s", "S" -> fromSequence()
        "m", "M" -> fromMatrix()
        else -> println("Wrong input restart program")
    }
}

fun fromSequence() {
    println("Number of entries:")
    val sequenceNb = readln().toInt()
    println("Enter each sequences, separate letters by space and sequence by new line:")
    val sequence = (0 until sequenceNb).map {
        print("Sequence ${(it + 65).toChar()}: ")
        readln().split(" ").map { it.toCharArray().first() }
    }

    val m = UPGMAMatrix.fromSequence(sequence)
    solve(m)
}

fun fromMatrix() {
    println("Number of entries:")
    val nb = readln().toInt()
    println("Enter each row of the matrix. Only enter numbers, 0's will be filled automatically. Use space between numbers.")
    val matrix = listOf(List(nb) { 0.0 }) + (1 until nb).map {
        print("Row ${(it + 65).toChar()}: ")
        val l = readln().split(" ").map { it.toDouble() }
        l + List(nb - l.size) { 0.0 }
    }

    val m = UPGMAMatrix(List(nb) { (it + 65).toChar().toString() }.toSet(), matrix)
    solve(m)
}

fun solve(matrix: UPGMAMatrix) {
    var m = matrix
    println("\nInput:")
    println(m)
    var idx = 1
    while (!m.solved()) {

        println("===== Iteration $idx =====\n")
        val newLink = m.newLinkPlusHeight()
        println("New link: ${newLink.first.first} - ${newLink.first.second}, with height: ${newLink.second / 2.0}\n")
        m = m.solveOneStep()
        println(m)
        idx++
    }
}
