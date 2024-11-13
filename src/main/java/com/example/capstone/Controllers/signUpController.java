package com.example.capstone.Controllers;

import com.example.capstone.Database.InventoryDatabase;
import com.example.capstone.Model.Logged_User;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Optional;

public class signUpController {
    static final DateTimeFormatter tF = DateTimeFormatter.ofPattern("hh:mm a");
    static final LocalDate lD = LocalDate.now();
    static final ZoneId zidnow = ZoneId.systemDefault();
    static final Timestamp tS = Timestamp.from(Instant.now());
    private static final InventoryDatabase inventoryDatabase = new InventoryDatabase();
    static LocalTime lT = null;
    static int logg_Id;
    private static int appId1;
    //End of Time and date Variables and formats
    private static Timestamp ts2;
    private static long diffoutput;
    private static String userNameSearch;
    private static String passWordSearch;
    private static boolean attemptS = false;
    private static boolean namePassMatch = false;
    private static ZoneId databaseZone;
    private static ZoneId displayID;
    private static String zdtZ;
    public Button signUpExitButton;
    boolean flag_en = false;
    String currentLanguage = System.getProperty("user.language");
    Parent scene;
    Stage stage;
    @FXML
    private TextField userNameBar;
    @FXML
    private TextField passWordBar;
    @FXML
    private Label timeZoneDisplay;
    @FXML
    private Button loginButton;
    @FXML
    private Text userNameText;
    @FXML
    private Text passWordText;
    @FXML
    private Text createUser;

    /**
     * Saves user activity to file
     */
    public static void SaveFile() {
        String success = "User: " + Logged_User.getLogged_User_Name() + "\nLogin attempt on: " + lD + " " + lT.format(tF) + "\nStatus: Success\n\n";
        String failure = "User: " + userNameSearch + "\nLogin attempt on: " + lD + " " + lT.format(tF) + "\nStatus: Fail\n\n";

        if (attemptS) {
            try {
                File login_activity = new File("login_activity.txt");
                if (login_activity.createNewFile()) {
                    FileWriter fileWriter = new FileWriter("login_activity.txt");
                    fileWriter.write(success);
                    fileWriter.close();
                } else {
                    FileWriter fileWriter2 = new FileWriter("login_activity.txt", true);
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
                File login_activity = new File("login_activity.txt");
                if (login_activity.createNewFile()) {
                    FileWriter fileWriter = new FileWriter("login_activity.txt");
                    fileWriter.write(failure);
                    fileWriter.close();
                } else {
                    FileWriter fileWriter2 = new FileWriter("login_activity.txt", true);
                    BufferedWriter bW = new BufferedWriter(fileWriter2);
                    bW.write(failure);
                    bW.newLine();
                    bW.close();
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
    }

    public static void SaveFile(String a, boolean attemptS) {
        String success = "User: " + Logged_User.getLogged_User_Name() + a + lD + " " + lT.format(tF) + "\nStatus: Success\n\n";
        String failure = "User: " + Logged_User.getLogged_User_Name() + a + lD + " " + lT.format(tF) + "\nStatus: Fail\n\n";

        if (attemptS) {
            try {
                File login_activity = new File("application_activity.txt");
                if (login_activity.createNewFile()) {
                    FileWriter fileWriter = new FileWriter("application_activity.txt");
                    fileWriter.write(success);
                    fileWriter.close();
                } else {
                    FileWriter fileWriter2 = new FileWriter("application_activity.txt", true);
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
                File login_activity = new File("application_activity.txt");
                if (login_activity.createNewFile()) {
                    FileWriter fileWriter = new FileWriter("application_activity.txt");
                    fileWriter.write(failure);
                    fileWriter.close();
                } else {
                    FileWriter fileWriter2 = new FileWriter("application_activity.txt", true);
                    BufferedWriter bW = new BufferedWriter(fileWriter2);
                    bW.write(failure);
                    bW.newLine();
                    bW.close();
                }

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
    }

    /**
     * Runs methods on initialization of Controller
     */
    @FXML
    public void initialize() {
        lT = LocalTime.now();


        timeZoneDisplay.setText(lT.format(tF) + " " + zidnow);

        createUser.setOnMouseEntered(mouseEvent -> {

            createUser.setUnderline(true);
            createUser.setFill(Paint.valueOf("#1e16ab"));
            createUser.setCursor(Cursor.HAND);
        });
        createUser.setOnMouseExited(mouseEvent -> {
            createUser.setUnderline(false);
            createUser.setFill(Paint.valueOf("#1a1a1b"));
        });
        createUser.setOnMouseClicked(mouseEvent -> {
            stage = (Stage) ((Text) mouseEvent.getSource()).getScene().getWindow();
            try {
                scene = FXMLLoader.load(Objects.requireNonNull(signUpController.class.getResource("/Views/CreateUser.fxml")));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            stage.setScene(new Scene(scene));
            stage.show();
        });

    }

    /**
     * On click sign in user, by running userNameCheck and if successful
     * Saves information to Logged_User Model
     *
     * @param mouseEvent
     * @throws IOException
     */
    @FXML
    void loginUser(MouseEvent mouseEvent) throws IOException {
        userNameSearch = userNameBar.getText();
        passWordSearch = passWordBar.getText();


        if (passWordSearch.isEmpty() && userNameSearch.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Blank Fields");
            alert.setContentText("Please Enter Username and Password");
            alert.showAndWait();
        } else if (userNameSearch.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("User Name Error");
            alert.setContentText("Please enter Username");
            alert.showAndWait();
        } else if (passWordSearch.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Password Error");
            alert.setContentText("Please enter Password");
            alert.showAndWait();
        } else {

            try {
                flag_en = inventoryDatabase.UserNameCheck(userNameSearch, passWordSearch);
                inventoryDatabase.getUser_ID(userNameSearch, passWordSearch);
                if (flag_en) {
                    new Logged_User(logg_Id, userNameSearch, tS);
                    stage = (Stage) ((Button) mouseEvent.getSource()).getScene().getWindow();
                    scene = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/Views/MainForm.fxml")));
                    stage.setScene(new Scene(scene));
                    stage.show();
                    attemptS = true;

                }


            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                SaveFile();
            }
        }
    }

    /**
     * Exits program
     *
     * @param actionEvent
     */
    public void signUpExitButtonClick(ActionEvent actionEvent) {
        Alert closeConfirm = new Alert(Alert.AlertType.CONFIRMATION);
        closeConfirm.setTitle("Exit");
        closeConfirm.setContentText("Are you Sure you want to Exit?");
        Optional<ButtonType> result = closeConfirm.showAndWait();
        if (result.get() == ButtonType.OK) {
            Platform.exit();
        }
    }


}

