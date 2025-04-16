import java.util.List;
import java.util.Queue;
import java.util.ArrayList;

public class PriorityNonPreemptive extends Scheduler {
    private List<Process> completedProcesses = new ArrayList<>();

    public PriorityNonPreemptive(List<Process> processes) {
        super(processes);
    }

    public PriorityNonPreemptive() {}

    @Override
    public int schedule() {
        int processId = runningProcess != null ? runningProcess.getId() : 0;

        if (runningProcess == null) {
            Process selected = null;
            for (Process p : processQueue) {
                if (p.getArrivalTime() <= currentTime && p.getExecutionTime() < p.getBurstTime()) {
                    if (selected == null || p.getPriority() < selected.getPriority()) {
                        selected = p;
                    }
                }
            }
            runningProcess = selected;
            if (runningProcess != null) {
                processId = runningProcess.getId();
            }
        }

        if (runningProcess != null) {
            runningProcess.setExecutionTime(runningProcess.getExecutionTime() + 1);
            if (runningProcess.getExecutionTime() == runningProcess.getBurstTime()) {
                runningProcess.setWaitingTime(currentTime + 1 - runningProcess.getArrivalTime() - runningProcess.getBurstTime());
                runningProcess.setTurnaroundTime(currentTime + 1 - runningProcess.getArrivalTime());
                completedProcesses.add(runningProcess);
                runningProcess = null;
            }
        }

        currentTime++;
        return processId;
    }

    public void addProcess(int processId, int priority) {
        // Assuming the Process class does not take priority in the constructor
        Process newProcess = new Process(processId, 0, 0); // Default burst and arrival times
        newProcess.setPriority(priority); // Set priority separately if supported
        processQueue.add(newProcess);
    }
    
    public synchronized void addProcessWithPriority(int burstTime, int arrivalTime, int priority) {
        // Assuming the Process class does not take priority in the constructor
        Process newProcess = new Process(processQueue.size() + 1, burstTime, arrivalTime); // Create process
        newProcess.setPriority(priority); // Set priority separately if supported
        processQueue.add(newProcess);
    }


    public String getGanttChart() {
        StringBuilder ganttChart = new StringBuilder();
        for (Process p : completedProcesses) {
            ganttChart.append("P").append(p.getId()).append(" ");
        }
        return ganttChart.toString().trim();
    }

  
    public void schedule(int timeQuantum, Queue<Integer> processQueue) {
        throw new UnsupportedOperationException("Time quantum scheduling is not supported in non-preemptive priority scheduling.");
    }

    public double getAverageTurnaroundTime() {
        double totalTurnaroundTime = 0;
        for (Process p : completedProcesses) {
            totalTurnaroundTime += p.getTurnaroundTime();
        }
        return completedProcesses.isEmpty() ? 0.0 : totalTurnaroundTime / completedProcesses.size();
    }


    public double getAverageWaitingTime() {
        double totalWaitingTime = 0;
        for (Process p : completedProcesses) {
            totalWaitingTime += p.getWaitingTime();
        }
        return completedProcesses.isEmpty() ? 0.0 : totalWaitingTime / completedProcesses.size();
    }

  
    public int getRemainingBurst(int processId) {
        for (Process p : processQueue) {
            if (p.getId() == processId) {
                return p.getBurstTime() - p.getExecutionTime();
            }
        }
        return 0;
    }

    public String getCurrentProcessName() {
        return runningProcess != null ? "P" + runningProcess.getId() : "Idle";
    }
}
