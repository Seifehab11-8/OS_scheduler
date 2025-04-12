package org.example.cpuschedular;

import java.util.Queue;

public class SJFNonPreemptiveScheduler implements Scheduler {
    @Override
    public void schedule(int numProcesses, Queue<Integer> burstTimes) {

    }

    @Override
    public int getRemainingBurst(int i) {
        return 0;
    }

    @Override
    public void addProcess(int burstTime,int arrivalTime) {

    }

    @Override
    public String getGanttChart() {
        return null;
    }

    @Override
    public double getAverageWaitingTime() {
        return 0;
    }

    @Override
    public double getAverageTurnaroundTime() {
        return 0;
    }
}
