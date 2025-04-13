public class Process {
    private static int ID_COUNTER = 1;
    private int id;
    private int priority;
    private int arrivalTime;
    private int burstTime;
    private int executionTime;
    private int waitingTime;
    private int turnaroundTime;

    public Process(int arrivalTime, int burstTime, int priority) {
        this.id = ID_COUNTER++;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.priority = priority;
        this.executionTime = 0;
        this.waitingTime = 0;
        this.turnaroundTime = 0;
    }

    public int getId() {
        return id;
    }

    public int getPriority() {
        return priority;
    }

    public int getArrivalTime() {
        return arrivalTime;
    }

    public int getBurstTime() {
        return burstTime;
    }

    public int getExecutionTime() {
        return executionTime;
    }

    public int getWaitingTime() {
        return waitingTime;
    }

    public int getTurnaroundTime() {
        return turnaroundTime;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public void setExecutionTime(int executionTime) {
        this.executionTime = executionTime;
    }

    public void setWaitingTime(int waitingTime) {
        this.waitingTime = waitingTime;
    }

    public void setTurnaroundTime(int turnaroundTime) {
        this.turnaroundTime = turnaroundTime;
    }
}
