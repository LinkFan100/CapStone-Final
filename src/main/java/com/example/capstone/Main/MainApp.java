package com.example.capstone.Main;

import com.example.capstone.Database.InventoryDatabase;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Objects;

public class MainApp extends Application {
    Stage mainWindow;

    public static void main(String[] args) {
        InventoryDatabase inventoryDatabase = new InventoryDatabase();
        inventoryDatabase.dbCreate();
        inventoryDatabase.tableCreate();

        launch(args);

    }

    @Override
    public void start(Stage primaryStage) throws Exception {


        mainWindow = primaryStage;
        Parent root = FXMLLoader.load(Objects.requireNonNull(MainApp.class.getResource("/Views/signUp.fxml")));
        primaryStage.setTitle("");
        primaryStage.setScene(new Scene(root, 700, 575));
        primaryStage.show();
//        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("/Views/signUp.fxml"));
//        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
//        primaryStage.setTitle("Hello!");
//        primaryStage.setScene(scene);
//        primaryStage.show();


    }
}
