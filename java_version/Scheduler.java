import java.util.ArrayList;
import java.util.List;

public abstract class Scheduler {
    protected List<Process> processQueue = new ArrayList<>();
    protected Process runningProcess = null;
    protected int currentTime = 0;

    public Scheduler(List<Process> processes) {
        this.processQueue.addAll(processes);
    }

    public Scheduler() {}

    public void addProcess(Process process) {
        processQueue.add(process);
    }

    public boolean isFinished() {
        return processQueue.stream().allMatch(p -> p.getBurstTime() == p.getExecutionTime());
    }

    public void printProcesses() {
        System.out.println("ID\tArrival Time\tBurst Time\tWaiting Time\tTurnaround Time");
        for (Process process : processQueue) {
            System.out.printf("%d\t%d\t\t%d\t\t%d\t\t%d%n",
                process.getId(),
                process.getArrivalTime(),
                process.getBurstTime(),
                process.getWaitingTime(),
                process.getTurnaroundTime());
        }
    }

    public abstract int schedule();
}
