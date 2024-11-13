package com.example.capstone.Controllers;

import com.example.capstone.Database.InventoryDatabase;
import com.example.capstone.Model.Inventory;
import com.example.capstone.Model.Part;
import com.example.capstone.Model.Product;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;


public class MainFormController implements Initializable {
    private static final String DB_NAME = "Inventory";
    private static final String url = "jdbc:sqlite:" + DB_NAME + ".db";
    //  database version
    private static final int DB_VERSION = 1;
    // Part Table
    private static final String Part_Table = "Parts";
    private static final String Part_Id = "partId";
    private static final String Part_Name = "part";
    private static final String Part_Price = "price";
    private static final String Part_Stock = "stock";
    private static final String Part_Min = "min";
    private static final String Part_Max = "max";
    private static final String OutsourcedInhouse = "outsourcedinhouse";
    private static final String Part_Product_Id = "partProductId";
    //  Product Table
    private static final String Product_Table = "Products";
    private static final String Product_Id = "id";
    private static final String Product_Name = "name";
    private static final String Product_Price = "price";
    private static final String Product_Stock = "stock";
    private static final String Product_Min = "min";
    private static final String Product_Max = "max";
    //  Inhouse Part Table
    private static final String Inhouse_Part_Table = "InhouseParts";
    private static final String Inhouse_Part_Id = "inhousePartId";
    private static final String Machine_Id = "machineId";
    //  Outsourced Part Table
    private static final String Outsourced_Part_Table = "OutsourcedParts";
    private static final String Outsourced_Part_Id = "outsourcedPartsId";
    private static final String Company_Name = "companyName";
    //  Logged_User Table
    private static final String Logged_User_Table = "LoggedUser";
    private static final String Logged_User_ID = "loggedId";
    private static final String Logged_User_Name = "loggedName";
    private static final String Logged_TimeStamp = "loggedTime";
    private static int productsId;
    private static int partsId;
    partsModifyFormController PartsModifyFormController = new partsModifyFormController();
    Parent scene;
    Stage stage;
    // Parts Table
    @FXML
    private TableView<Part> partsTable;
    @FXML
    private TableColumn<Part, Integer> partID;
    @FXML
    private TableColumn<Part, String> partName;
    @FXML
    private TableColumn<Part, Integer> partInv;
    @FXML
    private TableColumn<Part, Double> partPrice;
    @FXML
    private TextField partsSearchBar;
    //Products Table
    @FXML
    private TableView<Product> productsTable;
    @FXML
    private TableColumn<Product, Integer> productID;
    @FXML
    private TableColumn<Product, String> productName;
    @FXML
    private TableColumn<Product, Integer> productInv;
    @FXML
    private TableColumn<Product, Double> productPrice;
    @FXML
    private TextField productsSearchBar;
    private InventoryDatabase inventoryDatabase = new InventoryDatabase();
    private String outIn;

