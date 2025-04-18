package org.example.cpuschedular;

import java.util.*;

public class PriorityNonPreemptiveScheduler implements Scheduler {
    private final Map<String, Integer> arrivalTimes = new HashMap<>();
    private final Map<String, Integer> completionTimes = new HashMap<>();
    private final Map<String, Integer> burstTimeMap = new HashMap<>();
    private final Map<String, Integer> priorityMap = new HashMap<>();
    private final Map<String, Integer> waitingTimes = new HashMap<>();
    private final Map<String, Integer> turnaroundTimes = new HashMap<>();
    private final List<String> notArrivedYet = new ArrayList<>();


    // Priority queue sorted by priority (lower number = higher priority)
    private final PriorityQueue<String> readyQueue = new PriorityQueue<>(
            Comparator.comparingInt(priorityMap::get)
    );

    // Current process tracking
    private String currentProcess = null;
    private int currentTime = 0;
    private int currentProcessEndTime = 0;
    private final StringBuilder ganttChart = new StringBuilder();

    // Average metrics
    private double avgWaitingTime = 0;
    private double avgTurnaroundTime = 0;
    private int processCounter = 0;

    @Override
    public synchronized void schedule(int numProcesses, Queue<Integer> burstTimes) {
        // First, move any arriving processes to the ready queue
        Iterator<String> iterator = notArrivedYet.iterator();
        while (iterator.hasNext()) {
            String proc = iterator.next();
            if (arrivalTimes.get(proc) <= currentTime) {
                readyQueue.add(proc);
                iterator.remove();
            }
        }

        // Main scheduling logic
        if (currentProcess == null || currentTime > currentProcessEndTime) {
            if (!readyQueue.isEmpty()) {
                currentProcess = readyQueue.poll();
                int burstTime = burstTimeMap.get(currentProcess);
                currentProcessEndTime = currentTime + burstTime - 1;

                // Fill gantt chart
                ganttChart.append("| ");
                for (int i = 0; i < burstTime; i++) {
                    ganttChart.append(currentProcess).append(" ");
                }

                // Complete metrics
                completionTimes.put(currentProcess, currentProcessEndTime + 1);
                int turnaround = completionTimes.get(currentProcess) - arrivalTimes.get(currentProcess);
                turnaroundTimes.put(currentProcess, turnaround);
                int waiting = turnaround - burstTime;
                waitingTimes.put(currentProcess, waiting);

                updateAverages();
            } else {
                ganttChart.append("| idle ");
            }
        }

        currentTime++;
    }


    private void updateAverages() {
        double totalWaiting = waitingTimes.values().stream().mapToInt(Integer::intValue).sum();
        double totalTurnaround = turnaroundTimes.values().stream().mapToInt(Integer::intValue).sum();
        int count = waitingTimes.size();

        avgWaitingTime = count > 0 ? totalWaiting / count : 0;
        avgTurnaroundTime = count > 0 ? totalTurnaround / count : 0;
    }

    @Override
    public synchronized void addProcess(int burstTime, int arrivalTime) {
        processCounter++;
        String processName = "P" + processCounter;


        int priority = processCounter;
        addProcessWithPriority(burstTime, arrivalTime, priority);
    }

    // Additional method to add process with specific priority
    public synchronized void addProcessWithPriority(int burstTime, int arrivalTime, int priority) {
        processCounter++;
        String processName = "P" + processCounter;

        burstTimeMap.put(processName, burstTime);
        arrivalTimes.put(processName, arrivalTime);
        priorityMap.put(processName, priority);

        notArrivedYet.add(processName);
    }

    @Override
    public synchronized int getRemainingBurst(int index) {
        if (currentProcess != null && index == 0) {
            int remaining = currentProcessEndTime - currentTime;
            return Math.max(remaining, 0);
        }
        return 0;
    }

    @Override
    public synchronized String getGanttChart() {
        return ganttChart+ "|";
    }

    @Override
    public synchronized double getAverageWaitingTime() {
        return avgWaitingTime;
    }

    @Override
    public synchronized double getAverageTurnaroundTime() {
        return avgTurnaroundTime;
    }

    @Override
    public synchronized String getCurrentProcessName() {
        return currentProcess;
    }
}