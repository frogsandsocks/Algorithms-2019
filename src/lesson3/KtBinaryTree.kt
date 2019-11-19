package lesson3

import java.lang.IllegalArgumentException
import java.util.*
import kotlin.NoSuchElementException
import kotlin.math.max

// Attention: comparable supported but comparator is not
class KtBinaryTree<T : Comparable<T>> : AbstractMutableSet<T>(), CheckableSortedSet<T> {

    private var root: Node<T>? = null

    override var size = 0
        private set

    private class Node<T>(val value: T) {

        var left: Node<T>? = null

        var right: Node<T>? = null
    }

    override fun add(element: T): Boolean {
        val closest = find(element)
        val comparison = if (closest == null) -1 else element.compareTo(closest.value)
        if (comparison == 0) {
            return false
        }
        val newNode = Node(element)
        when {
            closest == null -> root = newNode
            comparison < 0 -> {
                assert(closest.left == null)
                closest.left = newNode
            }
            else -> {
                assert(closest.right == null)
                closest.right = newNode
            }
        }
        size++
        return true
    }

    override fun checkInvariant(): Boolean =
        root?.let { checkInvariant(it) } ?: true

    override fun height(): Int = height(root)

    private fun checkInvariant(node: Node<T>): Boolean {
        val left = node.left
        if (left != null && (left.value >= node.value || !checkInvariant(left))) return false
        val right = node.right
        return right == null || right.value > node.value && checkInvariant(right)
    }

    private fun height(node: Node<T>?): Int {
        if (node == null) return 0
        return 1 + max(height(node.left), height(node.right))
    }

    /**
     * Удаление элемента в дереве
     * Средняя
     *
     * Сложность: Худший случай — O(n), где n — это количество элементов. Сложность линейная, так как
     * дерево несбалансировано.
     *
     * Ресурсоёмкость: O(n)
     */

    /* Переменная для отслеживания успешности удаления ключа */
    private var removeNodeSuccessful = true

    override fun remove(element: T): Boolean {

        /* Сбрасываем значение */
        removeNodeSuccessful = true

        root = removeNode(element, root)
        if (removeNodeSuccessful) size--

        return removeNodeSuccessful
    }


    private fun removeNode(element: T, givenNode: Node<T>?): Node<T>? {

        /* Копируем параметр в изменяемый тип */
        var removeNodeCurrent = givenNode

        /*
         * Если изначально дерево не имеет элементов
         * или нужный элемент был не найден и функция вышла за границы дерева после листа
         */
        if (removeNodeCurrent == null) {

            removeNodeSuccessful = false
            return null
        }

        when {

            /* Проходим дерево до нужного элемента */
            element < removeNodeCurrent.value -> removeNodeCurrent.left = removeNode(element, removeNodeCurrent.left)
            element > removeNodeCurrent.value -> removeNodeCurrent.right = removeNode(element, removeNodeCurrent.right)

            /* Если нужный элемент был найден */
            else -> when {

                /* Если текущий элемент лист или имеет одного потомка */
                removeNodeCurrent.left == null -> return removeNodeCurrent.right
                removeNodeCurrent.right == null -> return removeNodeCurrent.left

                /* Если потомка два */
                else -> {

                    /* Находим элемент с наименьшим ключом */
                    var removeNodeSearchIterator = removeNodeCurrent.right

                    while (removeNodeSearchIterator?.left != null)
                        removeNodeSearchIterator = removeNodeSearchIterator.left

                    /* Вставляем этот элемент вместо текущего и удаляем */
                    removeNodeSearchIterator?.right =
                        removeNode(removeNodeSearchIterator!!.value, removeNodeCurrent.right)
                    removeNodeSearchIterator.left = removeNodeCurrent.left

                    removeNodeCurrent = removeNodeSearchIterator
                }
            }
        }

        /* Возвращаем тот же элемент, что и получили, если он не равен ключу и нас не интересует */
        return removeNodeCurrent
    }

    override operator fun contains(element: T): Boolean {
        val closest = find(element)
        return closest != null && element.compareTo(closest.value) == 0
    }

    private fun find(value: T): Node<T>? =
        root?.let { find(it, value) }

    private fun find(start: Node<T>, value: T): Node<T> {
        val comparison = value.compareTo(start.value)
        return when {
            comparison == 0 -> start
            comparison < 0 -> start.left?.let { find(it, value) } ?: start
            else -> start.right?.let { find(it, value) } ?: start
        }
    }

