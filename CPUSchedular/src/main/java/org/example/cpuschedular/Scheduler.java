package org.example.cpuschedular;

import java.util.Queue;

public interface Scheduler {
    void schedule(int numProcesses, Queue<Integer> burstTimes);

    int getRemainingBurst(int i);

    void addProcess(int burstTime , int arrivalTime);

    String getGanttChart();

    double getAverageWaitingTime();

    double getAverageTurnaroundTime();
    String getCurrentProcessName();
}
