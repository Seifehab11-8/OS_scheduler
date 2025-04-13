import java.util.Comparator;
import java.util.List;

public class SJFNonPreemptive extends Scheduler {
    public SJFNonPreemptive(List<Process> processes) {
        super(processes);
    }

    public SJFNonPreemptive() {}

    @Override
    public int schedule() {
        int processId = runningProcess != null ? runningProcess.getId() : 0; // Store the current running process ID

        // If no process is running, find the next process to run
        if (runningProcess == null) {
            runningProcess = processQueue.stream()
                .filter(p -> p.getArrivalTime() <= currentTime && p.getExecutionTime() < p.getBurstTime())
                .min(Comparator.comparingInt(Process::getBurstTime))
                .orElse(null);

            if (runningProcess != null) {
                processId = runningProcess.getId(); // Update processId with the new running process
            }
        }

        // If a process is running, update its execution time
        if (runningProcess != null) {
            runningProcess.setExecutionTime(runningProcess.getExecutionTime() + 1);

            // If the process completes, calculate waiting and turnaround times
            if (runningProcess.getExecutionTime() == runningProcess.getBurstTime()) {
                runningProcess.setWaitingTime(currentTime + 1 - runningProcess.getArrivalTime() - runningProcess.getBurstTime());
                runningProcess.setTurnaroundTime(currentTime + 1 - runningProcess.getArrivalTime());
                runningProcess = null; // Reset the running process
            }
        }

        currentTime++; // Increment the current time
        return processId; // Return the ID of the running process or 0 if no process is running
    }
}
