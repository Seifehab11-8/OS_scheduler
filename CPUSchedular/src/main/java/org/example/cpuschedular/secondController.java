package org.example.cpuschedular;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Duration;

public class secondController {

    @FXML
    private Label ganttChartLabel;
    @FXML
    private TableView<ProcessData> burstTable;
    @FXML
    private TableColumn<ProcessData, String> processColumn;
    @FXML
    private TableColumn<ProcessData, Integer> remainingBurstColumn;
    @FXML
    private Label statsLabel;
    @FXML
    private Button toggleButton;
    @FXML
    private TextField newProcessField;
    @FXML
    private Button addProcessButton;

    private Scheduler scheduler;
    private int numProcesses;
    private int[] burstTimes;
    private ObservableList<ProcessData> processDataList;

    private Timeline timeline;
    private boolean liveMode = true;
    private Thread schedulingThread;
    private Thread inputThread;
    private volatile boolean running = true;

    public void initialize() {
        processColumn.setCellValueFactory(cellData -> cellData.getValue().processNameProperty());
        remainingBurstColumn.setCellValueFactory(cellData -> cellData.getValue().remainingBurstProperty().asObject());

        processDataList = FXCollections.observableArrayList();
        burstTable.setItems(processDataList);
    }

    public void initializeScheduler(Scheduler scheduler, int numProcesses, int[] burstTimes) {
        this.scheduler = scheduler;
        this.numProcesses = numProcesses;
        this.burstTimes = burstTimes;

        // Initialize process data
        for (int i = 0; i < numProcesses; i++) {
            processDataList.add(new ProcessData("P" + (i + 1), burstTimes[i]));
        }

        // Start scheduling thread
        startSchedulingThread();

        // Start input thread
        startInputThread();

        // Initialize timeline for UI updates
        initializeTimeline();
    }

    private void startSchedulingThread() {
        schedulingThread = new Thread(() -> {
            while (running) {
                if (liveMode) {
                    // Execute one scheduling step
                    scheduler.schedule(numProcesses, burstTimes);

                    // Update burst times from scheduler
                    updateProcessData();

                    try {
                        Thread.sleep(1000); // Simulate time quantum
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }
        });
        schedulingThread.setDaemon(true);
        schedulingThread.start();
    }

    private void startInputThread() {
        inputThread = new Thread(() -> {
            while (running) {
                // This thread would normally listen for external input
                // In our case, we'll use the JavaFX input components
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
            }
        });
        inputThread.setDaemon(true);
        inputThread.start();
    }

    private void initializeTimeline() {
        timeline = new Timeline(new KeyFrame(Duration.seconds(0.5), event -> {
            Platform.runLater(() -> {
                // Update Gantt chart
                ganttChartLabel.setText(scheduler.getGanttChart());

                // Update statistics
                updateStats(scheduler.getAverageWaitingTime(),
                        scheduler.getAverageTurnaroundTime());
            });
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    @FXML
    private void toggleLiveMode() {
        liveMode = !liveMode;
        if (liveMode) {
            toggleButton.setText("Pause Live Scheduling");
        } else {
            toggleButton.setText("Resume Live Scheduling");
        }
    }

    @FXML
    private void addNewProcess() {
        try {
            int burstTime = Integer.parseInt(newProcessField.getText());
            String processName = "P" + (processDataList.size() + 1);
            processDataList.add(new ProcessData(processName, burstTime));

            // Update scheduler with new process
            scheduler.addProcess(burstTime);
            numProcesses++;

            newProcessField.clear();
        } catch (NumberFormatException e) {
            showAlert();
        }
    }

    private void updateProcessData() {
        for (int i = 0; i < numProcesses; i++) {
            ProcessData process = processDataList.get(i);
            process.setRemainingBurst(scheduler.getRemainingBurst(i));
        }
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
            alert.setContentText("Please enter a valid burst time (integer)");
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
        if (inputThread != null) {
            inputThread.interrupt();
        }
    }

    // Process data model class
    public static class ProcessData {
        private final StringProperty processName;
        private final IntegerProperty remainingBurst;

        public ProcessData(String name, int burst) {
            this.processName = new SimpleStringProperty(name);
            this.remainingBurst = new SimpleIntegerProperty(burst);
        }

        // Property getters
        public StringProperty processNameProperty() { return processName; }
        public IntegerProperty remainingBurstProperty() { return remainingBurst; }

        // Value getters and setters
        public String getProcessName() { return processName.get(); }
        public int getRemainingBurst() { return remainingBurst.get(); }
        public void setRemainingBurst(int value) { remainingBurst.set(value); }
    }
}