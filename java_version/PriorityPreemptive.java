import java.util.*;

public class PriorityPreemptive extends Scheduler {
    private boolean newAddFlag = true;

    public PriorityPreemptive() {
        super();
    }

    public PriorityPreemptive(List<Process> processes) {
        super();
        processQueue.addAll(processes);
    }

    @Override
    public int schedule() {
        int processId = -1;
        if (newAddFlag) {
            processQueue.sort(Comparator.comparingInt(Process::getPriority));
        }
        for (Process process : processQueue) {
            if (process.getArrivalTime() <= currentTime) {
                if (process.getBurstTime() != process.getExecutionTime()) {
                    processId = process.getId();
                    process.setExecutionTime(process.getExecutionTime() + 1);
                    if (process.getBurstTime() == process.getExecutionTime()) {
                        process.setWaitingTime(currentTime - process.getArrivalTime() - process.getBurstTime() + 1);
                        process.setTurnaroundTime(currentTime - process.getArrivalTime() + 1);
                    }
                    break;
                }
            }
        }
        currentTime++;
        newAddFlag = false;
        return processId;
    }

    @Override
    public void addProcess(Process process) {
        newAddFlag = true;
        processQueue.add(process);
    }

    @Override
    public void printProcesses() {
        processQueue.sort(Comparator.comparingInt(Process::getId));
        super.printProcesses();
    }
}
