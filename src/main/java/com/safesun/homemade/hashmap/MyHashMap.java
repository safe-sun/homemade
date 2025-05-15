package com.safesun.homemade.hashmap;

public class MyHashMap<K, V> {
    Node<K, V>[] table = new Node[16];

    private int size;

    private double factor = 0.75;

    public V put(K key, V value) {
        int keyIndex = indexOf(key);
        var node = table[keyIndex];
        if (node == null) {
            table[keyIndex] = new Node<>(key, value);
            size++;
            resize();
            return null;
        }
        while (true) {
            if (node.key.equals(key)) {
                var old = node.value;
                node.value = value;
                return old;
            }
            if (node.next == null) {
                node.next = new Node<>(key, value);
                size++;
                resize();
                return null;
            }
            node = node.next;
        }
    }

    public V get(K key) {
        int keyIndex = indexOf(key);
        var node = table[keyIndex];
        while (node != null) {
            if (node.key.equals(key)) {
                return node.value;
            }
            node = node.next;
        }
        return null;
    }

    public V remove(K key) {
        int keyIndex = indexOf(key);
        var node = table[keyIndex];
        if (node == null) {
            return null;
        }
        if (node.key.equals(key)) {
            table[keyIndex] = node.next;
            size--;
            return node.value;
        }
        node = node.next;
        var pre = node;
        while (node != null) {
            if (node.key.equals(key)) {
                pre.next = node.next;
                size--;
                return node.value;
            }
            pre = node;
            node = node.next;
        }
        return null;
    }

    private void resize() {
        if (size < table.length * factor) {
            return;
        }
        int LengthNew = table.length << 1;
        Node<K, V>[] tableNew = new Node[LengthNew];
        for (var node : table) {
            if (node == null) {
                continue;
            }

            while (node != null) {
                var indexNew = node.key.hashCode() & (tableNew.length - 1);
                var nodeNew = tableNew[indexNew];
                if (nodeNew == null) {
                    // head insert
                    tableNew[indexNew] = node;
                    var next = node.next;
                    node.next = null;
                    node = next;
                    continue;
                }
                var next = node.next;
                node.next = tableNew[indexNew];
                tableNew[indexNew] = node;
                node = next;
            }
        }
        table = tableNew;
    }

    public int size() {
        return size;
    }

    private int indexOf(Object key) {
        return key.hashCode() & (table.length - 1);
    }

    static class Node<K, V> {
        K key;
        V value;
        Node<K, V> next;

        public Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

}
