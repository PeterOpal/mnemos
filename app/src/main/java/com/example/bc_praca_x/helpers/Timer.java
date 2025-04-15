package com.example.bc_praca_x.helpers;

public class Timer {
    private long startTime = 0;
    private long accumulatedTime = 0;
    private boolean running = false;

    public void start() {
        startTime = System.currentTimeMillis();
        accumulatedTime = 0;
        running = true;
    }

    public void pause() {
        if (running) {
            accumulatedTime += System.currentTimeMillis() - startTime;
            running = false;
        }
    }

    public void resume() {
        if (!running) {
            startTime = System.currentTimeMillis();
            running = true;
        }
    }

    public int getElapsedTime() {
        long elapsed = accumulatedTime;
        if (running) {
            elapsed += System.currentTimeMillis() - startTime;
        }
        return (int) (elapsed / 1000);
    }

    public int stop() {
        int totalElapsed = getElapsedTime();
        reset();
        return totalElapsed;
    }

    public void reset() {
        startTime = 0;
        accumulatedTime = 0;
        running = false;
    }
}
