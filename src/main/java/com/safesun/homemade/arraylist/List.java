package com.safesun.homemade.arraylist;

public interface List<E> extends Iterable<E> {
    void add(E o);

    void add(int index, E o);

    E get(int index);

    E set(int index, E o);

    E remove(int index);

    boolean remove(E o);

    int size();
}
