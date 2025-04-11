package org.example.cpuschedular;

public interface Scheduler {
    void schedule(int numProcesses, int[] burstTimes);

    int getRemainingBurst(int i);

    void addProcess(int burstTime);

    String getGanttChart();

    double getAverageWaitingTime();

    double getAverageTurnaroundTime();
}
