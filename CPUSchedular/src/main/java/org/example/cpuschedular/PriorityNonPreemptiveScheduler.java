package org.example.cpuschedular;

import java.util.*;

public class PriorityNonPreemptiveScheduler implements Scheduler {

	@Override
	public void addProcess(int processId, int priority) {
		// TODO: Implement logic to add a process with the given ID and priority
	}
	public synchronized void addProcessWithPriority(int burstTime, int arrivalTime, int priority) {
        
    }

	@Override
	public String getGanttChart() {
		// TODO: Implement logic to return the Gantt chart as a list of strings
		return "";
	}

	@Override
	public void schedule(int timeQuantum, Queue<Integer> processQueue) {
		// TODO: Implement scheduling logic based on the time quantum and process queue
	}

	@Override
	public double getAverageTurnaroundTime() {
		// TODO: Implement logic to calculate and return the average turnaround time
		return 0.0;
	}

	@Override
	public double getAverageWaitingTime() {
		// TODO: Implement logic to calculate and return the average waiting time
		return 0.0;
	}

	@Override
	public int getRemainingBurst(int processId) {
		// TODO: Implement logic to return the remaining burst time for the given process ID
		return 0;
	}

	@Override
	public String getCurrentProcessName() {
		// TODO: Implement logic to return the name of the current process
		return "";
	}
}