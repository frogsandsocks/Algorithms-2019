@file:Suppress("UNUSED_PARAMETER", "unused")

package lesson5

import lesson5.Graph.Vertex
import java.lang.IllegalArgumentException
import java.lang.Math.max

/**
 * Эйлеров цикл.
 * Средняя
 *
 * Дан граф (получатель). Найти по нему любой Эйлеров цикл.
 * Если в графе нет Эйлеровых циклов, вернуть пустой список.
 * Соседние дуги в списке-результате должны быть инцидентны друг другу,
 * а первая дуга в списке инцидентна последней.
 * Длина списка, если он не пуст, должна быть равна количеству дуг в графе.
 * Веса дуг никак не учитываются.
 *
 * Пример:
 *
 *      G -- H
 *      |    |
 * A -- B -- C -- D
 * |    |    |    |
 * E    F -- I    |
 * |              |
 * J ------------ K
 *
 * Вариант ответа: A, E, J, K, D, C, H, G, B, C, I, F, B, A
 *
 * Справка: Эйлеров цикл -- это цикл, проходящий через все рёбра
 * связного графа ровно по одному разу
 */
fun Graph.findEulerLoop(): List<Graph.Edge> {
    TODO()
}

/**
 * Минимальное остовное дерево.
 * Средняя
 *
 * Дан граф (получатель). Найти по нему минимальное остовное дерево.
 * Если есть несколько минимальных остовных деревьев с одинаковым числом дуг,
 * вернуть любое из них. Веса дуг не учитывать.
 *
 * Пример:
 *
 *      G -- H
 *      |    |
 * A -- B -- C -- D
 * |    |    |    |
 * E    F -- I    |
 * |              |
 * J ------------ K
 *
 * Ответ:
 *
 *      G    H
 *      |    |
 * A -- B -- C -- D
 * |    |    |
 * E    F    I
 * |
 * J ------------ K
 */
fun Graph.minimumSpanningTree(): Graph {
    TODO()
}

/**
 * Максимальное независимое множество вершин в графе без циклов.
 * Сложная
 *
 * Дан граф без циклов (получатель), например
 *
 *      G -- H -- J
 *      |
 * A -- B -- D
 * |         |
 * C -- F    I
 * |
 * E
 *
 * Найти в нём самое большое независимое множество вершин и вернуть его.
 * Никакая пара вершин в независимом множестве не должна быть связана ребром.
 *
 * Если самых больших множеств несколько, приоритет имеет то из них,
 * в котором вершины расположены раньше во множестве this.vertices (начиная с первых).
 *
 * В данном случае ответ (A, E, F, D, G, J)
 *z
 * Если на входе граф с циклами, бросить IllegalArgumentException
 *
 * Эта задача может быть зачтена за пятый и шестой урок одновременно
 */


private class GraphExplorer(val graph: Graph) {

    val vertices: MutableSet<Vertex> = graph.vertices

    /* Множество посещённых вершин */
    val verticesVisited = mutableSetOf<Vertex>()

    /* Два множества для всех чётных и для всех нечётных вершин одного дерева */
    val verticesOdd = mutableSetOf<Vertex>()
    val verticesEven = mutableSetOf<Vertex>()

    /* Конечное множество независимых вершин */
    val verticesResult = mutableSetOf<Vertex>()


    fun largestIndependentVertexSet(): Set<Vertex> {

        vertices.forEach { vertex ->

            /* Если в общем множестве остались непосещённые вершины (если есть ещё одно дерево в переданном графе) */
            if (!verticesVisited.contains(vertex)) {

                /* Записываем первую вершину в дереве в множество нечётных элементов */
                verticesOdd += vertex

                /* Начинаем обход по дереву с этой вершины */
                independentVertexSetExploreVertex(vertex)

                /* Выбираем из двух множеств с наибольшим количеством вершин */
                verticesResult += if (verticesOdd.size >= verticesEven.size) verticesOdd else verticesEven

                verticesOdd.clear()
                verticesEven.clear()
            }
        }

        return verticesResult
    }


    private fun independentVertexSetExploreVertex(vertex: Vertex) {

        /* Получаем всех соседей текущей вершины */
        val vertexNeighbors = graph.getNeighbors(vertex)
        verticesVisited.add(vertex)

        /* Если предыдущая вершина была нечётной, то её соседей записываем в множество чётных и наоборот */
        when (vertex) {

            in verticesOdd -> verticesEven
            in verticesEven -> verticesOdd

            else -> throw IllegalArgumentException()

        }.addAll(vertexNeighbors)

        /* Рекурсивно проходим каждую вершину */
        vertexNeighbors.forEach { vertexNeighbor ->
            if (!verticesVisited.contains(vertexNeighbor)) independentVertexSetExploreVertex(vertexNeighbor)
        }
    }

    private fun graphCheckCycle() {}
}

fun Graph.largestIndependentVertexSet(): Set<Vertex> = GraphExplorer(this).largestIndependentVertexSet()

/**
 * Наидлиннейший простой путь.
 * Сложная
 *
 * Дан граф (получатель). Найти в нём простой путь, включающий максимальное количество рёбер.
 * Простым считается путь, вершины в котором не повторяются.
 * Если таких путей несколько, вернуть любой из них.
 *
 * Пример:
 *
 *      G -- H
 *      |    |
 * A -- B -- C -- D
 * |    |    |    |
 * E    F -- I    |
 * |              |
 * J ------------ K
 *
 * Ответ: A, E, J, K, D, C, H, G, B, F, I
 */
fun Graph.longestSimplePath(): Path {
    TODO()
}