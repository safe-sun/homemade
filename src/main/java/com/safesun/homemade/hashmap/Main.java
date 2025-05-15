package com.safesun.homemade.hashmap;

public class Main {
    public static void main(String[] args) {
        MyHashMap<Integer, String> map = new MyHashMap<>();

        int n = 100000;

        for (int i = 0;i < n;i++) {
            map.put(i, String.valueOf(1));
        }
        System.out.println("put all!");
    }

}