    inner class BinaryTreeIterator internal constructor() : MutableIterator<T> {

        private var binaryTreeIteratorStack = Stack<Node<T>>()

        init {
            downToLeftNode(root)
        }


        /**
         * Проверка наличия следующего элемента
         * Средняя
         *
         * Предполагаю, что сложность здесь будет O(1), также и с ресурсоёмкостью
         */
        override fun hasNext(): Boolean = !binaryTreeIteratorStack.empty()


        private fun downToLeftNode(givenNode: Node<T>?) {

            /* Копируем параметр в изменяемый тип */
            var downToLeftNodeIterator = givenNode

            /* Поочерёдно кладём в стек все самые левые элементы */
            while (downToLeftNodeIterator != null) {

                binaryTreeIteratorStack.push(downToLeftNodeIterator)
                downToLeftNodeIterator = downToLeftNodeIterator.left
            }
        }

        /**
         * Поиск следующего элемента
         * Средняя
         *
         * Сложность: Худший случай — O(n), где n — это количество элементов. Сложность линейная, так как
         * дерево несбалансировано.
         *
         * Ресурсоёмкость: O(n)
         */
        override fun next(): T {

            if (!hasNext()) throw NoSuchElementException("Iteration has no more elements")

            val nextNode = binaryTreeIteratorStack.pop()
            downToLeftNode(nextNode.right)

            return nextNode.value
        }

        /**
         * Удаление следующего элемента
         * Сложная
         */
        override fun remove() {
            // TODO
            throw NotImplementedError()
        }
    }

    override fun iterator(): MutableIterator<T> = BinaryTreeIterator()

    override fun comparator(): Comparator<in T>? = null


