package org.example.cpuschedular;

public class SJFPreemptiveScheduler implements Scheduler {
    @Override
    public void schedule(int numProcesses, int[] burstTimes) {

    }

    @Override
    public int getRemainingBurst(int i) {
        return 0;
    }

    @Override
    public void addProcess(int burstTime) {

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
