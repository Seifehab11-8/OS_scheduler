import java.util.List;

public class RoundRobinScheduler extends Scheduler {
    private int quantum;
    private int currentIndex;

    public RoundRobinScheduler(List<Process> processQueue, int quantum) {
        super(processQueue);
        this.quantum = quantum;
        this.currentIndex = 0;
    }

    public RoundRobinScheduler(int quantum) {
        super();
        this.quantum = quantum;
        this.currentIndex = 0;
    }

    @Override
    public int schedule() {
        int executedId = 0;
        int count = 0;

        if (processQueue.isEmpty()) {
            currentTime++;
            return 0; // Nothing to execute
        }

        while (count < processQueue.size()) {
            Process p = processQueue.get(currentIndex);

            if (p.getArrivalTime() <= currentTime &&
                p.getExecutionTime() < p.getBurstTime()) {

                executedId = p.getId();
                int remainingTime = p.getBurstTime() - p.getExecutionTime();
                int timeSlice = Math.min(quantum, remainingTime);

                for (int t = 0; t < timeSlice; ++t) {
                    p.setExecutionTime(p.getExecutionTime() + 1);
                    currentTime++;

                    if (p.getExecutionTime() == p.getBurstTime()) {
                        p.setWaitingTime(currentTime - p.getArrivalTime() - p.getBurstTime());
                        p.setTurnaroundTime(currentTime - p.getArrivalTime());
                        break;
                    }
                }

                currentIndex = (currentIndex + 1) % processQueue.size();
                return executedId;
            }

            currentIndex = (currentIndex + 1) % processQueue.size();
            count++;
        }

        currentTime++;
        return 0;
    }
}
