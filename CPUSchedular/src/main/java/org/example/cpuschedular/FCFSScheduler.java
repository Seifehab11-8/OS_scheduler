package org.example.cpuschedular;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class FCFSScheduler implements Scheduler {
    private final Map<String, Integer> arrivalTimes = new HashMap<>();
    private final Map<String, Integer> completionTimes = new HashMap<>();
    private final Map<String, Integer> burstTimeMap = new HashMap<>();
    private final Map<String, Integer> waitingTimes = new HashMap<>();
    private final Map<String, Integer> turnaroundTimes = new HashMap<>();
    private final Queue<Integer> readyQueue = new LinkedList<>();
    private final Queue<String> processNames = new LinkedList<>();

    // Fields for tick-based simulation
    private int currentTime = 0;
    private int currentProcessBurstRemaining = 0;
    private String currentProcessName = null;
    private final StringBuilder ganttChart = new StringBuilder();

    // Average metrics (not recalculated in this simple version)
    private double avgWaitingTime;
    private double avgTurnaroundTime;
    private int processCounter = 0;

    /**
     * Combined schedule() method: every call simulates one tick (1s).
     * If no process is active, it fetches the next process from the ready queue.
     */
    @Override
    public synchronized void schedule(int numProcesses, Queue<Integer> burstTimes) {
        // If no process is currently running, load the next process from the ready queue.
        if (currentProcessBurstRemaining == 0) {
            if (!readyQueue.isEmpty()) {
                currentProcessBurstRemaining = readyQueue.poll();
                currentProcessName = processNames.poll();
            } else {
                // No process available, simply return.
                return;
            }
        }

        // Simulate one tick of execution.
        currentTime++;
        currentProcessBurstRemaining--;

        // Update Gantt chart to reflect that currentProcessName executed during this tick.
        ganttChart.append("| ").append(currentProcessName).append(" ");

        // (Optional) If the process finishes, you might compute turnaround/waiting times here.
        // For simplicity, these metrics are not updated tick-by-tick in this version.
        if (currentProcessBurstRemaining == 0) {
            completionTimes.put(currentProcessName, currentTime);

            // Calculate turnaround time = completion - arrival (arrival = 0 in FCFS)
            int turnaroundTime = completionTimes.get(currentProcessName) - arrivalTimes.get(currentProcessName);
            turnaroundTimes.put(currentProcessName, turnaroundTime);

            // Waiting time = turnaround - burst
            int burst = burstTimeMap.get(currentProcessName);
            int waitingTime = turnaroundTime - burst;
            waitingTimes.put(currentProcessName, waitingTime);

            // Update averages
            int totalWaiting = 0;
            int totalTurnaround = 0;
            int processCount = waitingTimes.size();

            for (String p : waitingTimes.keySet()) {
                totalWaiting += waitingTimes.get(p);
                totalTurnaround += turnaroundTimes.get(p);
            }

            avgWaitingTime = (double) totalWaiting / processCount;
            avgTurnaroundTime = (double) totalTurnaround / processCount;
            System.out.println("Process: " + currentProcessName +
                    " | Arrival: " + arrivalTimes.get(currentProcessName) +
                    " | Completion: " + currentTime +
                    " | Turnaround: " + turnaroundTime +
                    " | Waiting: " + waitingTime);
        }


    }

    /**
     * Adds a new process with the given burst time.
     */
    @Override
    public synchronized void addProcess(int burstTime, int arrivalTime) {
        processCounter++;
        readyQueue.add(burstTime);
        String processName ="P"+processCounter;
        processNames.add(processName);
        arrivalTimes.put(processName,arrivalTime);
        burstTimeMap.put(processName,burstTime);

    }

    /**
     * Returns the remaining burst time for a process.
     * In this simple tick-based model, we assume index 0 corresponds to the current process.
     */
    @Override
    public synchronized int getRemainingBurst(int i) {
        return (i == 0) ? currentProcessBurstRemaining : 0;
    }

    /**
     * Returns the current Gantt chart as a string.
     */
    @Override
    public synchronized String getGanttChart() {
        return ganttChart + "|";
    }

    /**
     * Returns the average waiting time.
     * (In this simplified model, these values are placeholders and not recalculated in real-time.)
     */
    @Override
    public synchronized double getAverageWaitingTime() {
        return avgWaitingTime;
    }

    /**
     * Returns the average turnaround time.
     * (In this simplified model, these values are placeholders and not recalculated in real-time.)
     */
    @Override
    public synchronized double getAverageTurnaroundTime() {
        return avgTurnaroundTime;
    }
    public synchronized String getCurrentProcessName() {
        return currentProcessName;
    }
}