    /**
     * Search Bar method That looks up parts that matches values entered.
     * One Error that occurred was search no showing anything when I typed into search bar
     * The way I solved this is by readjusting the lookup part name and id method  in inventory
     */
    @FXML
    void partsSearch() {
        String searchinput = partsSearchBar.getText();
        try {
            ObservableList<Part> partSearch = Inventory.lookupPart(searchinput);
            if (partSearch.isEmpty()) {
                int idSearch = Integer.parseInt(searchinput);
                Part pSearch = Inventory.lookupPart(idSearch);
                partSearch.add(pSearch);

                if (pSearch == null) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Search Error");
                    alert.setContentText("No Matching Id");
                    alert.showAndWait();
                }
            }

            partsTable.setItems(partSearch);

        } catch (Exception e) {
            partsTable.setItems(null);
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Search Error");
            alert.setContentText("No Matching Results found.");
            alert.showAndWait();
        }

    }

    /**
     * Switch to View to add Part
     *
     * @param actionevent
     * @throws IOException
     */
    @FXML
    void partsAddClick(ActionEvent actionevent) throws IOException {
        stage = (Stage) ((Button) actionevent.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("/Views/partsAddForm.fxml"));
        stage.setTitle("Add Part");
        stage.setScene(new Scene(scene));
        stage.show();
    }

    /**
     * Opens Parts Modify Screen
     *
     * @param actionevent
     * @throws IOException
     */
    @FXML
    void partsModifyClick(ActionEvent actionevent) throws IOException {

        if (partsTable.getSelectionModel().getSelectedItem() == null) {
            Alert minMaxAlert = new Alert(Alert.AlertType.WARNING);
            minMaxAlert.setTitle("Error");
            minMaxAlert.setContentText("Nothing is selected to Modify");
            minMaxAlert.showAndWait();
        } else {
            partsId = partsTable.getSelectionModel().getSelectedItem().getId();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/Views/partsModifyForm.fxml"));
            loader.load();
            partsModifyFormController modController = loader.getController();
            modController.modifySelectedData(partsId);
            stage = (Stage) ((Button) actionevent.getSource()).getScene().getWindow();
            Parent scene = loader.getRoot();
            stage.setTitle("Modify Part");
            stage.setScene(new Scene(scene));
            stage.show();
        }
    }

    /**
     * Delete On Click Method for Parts Tables
     */
    @FXML
    void partsDeleteClick() {
        Part part = partsTable.getSelectionModel().getSelectedItem();
        String partID = String.valueOf(part.getId());
        try {
            outIn = PartsModifyFormController.oldOutIn(partID);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmation");
        confirmationAlert.setContentText("Are you sure you want to Delete this part?");
        Optional<ButtonType> result = confirmationAlert.showAndWait();
        if (result.get() == ButtonType.OK) {
            inventoryDatabase.partDelete(partID);
            if (outIn.equals("IH")) {
                inventoryDatabase.inHousePartDelete(partID);
            }
            if (outIn.equals("OS")) {
                inventoryDatabase.outsourcePartDelete(partID);
            }
            partsTable.refresh();
            Inventory.deletePart(part);
            boolean a = true;
            signUpController.SaveFile("\nDelete Part attempt on: ", a);
        }
    }

    /**
     * Search Bar method That looks up products that matches values entered.
     *
     * @param actionEvent
     */
    @FXML
    void productSearch(ActionEvent actionEvent) {
        String productSearchinput = productsSearchBar.getText();
        try {
            ObservableList<Product> productSearch = Inventory.lookupProduct(productSearchinput);
            if (productSearch.isEmpty()) {
                int proidSearch = Integer.parseInt(productSearchinput);
                Product proSearch = Inventory.lookupProduct(proidSearch);
                productSearch.add(proSearch);

                if (proSearch == null) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Search Error");
                    alert.setContentText("No Matching Id");
                    alert.showAndWait();
                }
            }
            productsTable.setItems(productSearch);

        } catch (Exception e) {
            productsTable.setItems(null);
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Search Error");
            alert.setContentText("No Matching Results found.");
            alert.showAndWait();
        }

    }

    /**
     * Switch to Add Product View
     *
     * @param actionevent
     * @throws IOException
     */
    @FXML
    void productsAddClick(ActionEvent actionevent) throws IOException {
        Stage stage = (Stage) ((Button) actionevent.getSource()).getScene().getWindow();
        scene = FXMLLoader.load(getClass().getResource("/Views/addProductParts.fxml"));
        stage.setTitle("Add Product");
        stage.setScene(new Scene(scene));
        stage.show();
    }

    /**
     * Opens Modify Product View
     *
     * @param actionevent
     * @throws IOException
     */
    @FXML
    void productsModifyClick(ActionEvent actionevent) throws IOException {

        if (productsTable.getSelectionModel().getSelectedItem() == null) {
            Alert minMaxAlert = new Alert(Alert.AlertType.WARNING);
            minMaxAlert.setTitle("Error");
            minMaxAlert.setContentText("Nothing is selected to Modify");
            minMaxAlert.showAndWait();
        } else {
            productsId = productsTable.getSelectionModel().getSelectedItem().getId();
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/Views/modifyProductParts.fxml"));
            loader.load();
            modifyProductPartsController mod2Controller = loader.getController();
            mod2Controller.modifyProductSelectedData(productsId);

            stage = (Stage) ((Button) actionevent.getSource()).getScene().getWindow();
            Parent scene = loader.getRoot();
            stage.setTitle("Modify Product");
            stage.setScene(new Scene(scene));
            stage.show();
        }
    }


    /**
     * Delete On Click Method for Products Tables
     *
     * @param actionEvent
     */
    @FXML
    void productsDeleteClick(ActionEvent actionEvent) throws Exception {
        Product product = productsTable.getSelectionModel().getSelectedItem();
        int idDelete = product.getId();
        boolean empty = inventoryDatabase.productAssociatedPartCheck(String.valueOf(idDelete));
        if (empty) {
            boolean a = false;
            signUpController.SaveFile("\nDelete Product attempt on: ", a);
            Alert aPAlert = new Alert(Alert.AlertType.WARNING);
            aPAlert.setTitle("Warning");
            aPAlert.setContentText("Product has at least 1 associated part, please remove part from product to Delete");
            aPAlert.showAndWait();
        } else {
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Confirmation");
            confirmationAlert.setContentText("Are you sure you want to Delete this Product?");
            Optional<ButtonType> result = confirmationAlert.showAndWait();


            if (result.get() == ButtonType.OK) {
                inventoryDatabase.productDelete(String.valueOf(idDelete));
                productsTable.refresh();
                Inventory.deleteProduct(product);
                boolean a = true;
                signUpController.SaveFile("\nDelete Product attempt on: ", a);
            }
        }
    }

    /**
     * *Loads default data into the Part and Product Tables
     *
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Inventory.clearAllPart();
        inventoryDatabase.partTablePopulate();
        partsTable.setItems(Inventory.getAllPart());
        partID.setCellValueFactory(new PropertyValueFactory<>("id"));
        partName.setCellValueFactory(new PropertyValueFactory<>("name"));
        partInv.setCellValueFactory(new PropertyValueFactory<>("stock"));
        partPrice.setCellValueFactory(new PropertyValueFactory<>("price"));


        Inventory.clearAllProduct();
        inventoryDatabase.productTablePopulate();
        productsTable.setItems((Inventory.getAllProduct()));
        productID.setCellValueFactory(new PropertyValueFactory<>("id"));
        productName.setCellValueFactory(new PropertyValueFactory<>("name"));
        productInv.setCellValueFactory(new PropertyValueFactory<>("stock"));
        productPrice.setCellValueFactory(new PropertyValueFactory<>("price"));


    }

    public void mainExitClicked(ActionEvent actionEvent) {
        Alert closeConfirm = new Alert(Alert.AlertType.CONFIRMATION);
        closeConfirm.setTitle("Exit");
        closeConfirm.setContentText("Are you Sure you want to Exit?");
        Optional<ButtonType> result = closeConfirm.showAndWait();
        if (result.get() == ButtonType.OK) {
            Platform.exit();
        }

    }


}
