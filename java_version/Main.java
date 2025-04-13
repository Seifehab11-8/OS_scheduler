// Removed package declaration to match the default package

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        List<Process> processes = new ArrayList<>();
        String schedulerType;

        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter the type of scheduler (e.g., SJFNonPreemptive): ");
            schedulerType = scanner.nextLine();

            System.out.print("Enter the number of processes: ");
            int numProcesses = scanner.nextInt();

            System.out.print("Do you use priority? (y/n): ");
            char priorityFlag = scanner.next().charAt(0);

            for (int i = 0; i < numProcesses; i++) {
                System.out.printf("Enter Process Arrival Time and Burst Time%s for Process %d: ",
                    (priorityFlag == 'y' ? ", Priority" : ""), i + 1);
                int arrivalTime = scanner.nextInt();
                int burstTime = scanner.nextInt();
                int priority = (priorityFlag == 'y') ? scanner.nextInt() : 0;
                processes.add(new Process(arrivalTime, burstTime, priority));
            }
        }

        Scheduler scheduler = null;
        try {
            scheduler = SchedulerFactory.createScheduler(schedulerType, processes);
        } catch (IllegalArgumentException e) {
            System.err.println(e.getMessage());
            System.out.println("Valid scheduler types: SJFNonPreemptive");
            return;
        }

        System.out.println("\nSimulating Gantt Chart:");
        StringBuilder topBorder = new StringBuilder("+");
        StringBuilder middleRow = new StringBuilder("|");
        StringBuilder bottomBorder = new StringBuilder("+");

        while (!scheduler.isFinished()) {
            int processId = scheduler.schedule();
            if (processId == 0) {
                middleRow.append("    -    |");
            } else {
                middleRow.append("    P").append(processId).append("    |");
            }
            topBorder.append("-----------+");
            bottomBorder.append("-----------+");

            System.out.println("\n" + topBorder);
            System.out.println(middleRow);
            System.out.println(bottomBorder);

            try {
                Thread.sleep(1000); // Wait for 1 second
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("Thread was interrupted");
            }
        }

        System.out.println(topBorder);
        System.out.println(middleRow);
        System.out.println(bottomBorder);

        System.out.println("\n\nProcess Details:");
        scheduler.printProcesses();
    }
}
