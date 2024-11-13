package com.example.capstone.Controllers;

import com.example.capstone.Database.InventoryDatabase;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;


public class CreateUser {


    static final DateTimeFormatter tF = DateTimeFormatter.ofPattern("hh:mm a");
    static final LocalDate lD = LocalDate.now();
    private static final InventoryDatabase inventoryDatabase = new InventoryDatabase();
    static LocalTime lT = LocalTime.now();
    private static boolean attemptS = false;
    boolean found;
    Parent scene;
    Stage stage;
    @FXML
    private Button createCancelBtn;
    @FXML
    private TextField createPasswordField;
    @FXML
    private Button createUserBtn;
    @FXML
    private TextField createUserField;

    /**
     * Saves the user creation result to a log file.
     *
     * This method logs the success or failure message to a file named "user_create_activity.txt".
     * If the file does not exist, it will be created. For subsequent writes, the log entry will be appended.
     *
     * The log entry includes the date and time of the attempt, and the success or failure status.
     *
     * Upon encountering exceptions during the file operations, a RuntimeException is thrown.
     */
    public static void SaveFile() {
        String success = "User Creation" + "\nattempt on: " + lD + " " + lT.format(tF) + "\nStatus: Success\n\n";
        String failure = "User Creation" + "\nattempt on: " + lD + " " + lT.format(tF) + "\nStatus: Fail\n\n";

        if (attemptS) {
            try {
                File login_activity = new File("user_create_activity.txt");
                if (login_activity.createNewFile()) {
                    FileWriter fileWriter = new FileWriter("user_create_activity.txt");
                    fileWriter.write(success);
                    fileWriter.close();
                } else {
                    FileWriter fileWriter2 = new FileWriter("user_create_activity.txt", true);
                    BufferedWriter bW = new BufferedWriter(fileWriter2);
                    bW.write(success);
                    bW.newLine();
                    bW.close();
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        } else
            try {
                File login_activity = new File("user_create_activity.txt");
                if (login_activity.createNewFile()) {
                    FileWriter fileWriter = new FileWriter("user_create_activity.txt");
                    fileWriter.write(failure);
                    fileWriter.close();
                } else {
                    FileWriter fileWriter2 = new FileWriter("user_create_activity.txt", true);
                    BufferedWriter bW = new BufferedWriter(fileWriter2);
                    bW.write(failure);
                    bW.newLine();
                    bW.close();
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
    }

    @FXML
    public void initialize() {
        createUserBtn.setOnMouseClicked(mouseEvent -> {
            String createUser = createUserField.getText();
            String createPass = createPasswordField.getText();

            if (createUser.isEmpty() && createPass.isEmpty()) {
                // Add Alert
                callAlert("User Name","Password");
                SaveFile();
            } else if (createUser.isEmpty()) {
                callAlert("User Name");
                SaveFile();
            } else if (createPass.isEmpty()) {
                callAlert("Password");
                SaveFile();
            } else {
                try {
                    found = inventoryDatabase.checkLoggedUserName(createUser);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                if (found) {

                } else {
                    inventoryDatabase.userCreate(createUser, createPass);
                    attemptS = true;
                    SaveFile();
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Success Dialog");
                    alert.setContentText("User has been Successfully Created");
                    alert.showAndWait();
                    stage = (Stage) ((Button) mouseEvent.getSource()).getScene().getWindow();
                    stageSwitch();
                }
            }

        });
        createCancelBtn.setOnMouseClicked(mouseEvent -> {
            stage = (Stage) ((Button) mouseEvent.getSource()).getScene().getWindow();

            stageSwitch();
        });
    }

    /**
     * Switches the current stage's scene to the sign-up view.
     * <p>
     * This method attempts to load the sign-up FXML file and set it as the scene of the current stage.
     * If an IOException occurs during the loading process, a RuntimeException is thrown.
     * The new scene is then displayed by calling `show()` on the stage.
     */
    public void stageSwitch() {
        try {
            scene = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Views/signUp.fxml")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        stage.setScene(new Scene(scene));
        stage.show();
    }

    /**
     * Displays a warning alert dialog with a message requesting valid input for the specified fields.
     *
     * @param name the name of the first field that requires valid input
     * @param pass the name of the second field that requires valid input
     */
    public void callAlert(String name,String pass){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning Dialog");
        alert.setContentText("Please input valid " + name + " and " + pass);
        alert.showAndWait();
    }
    public void callAlert(String name){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning Dialog");
        alert.setContentText("Please input valid " + name);
        alert.showAndWait();
    }
}
