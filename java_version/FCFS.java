import java.util.*;

public class FCFS extends Scheduler {

    public FCFS(List<Process> processes) {
        super(processes);
    }

    public FCFS() {
        super();
    }

    @Override
    public int schedule() {
        // Sort processes by arrival time (FCFS)
        processQueue.sort(Comparator.comparingInt(Process::getArrivalTime));

        while (!isFinished()) {
            // Find next process that has arrived and is not finished
            for (Process process : processQueue) {
                if (process.getArrivalTime() <= currentTime && process.getExecutionTime() < process.getBurstTime()) {
                    runningProcess = process;
                    break;
                }
            }

            if (runningProcess != null) {
                // Execute the running process
                runningProcess.setExecutionTime(runningProcess.getExecutionTime() + 1);

                if (runningProcess.getExecutionTime() == runningProcess.getBurstTime()) {
                    // Process finished: calculate turnaround and waiting times
                    int turnaroundTime = currentTime + 1 - runningProcess.getArrivalTime();
                    int waitingTime = turnaroundTime - runningProcess.getBurstTime();

                    runningProcess.setTurnaroundTime(turnaroundTime);
                    runningProcess.setWaitingTime(waitingTime);

                    runningProcess = null; // No running process anymore
                }
            }

            currentTime++; // Move time forward
        }

        return currentTime; // Return total time taken
    }
}
