import java.util.Comparator;
import java.util.List;

public class SJFPreemptive extends Scheduler {
    private int runningId = 0;

    public SJFPreemptive(List<Process> processes) {
        super(processes);
    }

    public SJFPreemptive() {}

    @Override
    public int schedule() {
        int id = 0;
        Process nextProcess = null;

        for (Process p : processQueue) {
            if (p.getArrivalTime() <= currentTime && p.getExecutionTime() < p.getBurstTime()) {
                if (nextProcess == null || 
                    (p.getBurstTime() - p.getExecutionTime()) < 
                    (nextProcess.getBurstTime() - nextProcess.getExecutionTime())) {
                    nextProcess = p;
                }
            }
        }

        if (nextProcess != null) {
            if (runningId != nextProcess.getId()) {
                runningId = nextProcess.getId();
                runningProcess = nextProcess;
            }

            id = runningId;
            runningProcess.setExecutionTime(runningProcess.getExecutionTime() + 1);

            if (runningProcess.getExecutionTime() == runningProcess.getBurstTime()) {
                int finishTime = currentTime + 1;
                runningProcess.setWaitingTime(finishTime - runningProcess.getArrivalTime() - runningProcess.getBurstTime());
                runningProcess.setTurnaroundTime(finishTime - runningProcess.getArrivalTime());
                runningId = 0;
            }
        }
        else {
            runningId = 0;
        }

        currentTime++;
        return id;
    }
}
