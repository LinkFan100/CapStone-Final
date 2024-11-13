package com.example.capstone.Controllers;

import com.example.capstone.Database.InventoryDatabase;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
                System.out.println("Please Input User Name and Password.");
                SaveFile();
            } else if (createUser.isEmpty()) {
                System.out.println("Please Input User Name.");
                SaveFile();
            } else if (createPass.isEmpty()) {
                System.out.println("Please Input Password.");
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

    public void stageSwitch() {
        try {
            scene = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Views/signUp.fxml")));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        stage.setScene(new Scene(scene));
        stage.show();
    }
}
