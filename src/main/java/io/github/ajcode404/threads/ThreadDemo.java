package io.github.ajcode404.threads;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

class TestThread implements Runnable {

    @Override
    public void run() {
        System.out.println("Hello from test thread");
    }
}

class ThreadDemo {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Thread t = new Thread(new TestThread());
        t.start();
        AtomicInteger i = new AtomicInteger();
        ExecutorService es = Executors.newFixedThreadPool(10);
        Future<String> f = es.submit(() -> "Hello " + i.getAndIncrement());
        String r = f.get();
        System.out.println(r);
    }
}