    inner class SubKtBinaryTree(private val ktBinaryTree: KtBinaryTree<T>, val fromElement: T?, val toElement: T?) :
        AbstractMutableSet<T>(), SortedSet<T> {


        init {
            require(!(fromElement == null && toElement == null))
        }

        /*
         * Сложность: O(n)
         * Ресурсоёмкость: O(n)
         */
        override val size: Int
            get() = when {
                fromElement == null -> ktBinaryTree.count { it < toElement!! }
                toElement == null -> ktBinaryTree.count { it >= fromElement }
                else -> ktBinaryTree.count { it >= fromElement && it < toElement }
            }


        /*
         * Сложность: O(n)
         * Ресурсоёмкость: O(n)
         */
        override fun add(element: T): Boolean {

            val state = when {

                fromElement == null -> element < toElement!!
                toElement == null -> element >= fromElement
                else -> (element >= fromElement && element < toElement)
            }

            return if (state) {

                ktBinaryTree.add(element)
                true

            } else throw IllegalArgumentException()
        }


        /*
         * Сложность: O(n)
         * Ресурсоёмкость: O(n)
         */
        override fun remove(element: T): Boolean {

            val state = when {

                fromElement == null -> element < toElement!!
                toElement == null -> element >= fromElement
                else -> (element >= fromElement && element < toElement)
            }

            return if (state) {

                return ktBinaryTree.remove(element)

            } else throw IllegalArgumentException()
        }

        override fun iterator(): MutableIterator<T> = object : MutableIterator<T> {
            private val delegate = this@SubKtBinaryTree.ktBinaryTree.iterator()

            private var next: T? = null

            init {
                if (fromElement != null) {
                    while (delegate.hasNext()) {
                        val next = delegate.next()
                        if (next >= fromElement) {
                            this.next = next
                            break
                        }
                    }

                } else {

                    this.next = delegate.next()
                }
            }

            /*
             * Сложность: O(1)
             * Ресурсоёмкость: O(1)
             */
            override fun hasNext(): Boolean {
                return next != null
            }

            /*
             * Сложность: O(n)
             * Ресурсоёмкость: O(2n) -> O(n)
             */
            override fun next(): T {

                var lastNext: T

                if (hasNext()) lastNext = next!!
                else throw NoSuchElementException("Iteration has no more elements")

                if (delegate.hasNext()) {

                    next = delegate.next()

                    if ((toElement != null) && (next!! >= toElement)) next = null

                } else next = null

                return lastNext
            }

            override fun remove() {
                delegate.remove()
            }

        }

        /*
         * Сложность: O(1)
         * Ресурсоёмкость: O(1)
         */
        override fun tailSet(fromElement: T): SortedSet<T> {

            return if (this.fromElement != null) {

                if (fromElement >= this.fromElement) {
                    SubKtBinaryTree(this@KtBinaryTree, fromElement, null)

                } else throw IllegalArgumentException()

            } else SubKtBinaryTree(this@KtBinaryTree, fromElement, null)
        }

        /*
         * Сложность: O(1)
         * Ресурсоёмкость: O(1)
         */
        override fun headSet(toElement: T): SortedSet<T> {

            return if (this.toElement != null) {

                if (toElement >= this.toElement) {
                    SubKtBinaryTree(this@KtBinaryTree, toElement, null)

                } else throw IllegalArgumentException()

            } else SubKtBinaryTree(this@KtBinaryTree, toElement, null)
        }

        /*
         * Сложность: O(1)
         * Ресурсоёмкость: O(1)
         */
        override fun subSet(fromElement: T, toElement: T): SortedSet<T> {

            var checkBoundsHead = true
            var checkBoundsTail = true

            if ((this.toElement != null) && toElement >= this.toElement) checkBoundsHead = false
            if ((this.fromElement != null) && fromElement < this.fromElement) checkBoundsTail = false

            if (checkBoundsHead && checkBoundsTail)
                return SubKtBinaryTree(this@KtBinaryTree, toElement, fromElement)
            else throw IllegalArgumentException()
        }


        override fun comparator(): Comparator<in T>? = ktBinaryTree.comparator()


        private var elementFirst: Node<T>? = null
        private var lastElement: Node<T>? = null

        /*
         * Сложность: O(n)
         * Ресурсоёмкость: O(n)
         */
        private fun ceiling(element: T, ceilingNodeIterator: Node<T>? = root): Node<T>? {

            if (ceilingNodeIterator == null) {

                elementFirst = lastElement
                return null
            }

            if (ceilingNodeIterator.value < element) ceiling(element, ceilingNodeIterator.right)
            else {
                lastElement = ceilingNodeIterator
                ceiling(element, ceilingNodeIterator.left)
            }

            return elementFirst
        }

        /*
         * Сложность: O(n)
         * Ресурсоёмкость: O(n)
         */
        override fun first(): T {

            return if (fromElement != null) ceiling(fromElement)?.value ?: throw NoSuchElementException()
            else ktBinaryTree.first()
        }


        private var elementLast: Node<T>? = null
        private var lastIteratedElement: Node<T>? = null

        /*
         * Сложность: O(n)
         * Ресурсоёмкость: O(n)
         */
        private fun lower(element: T, lowerNode: Node<T>? = root): Node<T>? {

            if (lowerNode == null) {

                elementLast = lastIteratedElement
                return null
            }

            if (lowerNode.value >= element) lower(element, lowerNode.left)
            else {

                lastIteratedElement = lowerNode
                lower(element, lowerNode.right)
            }

            return elementLast
        }

        /*
         * Сложность: O(n)
         * Ресурсоёмкость: O(n)
         */
        override fun last(): T {

            return if (toElement != null) lower(toElement)?.value ?: throw NoSuchElementException()
            else ktBinaryTree.last()
        }
    }


    /**
     * Найти множество всех элементов в диапазоне [fromElement, toElement)
     * Очень сложная
     */
    override fun subSet(fromElement: T, toElement: T): SortedSet<T> =
        SubKtBinaryTree(this, fromElement, toElement)

    /**
     * Найти множество всех элементов меньше заданного
     * Сложная
     */
    override fun headSet(toElement: T): SortedSet<T> =
        SubKtBinaryTree(this, null, toElement)

    /**
     * Найти множество всех элементов больше или равных заданного
     * Сложная
     */
    override fun tailSet(fromElement: T): SortedSet<T> =
        SubKtBinaryTree(this, fromElement, null)

    override fun first(): T {
        var current: Node<T> = root ?: throw NoSuchElementException()
        while (current.left != null) {
            current = current.left!!
        }
        return current.value
    }

    override fun last(): T {
        var current: Node<T> = root ?: throw NoSuchElementException()
        while (current.right != null) {
            current = current.right!!
        }
        return current.value
    }
}
