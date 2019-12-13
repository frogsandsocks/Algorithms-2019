@file:Suppress("UNUSED_PARAMETER", "unused")

package lesson5

import lesson5.Graph.Vertex
import lesson5.impl.GraphBuilder
import java.lang.IllegalArgumentException

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
 *
 * Сложность: O(n^2)
 *
 * Ресурсоёмкость: O(n)
 *
 */
fun Graph.minimumSpanningTree(): Graph {

    val minimumSpanningTree = GraphBuilder()

    /* Сложность: O(n^2) */
    val minimumSpanningTreePath = shortestPath(vertices.first())

    val minimumSpanningTreeVertices = mutableMapOf<String, Vertex>()

    /* Сложность: O(n) */
    vertices.forEach { minimumSpanningTreeVertices[it.name] = minimumSpanningTree.addVertex(it.name) }

    /* Сложность: O(n) */
    minimumSpanningTreePath.forEach { (_, vertexInfo) ->

        if (vertexInfo.prev?.name != null) {

            minimumSpanningTree.addConnection(
                minimumSpanningTreeVertices[vertexInfo.prev.name]!!,
                minimumSpanningTreeVertices[vertexInfo.vertex.name]!!
            )
        }
    }

    return minimumSpanningTree.build()
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
 *
 *
 * Сложность: O(n^2)
 * Ресурсоёмкость: O(n^2)
 */

private class GraphExplorer(val graph: Graph) {

    val vertices: MutableSet<Vertex> = graph.vertices

    /* Множество посещённых вершин */
    val verticesCalculated = mutableMapOf<Vertex, Set<Vertex>>()
    val verticesVisited = mutableSetOf<Vertex>()


    fun largestIndependentVertexSet(): Set<Vertex> {

        val result = mutableSetOf<Vertex>()

        vertices.forEach { vertex ->

            if (!verticesVisited.contains(vertex)) result.addAll(
                independentVertexSetExploreVertex(
                    vertex,
                    null
                )
            )
        }

        return result
    }


    private fun independentVertexSetExploreVertex(vertex: Vertex, previousChild: Vertex?): Set<Vertex> {

        verticesVisited.add(vertex)

        /* Если максимальное независимое множество вершин посчитано от этой вершины -> вернуть значение */
        if (verticesCalculated.containsKey(vertex)) return verticesCalculated[vertex]
            ?: throw IllegalArgumentException()

        /* Самое большое множество, которое можно получить, если не брать текущую вершину */
        val vertexFromChildrenSum = mutableSetOf<Vertex>()

        /* Самое большое множество, которое можно получить, если взять текущую вершину */
        val vertexFromGrandchildrenSum = mutableSetOf<Vertex>()

        val vertexChildren = graph.getNeighbors(vertex)
        vertexChildren.remove(previousChild)


        vertexChildren.forEach { vertexChild ->

            vertexFromChildrenSum.addAll(independentVertexSetExploreVertex(vertexChild, vertex))
        }


        vertexChildren.forEach { vertexChild ->

            val vertexGrandchildren = graph.getNeighbors(vertexChild)

            vertexGrandchildren.remove(vertex)

            vertexGrandchildren.forEach { vertexGrandchild ->

                vertexFromGrandchildrenSum.addAll(
                    independentVertexSetExploreVertex(vertexGrandchild, vertex)
                )
            }
        }

        vertexFromGrandchildrenSum.add(vertex)

        val result =
            if (vertexFromGrandchildrenSum.size >= vertexFromChildrenSum.size) vertexFromGrandchildrenSum
            else vertexFromChildrenSum

        verticesCalculated[vertex] = result

        return result
    }
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