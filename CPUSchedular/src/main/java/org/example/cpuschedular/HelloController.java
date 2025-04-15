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

    @FXML
    private TextField numProcessesField;

    @FXML
    private TextField burstTimesField;

    @FXML
    private ComboBox<String> algorithmChoice;

    @FXML
    private Button submitButton;

    @FXML
    private void onEnter1(ActionEvent event) {
        if (event.getSource() == numProcessesField) {
            burstTimesField.requestFocus();
        }
    }

    @FXML
    private void onSubmitClick() {
        try {
            int numProcesses = Integer.parseInt(numProcessesField.getText().trim());
            String selectedAlgorithm = algorithmChoice.getValue();

            String[] burstStrings = burstTimesField.getText().trim().split(" ");
            int[] burstTimes = new int[burstStrings.length];
            for (int i = 0; i < burstStrings.length; i++) {
                burstTimes[i] = Integer.parseInt(burstStrings[i].trim());
            }

            if (selectedAlgorithm == null || selectedAlgorithm.isEmpty()) {
                System.err.println("Please select an algorithm.");
                return;
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

            chartController.initializeScheduler(scheduler, numProcesses, burstTimes, priorities);

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

    public void onEnter2(ActionEvent event) {
        if (event.getSource() == RRQuantum) {
            submitButton.fire();
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
