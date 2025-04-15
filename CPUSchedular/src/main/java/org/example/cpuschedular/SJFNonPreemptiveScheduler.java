package org.example.cpuschedular;

import java.util.*;

public class SJFNonPreemptiveScheduler implements Scheduler {

	@Override
	public void addProcess(int processId, int burstTime) {
		// TODO: Implement the logic to add a process
	}

	@Override
	public String getGanttChart() {
		// TODO: Implement the logic to return the Gantt chart
		return "";
	}

	@Override
	public void schedule(int currentTime, Queue<Integer> processQueue) {
		// TODO: Implement the scheduling logic
	}

	@Override
	public double getAverageTurnaroundTime() {
		// TODO: Implement the logic to calculate average turnaround time
		return 0.0;
	}

	@Override
	public double getAverageWaitingTime() {
		// TODO: Implement the logic to calculate average waiting time
		return 0.0;
	}

	@Override
	public int getRemainingBurst(int processId) {
		// TODO: Implement the logic to get the remaining burst time for a process
		return 0;
	}

	@Override
	public String getCurrentProcessName() {
		// TODO: Implement the logic to get the name of the current process
		return "";
	}
}