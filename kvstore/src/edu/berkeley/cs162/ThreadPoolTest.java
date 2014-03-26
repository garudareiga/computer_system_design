package edu.berkeley.cs162;

import static org.junit.Assert.*;

import org.junit.Test;

public class ThreadPoolTest {

    public class MyRunnable implements Runnable {
        private int index;
        
        public MyRunnable(int index) {
            this.index = index;
        }
        
        @Override
        public void run() {
            // TODO Auto-generated method stub
            System.out.println(String.format("Runnable-%d", this.index));
        }
        
    }
    
    @Test
    public void test() {
        ThreadPool threadPool = new ThreadPool(2);
        try {
            for (int i = 0; i < 10; i++) {
                threadPool.addToQueue(new MyRunnable(i));
                Thread.sleep(500);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}