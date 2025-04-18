package org.example.cpuschedular;

import java.util.*;

public class PriorityPreemptiveScheduler implements Scheduler {
    private final Map<String, Integer> arrivalTimes = new HashMap<>();
    private final Map<String, Integer> completionTimes = new HashMap<>();
    private final Map<String, Integer> burstTimeMap = new HashMap<>();
    private final Map<String, Integer> remainingBurstTimes = new HashMap<>();
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
    private final StringBuilder ganttChart = new StringBuilder();

    // Average metrics
    private double avgWaitingTime = 0;
    private double avgTurnaroundTime = 0;
    private int processCounter = 0;

    @Override
    public synchronized void schedule(int numProcesses, Queue<Integer> burstTimes) {
        // Move newly arrived processes into readyQueue
        Iterator<String> iterator = notArrivedYet.iterator();
        while (iterator.hasNext()) {
            String proc = iterator.next();
            if (arrivalTimes.get(proc) <= currentTime) {
                readyQueue.add(proc);
                iterator.remove();

                // Check for preemption
                if (currentProcess != null &&
                        priorityMap.get(proc) < priorityMap.get(currentProcess)) {
                    readyQueue.add(currentProcess);
                    currentProcess = readyQueue.poll();
                }
            }
        }

        // Preemption check (again in case something just arrived)
        if (currentProcess != null && !readyQueue.isEmpty()) {
            String highestPriority = readyQueue.peek();
            if (priorityMap.get(highestPriority) < priorityMap.get(currentProcess)) {
                readyQueue.add(currentProcess);
                currentProcess = readyQueue.poll();
            }
        }

        // Pick next process if current one is null or finished
        if (currentProcess == null || remainingBurstTimes.get(currentProcess) == 0) {
            if (!readyQueue.isEmpty()) {
                currentProcess = readyQueue.poll();
            } else {
                ganttChart.append("| idle ");
                currentTime++;
                return;
            }
        }

        // Execute for one unit
        remainingBurstTimes.put(currentProcess,
                remainingBurstTimes.get(currentProcess) - 1);
        ganttChart.append("| ").append(currentProcess).append(" ");

        // Completion check
        if (remainingBurstTimes.get(currentProcess) == 0) {
            completionTimes.put(currentProcess, currentTime + 1);
            int turnaround = completionTimes.get(currentProcess) - arrivalTimes.get(currentProcess);
            turnaroundTimes.put(currentProcess, turnaround);
            int waiting = turnaround - burstTimeMap.get(currentProcess);
            waitingTimes.put(currentProcess, waiting);
            updateAverages();
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
        int priority = processCounter;
        addProcessWithPriority(burstTime, arrivalTime, priority);
    }

    public synchronized void addProcessWithPriority(int burstTime, int arrivalTime, int priority) {
        String processName = "P" + (++processCounter);

        burstTimeMap.put(processName, burstTime);
        arrivalTimes.put(processName, arrivalTime);
        remainingBurstTimes.put(processName, burstTime);
        priorityMap.put(processName, priority);

        notArrivedYet.add(processName);
    }


    @Override
    public synchronized int getRemainingBurst(int index) {
        if (currentProcess != null && index == 0) {
            return remainingBurstTimes.get(currentProcess);
        }
        return 0;
    }

    @Override
    public synchronized String getGanttChart() {
        return ganttChart + "|";
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