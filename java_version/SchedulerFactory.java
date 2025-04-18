import java.util.List;

public class SchedulerFactory {
    public static Scheduler createScheduler(String type, List<Process> processes) {
        switch (type) {
            case "SJFNonPreemptive": // Ensure this matches the expected input
                return new SJFNonPreemptive(processes);
            case "FCFS": 
                return new FCFS(processes);
            case "PriorityNonPreemptive":
                return new PriorityNonPreemptive(processes);
            case "PriorityPreemptive":
                return new PriorityPreemptive(processes);
            case "SJFPreemptive":
                return new SJFPreemptive(processes);
	    case "RoundRobin":
                return new RoundRobinScheduler(processes , 2);	//Qauntum is fixed = 2 

            // Add other scheduler types here
            // Add other scheduler types here
            default:
                throw new IllegalArgumentException("Unknown scheduler type: " + type);
        }
    }
}
