package com.github.gunkins.lrucache

class LruCache<K, V>(private val capacity: Int) {

    init {
        if (capacity < 1) throw IllegalArgumentException("Capacity must be more than 1")
    }

    private val nodesByKey: MutableMap<K, Node<K, V>> = mutableMapOf()
    private val recentlyUsed : RecentlyUsedNodes<K, V> = RecentlyUsedNodes()
    private var size = 0

    fun get(key: K): V? {
        val node = nodesByKey[key] ?: return null
        recentlyUsed.markAsLastUsed(node)
        return node.value
    }

    fun put(key: K, value: V) {
        val maybeNode = nodesByKey[key]
        if (maybeNode != null) {
            recentlyUsed.markAsLastUsed(maybeNode)
            maybeNode.value = value
        } else {
            if (size == capacity) {
                val lastNode = recentlyUsed.removeLeastRecentlyUsed()
                nodesByKey.remove(lastNode.key)
                size--
            }
            val newNode = recentlyUsed.add(key, value)
            nodesByKey[key] = newNode
            size++
        }
        assert(size <= capacity)
    }
}

private class RecentlyUsedNodes<K, V> {
    private var head: Node<K, V>? = null
    private var tail: Node<K, V>? = null

    fun add(key: K, value: V) : Node<K, V> {
        val node = Node(key, value)
        if (tail == null) {
            head = node
            tail = node
        } else {
            assert(head != null)
            setInFrontOfNotNullHead(node)
        }
        return node
    }

    fun removeLeastRecentlyUsed(): Node<K, V> {
        assert(head != null && tail != null)

        val lastNode = tail!!

        if (head === tail) {
            head = null
            tail = null
        } else {
            assert(tail?.prev != null)

            val preLast = tail!!.prev!!
            tail = preLast
            preLast.next = null
        }

        return lastNode
    }

    fun markAsLastUsed(node: Node<K, V>) {
        assert(head != null && tail != null)

        if (head === node) return

        assert(node.prev != null)

        val prev = node.prev!!
        prev.next = node.next

        if (node === tail) {
            tail = prev
        } else {
            node.next!!.prev = prev
        }

        setInFrontOfNotNullHead(node)
    }

    private fun setInFrontOfNotNullHead(node: Node<K, V>) {
        node.next = head
        head!!.prev = node
        head = node
    }
}

private data class Node<K, V>(
    val key: K,
    var value: V,
    var next: Node<K, V>? = null,
    var prev: Node<K, V>? = null
) {
    override fun toString() = value.toString()
}