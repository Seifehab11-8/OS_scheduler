package org.example.cpuschedular;

import java.util.*;

public class RoundRobinScheduler implements Scheduler {
	private final int timeQuantum;

	public RoundRobinScheduler(int timeQuantum) {
        this.timeQuantum = timeQuantum;
    }

	@Override
	public void addProcess(int processId, int burstTime) {
		// TODO: Implement this method
	}

	@Override
	public String getGanttChart() {
		// TODO: Implement this method
		return "";
	}

	@Override
	public void schedule(int timeQuantum, Queue<Integer> processQueue) {
		// TODO: Implement this method
	}

	@Override
	public double getAverageTurnaroundTime() {
		// TODO: Implement this method
		return 0.0;
	}

	@Override
	public double getAverageWaitingTime() {
		// TODO: Implement this method
		return 0.0;
	}

	@Override
	public int getRemainingBurst(int processId) {
		// TODO: Implement this method
		return 0;
	}

	@Override
	public String getCurrentProcessName() {
		// TODO: Implement this method
		return "";
	}
}