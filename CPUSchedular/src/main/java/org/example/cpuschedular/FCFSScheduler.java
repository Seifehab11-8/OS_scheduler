package org.example.cpuschedular;

import java.util.*;

public class FCFSScheduler implements Scheduler {
    private final Map<String, Integer> arrivalTimes = new HashMap<>();
    private final Map<String, Integer> burstTimeMap = new HashMap<>();
    private final Map<String, Integer> completionTimes = new HashMap<>();
    private final Map<String, Integer> turnaroundTimes = new HashMap<>();
    private final Map<String, Integer> waitingTimes = new HashMap<>();
    private final Queue<String> readyQueue = new LinkedList<>();
    private final Map<String, Integer> pendingArrivalTimes = new HashMap<>();

    private int currentTime = 0;
    private String currentProcessName = null;
    private int currentProcessBurstRemaining = 0;

    private final StringBuilder ganttChart = new StringBuilder();
    private double avgWaitingTime = 0;
    private double avgTurnaroundTime = 0;

    @Override
    public synchronized void addProcess(int burstTime, int arrivalTime) {
        String processName = "P" + (arrivalTimes.size() + 1);
        arrivalTimes.put(processName, arrivalTime);
        burstTimeMap.put(processName, burstTime);
        pendingArrivalTimes.put(processName, arrivalTime);
    }

    @Override
    public synchronized void schedule(int numProcesses, Queue<Integer> unused) {
        // Step 1: Move arrived processes to the ready queue
        List<String> justArrived = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : pendingArrivalTimes.entrySet()) {
            if (entry.getValue() <= currentTime) {
                readyQueue.add(entry.getKey());
                justArrived.add(entry.getKey());
            }
        }
        for (String name : justArrived) {
            pendingArrivalTimes.remove(name);
        }

        //  Step 2: Run current process
        if (currentProcessName != null) {
            currentProcessBurstRemaining--;

            if (currentProcessBurstRemaining == 0) {
                int completionTime = currentTime + 1;
                completionTimes.put(currentProcessName, completionTime);

                int arrival = arrivalTimes.get(currentProcessName);
                int burst = burstTimeMap.get(currentProcessName);
                int turnaround = completionTime - arrival;
                int waiting = turnaround - burst;

                turnaroundTimes.put(currentProcessName, turnaround);
                waitingTimes.put(currentProcessName, waiting);

                int count = waitingTimes.size();
                int totalWaiting = 0, totalTurnaround = 0;
                for (String p : waitingTimes.keySet()) {
                    totalWaiting += waitingTimes.get(p);
                    totalTurnaround += turnaroundTimes.get(p);
                }

                avgWaitingTime = (double) totalWaiting / count;
                avgTurnaroundTime = (double) totalTurnaround / count;

                currentProcessName = null; // 🔥 don’t start next process in same tick
            }
        }

        //  Step 3: If no process running, pick next from ready queue
        if (currentProcessName == null && !readyQueue.isEmpty()) {
            currentProcessName = readyQueue.poll();
            currentProcessBurstRemaining = burstTimeMap.get(currentProcessName);
        }

        //  Step 4: Log to Gantt chart
        if (currentProcessName == null) {
            ganttChart.append("| idle ");
        } else {
            ganttChart.append("| ").append(currentProcessName).append(" ");
        }

        currentTime++;
    }

    @Override
    public synchronized String getCurrentProcessName() {
        return currentProcessName;
    }

    @Override
    public synchronized int getRemainingBurst(int unused) {
        return currentProcessBurstRemaining-1;
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
