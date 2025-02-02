@file:Suppress("UNUSED_PARAMETER")

package lesson6

import java.io.File

import kotlin.Int.Companion.MAX_VALUE

/**
 * Наибольшая общая подпоследовательность.
 * Средняя
 *
 * Дано две строки, например "nematode knowledge" и "empty bottle".
 * Найти их самую длинную общую подпоследовательность -- в примере это "emt ole".
 * Подпоследовательность отличается от подстроки тем, что её символы не обязаны идти подряд
 * (но по-прежнему должны быть расположены в исходной строке в том же порядке).
 * Если общей подпоследовательности нет, вернуть пустую строку.
 * Если есть несколько самых длинных общих подпоследовательностей, вернуть любую из них.
 * При сравнении подстрок, регистр символов *имеет* значение.
 */
fun longestCommonSubSequence(first: String, second: String): String {
    TODO()
}

/**
 * Наибольшая возрастающая подпоследовательность
 * Сложная
 *
 * Дан список целых чисел, например, [2 8 5 9 12 6].
 * Найти в нём самую длинную возрастающую подпоследовательность.
 * Элементы подпоследовательности не обязаны идти подряд,
 * но должны быть расположены в исходном списке в том же порядке.
 * Если самых длинных возрастающих подпоследовательностей несколько (как в примере),
 * то вернуть ту, в которой числа расположены раньше (приоритет имеют первые числа).
 * В примере ответами являются 2, 8, 9, 12 или 2, 5, 9, 12 -- выбираем первую из них.
 */
fun longestIncreasingSubSequence(list: List<Int>): List<Int> {
    TODO()
}

/**
 * Самый короткий маршрут на прямоугольном поле.
 * Средняя
 *
 * В файле с именем inputName задано прямоугольное поле:
 *
 * 0 2 3 2 4 1
 * 1 5 3 4 6 2
 * 2 6 2 5 1 3
 * 1 4 3 2 6 2
 * 4 2 3 1 5 0
 *
 * Можно совершать шаги длиной в одну клетку вправо, вниз или по диагонали вправо-вниз.
 * В каждой клетке записано некоторое натуральное число или нуль.
 * Необходимо попасть из верхней левой клетки в правую нижнюю.
 * Вес маршрута вычисляется как сумма чисел со всех посещенных клеток.
 * Необходимо найти маршрут с минимальным весом и вернуть этот минимальный вес.
 *
 * Здесь ответ 2 + 3 + 4 + 1 + 2 = 12
 */

private data class Node(val fieldNodeValue: Int) {

    var pathValue = MAX_VALUE

    var closed = false
}

private data class Coordinates(val x: Int, val y: Int)

fun shortestPathOnField(inputName: String): Int {

    val inputField = mutableListOf<String>()

    for (line in File(inputName).readLines()) {

        inputField += line
    }


    val field = mutableMapOf<Coordinates, Node>()

    var result = MAX_VALUE

    val fieldLength = inputField.size
    val fieldWidth = inputField.first().split(" ").size

    inputField.forEachIndexed { lineIndex, line ->

        line.split(" ").forEachIndexed { valueIndex, value ->

            field[Coordinates(valueIndex + 1, lineIndex + 1)] = Node(value.toInt())
        }
    }


    fun shortestPathNodeExplore(fieldNodeCoordinates: Coordinates, fieldNode: Node) {

        val fieldNodeChildLeftCoordinates = Coordinates(fieldNodeCoordinates.x - 1, fieldNodeCoordinates.y)
        val fieldNodeChildAboveCoordinates = Coordinates(fieldNodeCoordinates.x, fieldNodeCoordinates.y - 1)
        val fieldNodeChildDiagonalCoordinates = Coordinates(fieldNodeCoordinates.x - 1, fieldNodeCoordinates.y - 1)

        val fieldNodeChildLeft = field[fieldNodeChildLeftCoordinates]
        val fieldNodeChildAbove = field[fieldNodeChildAboveCoordinates]
        val fieldNodeChildDiagonal = field[fieldNodeChildDiagonalCoordinates]


        if (fieldNodeChildLeft != null) {

            val fieldNodeLeftStep = fieldNodeChildLeft.fieldNodeValue + fieldNode.pathValue

            if (fieldNodeLeftStep < fieldNodeChildLeft.pathValue) {

                fieldNodeChildLeft.pathValue = fieldNodeLeftStep
            }

            shortestPathNodeExplore(fieldNodeChildLeftCoordinates, fieldNodeChildLeft)
        }


        if (fieldNodeChildAbove != null) {

            val fieldNodeUpStep = fieldNodeChildAbove.fieldNodeValue + fieldNode.pathValue

            if (fieldNodeUpStep < fieldNodeChildAbove.pathValue) {

                fieldNodeChildAbove.pathValue = fieldNodeUpStep
            }

            shortestPathNodeExplore(fieldNodeChildAboveCoordinates, fieldNodeChildAbove)
        }

        if (fieldNodeChildDiagonal != null) {

            val fieldNodeDiagonalStep = fieldNodeChildDiagonal.fieldNodeValue + fieldNode.pathValue

            if (fieldNodeDiagonalStep < fieldNodeChildDiagonal.pathValue) {

                fieldNodeChildDiagonal.pathValue = fieldNodeDiagonalStep
            }

            shortestPathNodeExplore(fieldNodeChildLeftCoordinates, fieldNodeChildDiagonal)
        }

        if (fieldNodeChildLeft == null && fieldNodeChildAbove == null) {

            if (result > fieldNode.pathValue) result = fieldNode.pathValue
        }
    }


    val firstNodeCoordinates = Coordinates(fieldWidth, fieldLength)
    val firstNode = field[firstNodeCoordinates] ?: return 0
    firstNode.pathValue = firstNode.fieldNodeValue

    shortestPathNodeExplore(firstNodeCoordinates, firstNode)
    return result
}

// Задачу "Максимальное независимое множество вершин в графе без циклов"
// смотрите в уроке 5