package org.example.cpuschedular;

import java.util.*;

public class SJFNonPreemptiveScheduler implements Scheduler {
    private final Map<String, Integer> arrivalTimes = new HashMap<>();
    private final Map<String, Integer> completionTimes = new HashMap<>();
    private final Map<String, Integer> burstTimeMap = new HashMap<>();
    private final Map<String, Integer> waitingTimes = new HashMap<>();
    private final Map<String, Integer> turnaroundTimes = new HashMap<>();

    private final PriorityQueue<String> readyQueue = new PriorityQueue<>(
            Comparator.comparingInt(burstTimeMap::get)
    );

    private final List<String> allProcesses = new ArrayList<>(); // store all process names
    private final StringBuilder ganttChart = new StringBuilder();

    private String currentProcess = null;
    private int currentTime = 0;
    private int currentProcessEndTime = 0;

    private double avgWaitingTime = 0;
    private double avgTurnaroundTime = 0;
    private int processCounter = 0;

    @Override
    public synchronized void schedule(int numProcesses, Queue<Integer> burstTimes) {
        // Check for newly arrived processes and add to ready queue
        for (String process : allProcesses) {
            if (arrivalTimes.get(process) == currentTime) {
                readyQueue.add(process);
            }
        }

        // If no process is running or the current one is done
        if (currentProcess == null || currentTime >= currentProcessEndTime) {
            if (!readyQueue.isEmpty()) {
                currentProcess = readyQueue.poll();
                int burstTime = burstTimeMap.get(currentProcess);
                currentProcessEndTime = currentTime + burstTime;

                // Gantt chart
                ganttChart.append("| ");
                for (int i = 0; i < burstTime; i++) {
                    ganttChart.append(currentProcess).append(" ");
                }

                // Set times
                completionTimes.put(currentProcess, currentProcessEndTime);
                int turnaround = currentProcessEndTime - arrivalTimes.get(currentProcess);
                turnaroundTimes.put(currentProcess, turnaround);
                int waiting = turnaround - burstTime;
                waitingTimes.put(currentProcess, waiting);

                updateAverages();
            } else {
                ganttChart.append("| idle ");
                currentProcess = null;
            }
        }
        System.out.println("Process: " + currentProcess +
                " | Arrival: " + arrivalTimes.get(currentProcess) +
                " | Completion: " + currentTime +
                " | Turnaround: " + turnaroundTimes.get(currentProcess) +
                " |Completion time " + completionTimes.get(currentProcess)+
                " | Waiting: " + waitingTimes.get(currentProcess));

        currentTime++; // Tick time forward
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
        allProcesses.add(processName); // keep track of everything to check later
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
