package org.example.cpuschedular;

import java.util.*;

public class SJFPreemptiveScheduler implements Scheduler {
    private final Map<String, Integer> arrivalTimes = new HashMap<>();
    private final Map<String, Integer> completionTimes = new HashMap<>();
    private final Map<String, Integer> burstTimeMap = new HashMap<>();
    private final Map<String, Integer> remainingBurstTimes = new HashMap<>();
    private final Map<String, Integer> waitingTimes = new HashMap<>();
    private final Map<String, Integer> turnaroundTimes = new HashMap<>();
    private final List<String> notArrivedYet = new ArrayList<>();


    // Priority queue sorted by remaining burst time
    private final PriorityQueue<String> readyQueue = new PriorityQueue<>(
            Comparator.comparingInt(remainingBurstTimes::get)
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
        // First, move any arriving processes to the ready queue
        Iterator<String> iterator = notArrivedYet.iterator();
        while (iterator.hasNext()) {
            String proc = iterator.next();
            if (arrivalTimes.get(proc) <= currentTime) {
                readyQueue.add(proc);
                iterator.remove();
            }
        }

        currentTime++;

        // Preemption check
        if (currentProcess != null && !readyQueue.isEmpty()) {
            String shortestJob = readyQueue.peek();
            if (remainingBurstTimes.get(shortestJob) < remainingBurstTimes.get(currentProcess)) {
                readyQueue.add(currentProcess);
                currentProcess = readyQueue.poll();
            }
        }

        // Start new process if needed
        if (currentProcess == null || remainingBurstTimes.get(currentProcess) == 0) {
            if (!readyQueue.isEmpty()) {
                currentProcess = readyQueue.poll();
            } else {
                ganttChart.append("| idle ");
                return; // No process to run this tick
            }
        }

        // Run current process for 1 unit
        remainingBurstTimes.put(currentProcess, remainingBurstTimes.get(currentProcess) - 1);
        ganttChart.append("| ").append(currentProcess).append(" ");

        // Completion
        if (remainingBurstTimes.get(currentProcess) == 0) {
            completionTimes.put(currentProcess, currentTime);
            int turnaround = currentTime - arrivalTimes.get(currentProcess);
            turnaroundTimes.put(currentProcess, turnaround);
            int waiting = turnaround - burstTimeMap.get(currentProcess);
            waitingTimes.put(currentProcess, waiting);
            updateAverages();
        }
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

        burstTimeMap.put(processName, burstTime);
        remainingBurstTimes.put(processName, burstTime);
        arrivalTimes.put(processName, arrivalTime);

        notArrivedYet.add(processName);
    }


    @Override
    public synchronized int getRemainingBurst(int index) {
        // This is simplified - returns current process or 0
        // For complete implementation, track all processes
        if (index == 0 && currentProcess != null) {
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