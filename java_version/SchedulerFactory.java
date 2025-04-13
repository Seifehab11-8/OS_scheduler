import java.util.List;

public class SchedulerFactory {
    public static Scheduler createScheduler(String type, List<Process> processes) {
        switch (type) {
            case "SJFNonPreemptive": // Ensure this matches the expected input
                return new SJFNonPreemptive(processes);
            // Add other scheduler types here
            default:
                throw new IllegalArgumentException("Unknown scheduler type: " + type);
        }
    }
}
