package org.example.cpuschedular;

import java.util.*;

public class RoundRobinScheduler implements Scheduler {
    private final Map<String, Integer> arrivalTimes = new HashMap<>();
    private final Map<String, Integer> completionTimes = new HashMap<>();
    private final Map<String, Integer> burstTimeMap = new HashMap<>();
    private final Map<String, Integer> remainingBurstTimes = new HashMap<>();
    private final Map<String, Integer> waitingTimes = new HashMap<>();
    private final Map<String, Integer> turnaroundTimes = new HashMap<>();
    private final List<String> notArrivedYet = new ArrayList<>();
    private final Queue<String> readyQueue = new LinkedList<>();

    // Current process tracking
    private String currentProcess = null;
    private int currentTime = 0;
    private int timeSliceRemaining ;
    private final StringBuilder ganttChart = new StringBuilder();
    private final int timeQuantum;

    // Average metrics
    private double avgWaitingTime = 0;
    private double avgTurnaroundTime = 0;
    private int processCounter = 0;

    public RoundRobinScheduler(int timeQuantum) {
        this.timeQuantum = timeQuantum;
        this.timeSliceRemaining = timeQuantum;
    }

    @Override
    public synchronized void schedule(int numProcesses, Queue<Integer> burstTimes) {
        // Move newly arrived processes into readyQueue
        Iterator<String> iterator = notArrivedYet.iterator();
        while (iterator.hasNext()) {
            String proc = iterator.next();
            if (arrivalTimes.get(proc) <= currentTime) {
                readyQueue.add(proc);
                iterator.remove();
            }
        }

        // Check if current process has completed its time quantum or finished execution
        if (currentProcess != null && (timeSliceRemaining == 0 ||
                remainingBurstTimes.get(currentProcess) == 0)) {
            // If process still has burst time, put it back in queue
            if (remainingBurstTimes.get(currentProcess) > 0) {
                readyQueue.add(currentProcess);
            } else {
                // Process completed
                completionTimes.put(currentProcess, currentTime);
                int turnaround = currentTime - arrivalTimes.get(currentProcess);
                turnaroundTimes.put(currentProcess, turnaround);
                int waiting = turnaround - burstTimeMap.get(currentProcess);
                waitingTimes.put(currentProcess, waiting);
                updateAverages();
            }
            currentProcess = null;
        }

        // Get next process if needed
        if (currentProcess == null && !readyQueue.isEmpty()) {
            currentProcess = readyQueue.poll();
            timeSliceRemaining = timeQuantum;
        }

        // Execute current process
        if (currentProcess != null) {
            remainingBurstTimes.put(currentProcess,
                    remainingBurstTimes.get(currentProcess) - 1);
            timeSliceRemaining--;
            ganttChart.append("| ").append(currentProcess).append(" ");
        } else {
            ganttChart.append("| idle ");
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

        burstTimeMap.put(processName, burstTime);
        arrivalTimes.put(processName, arrivalTime);
        remainingBurstTimes.put(processName, burstTime);

        if (arrivalTime <= currentTime) {
            readyQueue.add(processName);
        } else {
            notArrivedYet.add(processName);
        }
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