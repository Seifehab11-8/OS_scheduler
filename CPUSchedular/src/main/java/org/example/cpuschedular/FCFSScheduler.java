package org.example.cpuschedular;

import java.util.*;

public class FCFSScheduler implements Scheduler {
    private final Map<String, Integer> arrivalTimes = new LinkedHashMap<>();
    private final Map<String, Integer> burstTimeMap = new HashMap<>();
    private final Map<String, Integer> completionTimes = new HashMap<>();
    private final Map<String, Integer> turnaroundTimes = new HashMap<>();
    private final Map<String, Integer> waitingTimes = new HashMap<>();
    private final Queue<String> readyQueue = new LinkedList<>();
    private final Map<String, Integer> pendingArrivalTimes = new HashMap<>();

    private int currentTime = 0;
    private String currentProcess = null;
    private int remainingBurst = 0;
    private boolean justCompleted = false;  // retain finished process for one tick

    private final StringBuilder ganttChart = new StringBuilder();
    private double avgWaitingTime = 0;
    private double avgTurnaroundTime = 0;

    @Override
    public synchronized void addProcess(int burstTime, int arrivalTime) {
        String name = "P" + (arrivalTimes.size() + 1);
        arrivalTimes.put(name, arrivalTime);
        burstTimeMap.put(name, burstTime);
        pendingArrivalTimes.put(name, arrivalTime);
    }

    @Override
    public synchronized void schedule(int numProcesses, Queue<Integer> unused) {
        // 1. Move arrivals into ready queue
        Iterator<Map.Entry<String, Integer>> it = pendingArrivalTimes.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Integer> entry = it.next();
            if (entry.getValue() <= currentTime) {
                readyQueue.add(entry.getKey());
                it.remove();
            }
        }

        // 2. If a process just finished last tick, clear it now so next pick can run
        if (justCompleted) {
            currentProcess = null;
            justCompleted = false;
        }

        // 3. If CPU is free, pick next FCFS process
        if (currentProcess == null && !readyQueue.isEmpty()) {
            currentProcess = readyQueue.poll();
            remainingBurst = burstTimeMap.get(currentProcess);
        }

        // 4. Log to Gantt and actually run one unit
        if (currentProcess == null) {
            ganttChart.append("| idle ");
        } else {
            ganttChart.append("| ").append(currentProcess).append(" ");
            remainingBurst--;
            if (remainingBurst == 0) {
                // Record completion
                int completionTime = currentTime + 1;
                completionTimes.put(currentProcess, completionTime);

                int arrival = arrivalTimes.get(currentProcess);
                int burst = burstTimeMap.get(currentProcess);
                int turnaround = completionTime - arrival;
                int waiting = turnaround - burst;
                turnaroundTimes.put(currentProcess, turnaround);
                waitingTimes.put(currentProcess, waiting);

                // Recompute averages
                avgWaitingTime = waitingTimes.values().stream().mapToInt(Integer::intValue).average().orElse(0);
                avgTurnaroundTime = turnaroundTimes.values().stream().mapToInt(Integer::intValue).average().orElse(0);

                // Keep this process alive for one more tick so UI sees remainingBurst == 0
                justCompleted = true;
            }
        }

        // 5. Advance the clock
        currentTime++;
    }

    @Override
    public synchronized String getCurrentProcessName() {
        return currentProcess;
    }

    @Override
    public synchronized int getRemainingBurst(int unused) {
        return remainingBurst;
    }

    @Override
    public synchronized String getGanttChart() {
        return ganttChart.toString();
    }

    @Override
    public synchronized double getAverageWaitingTime() {
        return avgWaitingTime;
    }

    @Override
    public synchronized double getAverageTurnaroundTime() {
        return avgTurnaroundTime;
    }
}
