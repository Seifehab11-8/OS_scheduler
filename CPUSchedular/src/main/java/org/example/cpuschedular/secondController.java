package org.example.cpuschedular;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;

public class secondController {

    @FXML private Label ganttChartLabel;
    @FXML private TableView<ProcessData> burstTable;
    @FXML private TableColumn<ProcessData, String> processColumn;
    @FXML private TableColumn<ProcessData, Integer> remainingBurstColumn;
    @FXML private TableColumn<ProcessData, Integer> arrivalTimeColumn;
    @FXML private Label statsLabel;
    @FXML private Button toggleButton;
    @FXML private TextField newProcessField;

    private final Object lock = new Object(); // For thread synchronization
    private Scheduler scheduler;
    private ObservableList<ProcessData> processDataList;
    private Timeline timeline;
    private volatile boolean liveMode = true;
    private volatile boolean running = true;
    private Thread schedulingThread;
    private int arrivalCounter =1;

    public void initialize() {
        processColumn.setCellValueFactory(cellData -> cellData.getValue().processNameProperty());
        remainingBurstColumn.setCellValueFactory(cellData -> cellData.getValue().remainingBurstProperty().asObject());
        arrivalTimeColumn.setCellValueFactory(cellData -> cellData.getValue().arrivalTimeProperty().asObject());
        processDataList = FXCollections.observableArrayList();
        burstTable.setItems(processDataList);
    }

    public void initializeScheduler(Scheduler scheduler, int numProcesses, int[] burstTimes) {
        this.scheduler = scheduler;

        synchronized (lock) {
            for (int i = 0; i < numProcesses; i++) {
                String processName = "P" + (i + 1);
                scheduler.addProcess(burstTimes[i],0);
                processDataList.add(new ProcessData(processName, burstTimes[i],0));
            }
        }
        startSchedulingThread();
        initializeTimeline();
    }

    /**
     * Starts the scheduling thread which periodically updates the scheduler.
     */
    private void startSchedulingThread() {
        schedulingThread = new Thread(() -> {
            while (running && !Thread.currentThread().isInterrupted()) {
                if (liveMode) {
                    synchronized (lock) {
                        try {
                            scheduler.schedule(processDataList.size(),null);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        updateProcessData();
                    }

                    try {
                        Thread.sleep(1000); // 1 second per quantum
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                } else {
                    try {
                        Thread.sleep(100); // Reduced CPU usage when paused
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        });
        schedulingThread.setDaemon(true);
        schedulingThread.setName("Scheduling-Thread");
        schedulingThread.start();
    }


    /**
     * Initializes the timeline that updates the UI elements periodically.
     */
    private void initializeTimeline() {
        timeline = new Timeline(new KeyFrame(Duration.seconds(0.5), event -> {
            Platform.runLater(() -> {
                synchronized (lock) {
                    ganttChartLabel.setText(scheduler.getGanttChart());
                    updateStats(scheduler.getAverageWaitingTime(), scheduler.getAverageTurnaroundTime());
                }
            });
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    @FXML
    private void toggleLiveMode() {
        liveMode = !liveMode;
        Platform.runLater(() ->
                toggleButton.setText(liveMode ? "Pause Live Scheduling" : "Resume Live Scheduling")
        );
    }

    @FXML
    private void addNewProcess(ActionEvent event) {
        try {
            int burstTime = Integer.parseInt(newProcessField.getText());
            if (burstTime <= 0) throw new NumberFormatException();

            synchronized (lock) {
                String processName = "P" + (processDataList.size() + 1);
                processDataList.add(new ProcessData(processName, burstTime, arrivalCounter));
                scheduler.addProcess(burstTime, arrivalCounter++);
                newProcessField.clear();
            }
        } catch (NumberFormatException e) {
            showAlert();
        }
    }

    /**
     * Updates the process table data.
     * For tick-based schedulers, you might only get an updated remaining burst for the currently running process.
     * Other algorithms might update all processes, so this loop is kept generic.
     */
    private void updateProcessData() {
        Platform.runLater(() -> {
            synchronized (lock) {
                // Get the current running process name from the scheduler.
                String runningProcess = "";
                if (scheduler instanceof FCFSScheduler) {
                    runningProcess = ((FCFSScheduler) scheduler).getCurrentProcessName();
                }

                // Iterate over the processDataList and update only the row matching the running process.
                for (ProcessData process : processDataList) {
                    if (process.processNameProperty().get().equals(runningProcess)) {
                        // We assume getRemainingBurst(0) corresponds to the current running process.
                        process.setRemainingBurst(scheduler.getRemainingBurst(0));
                        break; // Only update one process.
                    }
                }
            }
        });
    }

    private void updateStats(double avgWaitingTime, double avgTurnaroundTime) {
        statsLabel.setText(String.format("Avg Waiting: %.2f | Avg Turnaround: %.2f",
                avgWaitingTime, avgTurnaroundTime));
    }

    private void showAlert() {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Invalid Input");
            alert.setHeaderText(null);
            alert.setContentText("Please enter a positive integer burst time");
            alert.showAndWait();
        });
    }

    public void shutdown() {
        running = false;

        if (timeline != null) {
            timeline.stop();
        }

        if (schedulingThread != null) {
            schedulingThread.interrupt();
        }

        try {
            if (schedulingThread != null) schedulingThread.join(200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static class ProcessData {
        private final StringProperty processName;
        private final IntegerProperty remainingBurst;
        private final IntegerProperty arrivalTime;

        public ProcessData(String name, int burst, int arrival) {
            this.processName = new SimpleStringProperty(name);
            this.remainingBurst = new SimpleIntegerProperty(burst);
            this.arrivalTime = new SimpleIntegerProperty(arrival);

        }

        public StringProperty processNameProperty() { return processName; }
        public IntegerProperty remainingBurstProperty() { return remainingBurst; }
        public IntegerProperty arrivalTimeProperty() { return arrivalTime; }
        public void setRemainingBurst(int value) { remainingBurst.set(value); }
    }
}
