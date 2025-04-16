package org.example.cpuschedular;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.IOException;

public class secondController {

    public TextField newPriorityField;
    @FXML private Label ganttChartLabel;
    @FXML private TableView<ProcessData> burstTable;
    @FXML private TableColumn<ProcessData, String> processColumn;
    @FXML private TableColumn<ProcessData, Integer> remainingBurstColumn;
    @FXML private TableColumn<ProcessData, Integer> arrivalTimeColumn;
    @FXML private TableColumn<ProcessData, Integer> priorityColumn;
    @FXML private Label statsLabel;
    @FXML private Button toggleButton;
    @FXML private TextField newProcessField;
    @FXML public Button backButton;

    private final Object lock = new Object();
    private Scheduler scheduler;
    private ObservableList<ProcessData> processDataList;
    private Timeline timeline;
    private volatile boolean liveMode = true;
    private volatile boolean running = true;
    private Thread schedulingThread;
    private int arrivalCounter = 0;

    public void initialize() {
        processColumn.setCellValueFactory(cellData -> cellData.getValue().processNameProperty());
        remainingBurstColumn.setCellValueFactory(cellData -> cellData.getValue().remainingBurstProperty().asObject());
        arrivalTimeColumn.setCellValueFactory(cellData -> cellData.getValue().arrivalTimeProperty().asObject());
        priorityColumn.setCellValueFactory(cellData -> cellData.getValue().priorityProperty().asObject());
        processDataList = FXCollections.observableArrayList();
        burstTable.setItems(processDataList);
    }

    public void initializeScheduler(Scheduler scheduler, int numProcesses, int[] burstTimes, int[] priorities,int[] arrivalTimes) {
        this.scheduler = scheduler;

        synchronized (lock) {
            for (int i = 0; i < numProcesses; i++) {
                String processName = "P" + (i + 1);
                if (scheduler instanceof PriorityNonPreemptiveScheduler) {
                    ((PriorityNonPreemptiveScheduler) scheduler).addProcessWithPriority(burstTimes[i], arrivalTimes[i], priorities[i]);
                } else if (scheduler instanceof PriorityPreemptiveScheduler) {
                    ((PriorityPreemptiveScheduler) scheduler).addProcessWithPriority(burstTimes[i], arrivalTimes[i], priorities[i]);
                } else {
                    scheduler.addProcess(burstTimes[i], arrivalTimes[i]);
                }

                ProcessData process = new ProcessData(processName, burstTimes[i], arrivalTimes[i]);
                process.setPriority(priorities[i]);
                processDataList.add(process);
            }
        }
        startSchedulingThread();
        initializeTimeline();
    }

    private void startSchedulingThread() {
        schedulingThread = new Thread(() -> {
            while (running && !Thread.currentThread().isInterrupted()) {
                if (liveMode) {
                    synchronized (lock) {
                        try {
                            scheduler.schedule(processDataList.size(), null);
                            arrivalCounter++;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        updateProcessData();
                    }

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                } else {
                    try {
                        Thread.sleep(100);
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
    private void addNewProcess() {
        try {
            int burstTime = Integer.parseInt(newProcessField.getText());
            int priority;
            if (burstTime <= 0) throw new NumberFormatException();
            if(scheduler instanceof PriorityPreemptiveScheduler || scheduler instanceof  PriorityNonPreemptiveScheduler){
                 priority = Integer.parseInt(newPriorityField.getText());
                    if (priority < 0) throw new NumberFormatException();
            }
            else
                priority =0;
            synchronized (lock) {
                String processName = "P" + (processDataList.size() + 1);
                ProcessData process = new ProcessData(processName, burstTime, arrivalCounter);
                process.setPriority(priority);
                processDataList.add(process);

                if (scheduler instanceof PriorityNonPreemptiveScheduler) {
                    ((PriorityNonPreemptiveScheduler) scheduler).addProcessWithPriority(burstTime, arrivalCounter, priority);
                } else if (scheduler instanceof PriorityPreemptiveScheduler) {
                    ((PriorityPreemptiveScheduler) scheduler).addProcessWithPriority(burstTime, arrivalCounter, priority);
                } else {
                    scheduler.addProcess(burstTime, arrivalCounter);
                }

                newProcessField.clear();
                newPriorityField.clear();
            }
        } catch (NumberFormatException e) {
            showAlert();
        }
    }

    private void updateProcessData() {
        Platform.runLater(() -> {
            synchronized (lock) {
                String runningProcess = scheduler.getCurrentProcessName();
                for (ProcessData process : processDataList) {
                    if (process.processNameProperty().get().equals(runningProcess)) {
                        process.setRemainingBurst(scheduler.getRemainingBurst(0));
                        break;
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
            alert.setContentText("Please enter a valid burst time and priority (positive integers)");
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
    @FXML
    private void ReturnToMainScene() {
        try {
            // Load the main view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/cpuschedular/hello-view.fxml"));
            Parent root = loader.load();

            // Get the current stage
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setWidth(stage.getWidth());
            stage.setHeight(stage.getWidth());
            // Set the new scene
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static class ProcessData {
        private final StringProperty processName;
        private final IntegerProperty remainingBurst;
        private final IntegerProperty arrivalTime;
        private final IntegerProperty priorityValue;

        public ProcessData(String name, int burst, int arrival) {
            this.processName = new SimpleStringProperty(name);
            this.remainingBurst = new SimpleIntegerProperty(burst);
            this.arrivalTime = new SimpleIntegerProperty(arrival);
            this.priorityValue = new SimpleIntegerProperty();
        }

        public StringProperty processNameProperty() { return processName; }
        public IntegerProperty remainingBurstProperty() { return remainingBurst; }
        public IntegerProperty arrivalTimeProperty() { return arrivalTime; }
        public IntegerProperty priorityProperty() { return priorityValue; }

        public void setRemainingBurst(int value) { remainingBurst.set(value); }
        public void setPriority(int value) { priorityValue.set(value); }
    }
}
