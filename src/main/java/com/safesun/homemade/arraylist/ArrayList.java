package com.safesun.homemade.arraylist;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;

public class ArrayList<E> implements List<E> {
    private Object[] elements = new Object[10];

    private int size;

    private void resize() {
        if (size == elements.length) {
            Object[] newElements = new Object[elements.length * 2];
            System.arraycopy(elements, 0, newElements, 0, size);
            elements = newElements;
        }
    }

    @Override
    public void add(E o) {
        resize();
        elements[size] = o;
        size++;
    }

    @Override
    public void add(int index, E o) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException();
        }
        resize();
        System.arraycopy(elements, index, elements, index + 1, size - index);
        elements[index] = o;
        size++;
    }

    @Override
    public E get(int index) {
        if (index < 0 || index > size - 1) {
            throw new IndexOutOfBoundsException();
        }
        return (E) elements[index];
    }

    @Override
    public E set(int index, E o) {
        if (index < 0 || index > size - 1) {
            throw new IndexOutOfBoundsException();
        }
        E old = (E) elements[index];
        elements[index] = o;
        return old;
    }

    @Override
    public E remove(int index) {
        if (index < 0 || index > size - 1) {
            throw new IndexOutOfBoundsException();
        }
        E old = (E) elements[index];
        System.arraycopy(elements, index + 1, elements, index, size - index - 1);
        size--;
        return old;
    }

    @Override
    public boolean remove(E o) {
        for (int i = 0;i < size;i++) {
            if (Objects.equals(elements[i], o)) {
                remove(i);
                return true;
            }
        }
        return false;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Iterator<E> iterator() {
        return new ArrayListIterator();
    }

    class ArrayListIterator implements Iterator<E> {
        int cursor;

        @Override
        public boolean hasNext() {
            return cursor < size;
        }

        @Override
        public E next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            E element = (E) elements[cursor];
            cursor++;
            return element;
        }
    }
}
