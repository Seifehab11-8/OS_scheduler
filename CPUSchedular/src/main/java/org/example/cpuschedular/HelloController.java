package org.example.cpuschedular;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloController {

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
        // Move focus from numProcessesField to burstTimesField
        if (event.getSource() == numProcessesField) {
            burstTimesField.requestFocus();
        }
    }

    @FXML
    private void onSubmitClick(ActionEvent event) {
        try {
            // Get number of processes
            int numProcesses = Integer.parseInt(numProcessesField.getText().trim());

            // Parse burst times (assuming comma-separated integers)
            String[] burstStrings = burstTimesField.getText().trim().split(" ");
            int[] burstTimes = new int[burstStrings.length];
            for (int i = 0; i < burstStrings.length; i++) {
                burstTimes[i] = Integer.parseInt(burstStrings[i].trim());
            }

            // Retrieve the selected algorithm
            String selectedAlgorithm = algorithmChoice.getValue();
            if (selectedAlgorithm == null || selectedAlgorithm.isEmpty()) {
                System.err.println("Please select an algorithm.");
                return;
            }

            // Instantiate the scheduler based on selection
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
                case " Priority (Preemptive)":
                case "Priority (Preemptive)":
                    scheduler = new PriorityPreemptiveScheduler();
                    break;
                case " Priority (NOn Preemptive)":
                case "Priority (NOn Preemptive)":
                    scheduler = new PriorityNonPreemptiveScheduler();
                    break;
                case "Round Robin":
                    scheduler = new RoundRobinScheduler();
                    break;
                default:
                    System.out.println("Unknown algorithm selected.");
                    return;
            }

            // Optionally, you might call scheduler.schedule() to pre-calculate metrics etc.
            // Now, switch to the chart view scene and pass the necessary data
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/cpuschedular/second-view.fxml"));
            Parent chartRoot = loader.load();

            // Get the ChartController to pass data
            secondController chartController = loader.getController();
            chartController.initializeScheduler(scheduler, numProcesses, burstTimes);

            // Switch scene
            Stage stage = (Stage) submitButton.getScene().getWindow();
            stage.setScene(new Scene(chartRoot));
            stage.show();

        } catch (NumberFormatException e) {
            System.err.println("Invalid input! Please check the number of processes and burst times.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
