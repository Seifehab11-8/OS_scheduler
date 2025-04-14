import java.util.List;

public class PriorityNonPreemptive extends Scheduler {
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
                runningProcess = null;
            }
        }

        currentTime++;
        return processId;
    }
}
