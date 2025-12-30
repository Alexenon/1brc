package com.xenon.utils;

public class Benchmark {

    private Benchmark() {
    }

    public static void execute(String taskName, Runnable runnable) {
        long start = System.currentTimeMillis();
        runnable.run();
        long end = System.currentTimeMillis();
        System.out.printf("%s took: %d ms\n", taskName, end - start);
    }

}
