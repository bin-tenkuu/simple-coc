package com.github.bin.util;

import lombok.val;

import java.time.Duration;
import java.util.HashMap;

/**
 * @author bin
 * @since 2023/08/22
 */
public class CacheMap<K, V> {
    /**
     * 过期时间,毫秒
     */
    private final long timeout;

    private final HashMap<K, Node<V>> map = new HashMap<>();

    public CacheMap(long timeout) {
        this.timeout = timeout;
    }

    public CacheMap() {
        this.timeout = Duration.ofMinutes(10).toMillis();
    }

    private long nextExpirationTime = Long.MAX_VALUE;

    private void expungeExpiredEntries() {
        val time = System.currentTimeMillis();
        if (nextExpirationTime > time) {
            return;
        }
        nextExpirationTime = Long.MAX_VALUE;
        val iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            val v = iterator.next().getValue();
            if (v.isBeOverdue(time)) {
                iterator.remove();
            } else if (nextExpirationTime > v.time) {
                nextExpirationTime = v.time;
            }
        }
    }

    public int getSize() {
        expungeExpiredEntries();
        return map.size();
    }

    public void clear() {
        map.clear();
        nextExpirationTime = Long.MAX_VALUE;
    }

    public void set(K key, V value, long timeout) {
        expungeExpiredEntries();
        map.put(key, new Node<>(value, timeout));
    }

    public V getAndExpire(K key, long timeout) {
        expungeExpiredEntries();
        var node = map.get(key);
        if (node == null) {
            return null;
        }
        map.put(key, new Node<>(node.v, timeout));
        return node.v;
    }

    public void set(K key, V value) {
        set(key, value, timeout);
    }

    public V get(K key) {
        val node = map.get(key);
        if (node == null) {
            return null;
        }
        if (node.isBeOverdue()) {
            map.remove(key);
            return null;
        }
        return node.v;
    }

    public boolean contains(K key) {
        val node = map.get(key);
        if (node == null) {
            return false;
        }
        if (node.isBeOverdue()) {
            map.remove(key);
            return false;
        }
        return true;
    }

    public V remove(K key) {
        val node = map.remove(key);
        if (node == null) {
            return null;
        }
        if (node.isBeOverdue()) {
            return null;
        }
        return node.v;
    }

    private record Node<V>(V v, long time) {
        private Node(V v, long time) {
            this.v = v;
            this.time = time + System.currentTimeMillis();
        }

        public boolean isBeOverdue(long time) {
            return time >= this.time;
        }

        public boolean isBeOverdue() {
            return System.currentTimeMillis() >= this.time;
        }

        @Override
        public String toString() {
            return String.format("%s:%s", isBeOverdue() ? "timeout" : "waiting", v);
        }
    }
}
