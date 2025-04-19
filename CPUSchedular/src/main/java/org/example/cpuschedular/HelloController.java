package org.example.cpuschedular;


import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class HelloController {

    public TextField RRQuantum;
    public TextField Priority;
    public TextField ArrivalTimefield;

    @FXML
    private TextField numProcessesField;

    @FXML
    private TextField burstTimesField;

    @FXML
    private ComboBox<String> algorithmChoice;

    @FXML
    private Button submitButton;



    @FXML
    private void onSubmitClick() {
        try {
            int numProcesses = Integer.parseInt(numProcessesField.getText().trim());
            String selectedAlgorithm = algorithmChoice.getValue();
            String burstText = burstTimesField.getText().trim();
            String arrivalText = ArrivalTimefield.getText().trim();
            String[] burstStrings = burstText.split("\\s+");
            String[] arrivalStrings = arrivalText.isEmpty() ? new String[0] : arrivalText.split("\\s+");

            int[] burstTimes = new int[burstStrings.length];
            int[] arrivalTimes = new int[burstStrings.length];

            for (int i = 0; i < burstStrings.length; i++) {
                burstTimes[i] = Integer.parseInt(burstStrings[i].trim());
            }

            if (arrivalStrings.length > 0) {
                for (int i = 0; i < burstStrings.length; i++) {
                    // If there is a corresponding arrival time, parse it; otherwise, set to 0.
                    if (i < arrivalStrings.length) {
                        arrivalTimes[i] = Integer.parseInt(arrivalStrings[i].trim());
                    } else {
                        arrivalTimes[i] = 0;
                    }
                }
            } else {
                for (int i = 0; i < burstStrings.length; i++) {
                    arrivalTimes[i] = 0;
                }
            }

            Scheduler scheduler;
            switch (selectedAlgorithm) {
                case "FCFS":
                    scheduler = new FCFSScheduler();
                    break;
                case "SJF (Preemptive)":
                    scheduler = new SJFPreemptiveScheduler();
                    break;
                case "SJF (Non Preemptive)":
                    scheduler = new SJFNonPreemptiveScheduler();
                    break;
                case "Priority (Preemptive)":
                    scheduler = new PriorityPreemptiveScheduler();
                    break;
                case "Priority (Non Preemptive)":
                    scheduler = new PriorityNonPreemptiveScheduler();
                    break;
                case "Round Robin":
                    try {
                        scheduler = new RoundRobinScheduler(Integer.parseInt(RRQuantum.getText().trim()));
                    } catch (NumberFormatException e) {
                        showAlert();
                        RRQuantum.clear();
                        return;
                    }
                    break;
                default:
                    System.out.println("Unknown algorithm selected.");
                    return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/cpuschedular/second-view.fxml"));
            Parent chartRoot = loader.load();
            secondController chartController = loader.getController();

            // Prepare priorities
            int[] priorities;
            if (Objects.equals(selectedAlgorithm, "Priority (Preemptive)") ||
                    Objects.equals(selectedAlgorithm, "Priority (Non Preemptive)")) {

                String[] priorityStrings = Priority.getText().trim().split(" ");
                priorities = new int[priorityStrings.length];
                for (int i = 0; i < priorityStrings.length; i++) {
                    priorities[i] = Integer.parseInt(priorityStrings[i].trim());
                }
            } else {
                priorities = new int[burstTimes.length]; // default to 0 if not using priority
                for (int i = 0; i < burstTimes.length; i++) {
                    priorities[i] = 0;
                }
            }

            chartController.initializeScheduler(scheduler, burstStrings.length, burstTimes, priorities,arrivalTimes);

            // Switch scene
            Stage stage = (Stage) submitButton.getScene().getWindow();
            double width = submitButton.getScene().getWidth();
            double height = submitButton.getScene().getHeight();
            stage.setScene(new Scene(chartRoot, width, height));
            stage.show();

        } catch (NumberFormatException e) {
            System.err.println("Invalid input! Please check the number of processes and burst times.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void onEnter1(ActionEvent event) {
        if (event.getSource() == numProcessesField) {
            burstTimesField.requestFocus();
        }
    }
    @FXML
    public void onEnter2(ActionEvent event) {
        if (event.getSource() == burstTimesField) {
            ArrivalTimefield.requestFocus();
        }
    }
    @FXML
    public void onEnter3(ActionEvent event) {
        if (event.getSource() == ArrivalTimefield) {
            algorithmChoice.show();
            algorithmChoice.requestFocus();
        }
    }


    private void showAlert() {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Invalid Input");
            alert.setHeaderText(null);
            alert.setContentText("Please enter a positive integer quantum time");
            alert.showAndWait();
        });
    }
}
