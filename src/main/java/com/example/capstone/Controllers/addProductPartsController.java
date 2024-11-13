package com.example.capstone.Controllers;

import com.example.capstone.Database.InventoryDatabase;
import com.example.capstone.Model.*;
import com.example.capstone.Model.Product;
import javafx.collections.FXCollections;
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

public class addProductPartsController implements Initializable {


    Stage stage;
    Parent scene;
    private ObservableList<Part> associatedParts = FXCollections.observableArrayList();
    private ObservableList<Part> associatedPartsList = FXCollections.observableArrayList();

    private Product newProduct = new Product(0, "", 0.00, 0, 0, 0);
    //Products Table
    @FXML
    private TextField addProductId;
    @FXML
    private TextField addProductName;
    @FXML
    private TextField addProductInv;
    @FXML
    private TextField addProductPrice;
    @FXML
    private TextField addProductMax;
    @FXML
    private TextField addProductMin;
    @FXML
    private TableView<Part> partsOfProductTable;
    @FXML
    private TableColumn<Part, Integer> partID;
    @FXML
    private TableColumn<Part, String> partName;
    @FXML
    private TableColumn<Part, Integer> partsInventoryLevel;
    @FXML
    private TableColumn<Part, Double> partsPrice;
    @FXML
    private TextField productPartSearchBar;

    @FXML
    private TableView<Part> AssociatedPartsTable;
    @FXML
    private TableColumn<Part, Integer> partID1;
    @FXML
    private TableColumn<Part, String> partName1;
    @FXML
    private TableColumn<Part, Integer> partsInventoryLevel1;
    @FXML
    private TableColumn<Part, Double> partsPrice1;
    private InventoryDatabase inventoryDatabase = new InventoryDatabase();

    /**
     * Saves Product data to product Table
     *
     * @param actionEvent
     * @throws IOException
     */
    @FXML
    void addProductSaveClick(ActionEvent actionEvent) throws IOException {

        try {
            Boolean flag = false;


//            newProduct.setName(addProductName.getText());
//            newProduct.setStock(Integer.parseInt(addProductInv.getText()));
//            newProduct.setPrice(Double.parseDouble(addProductPrice.getText()));
//            newProduct.setMin(Integer.parseInt(addProductMin.getText()));
//            newProduct.setMax(Integer.parseInt(addProductMax.getText()));
//            newProduct.setAssociatedPart(associatedParts);


            String productName = addProductName.getText();
            int productInv = Integer.parseInt(addProductInv.getText());
            double productPrice = Double.parseDouble(addProductPrice.getText());
            int productMin = Integer.parseInt(addProductMin.getText());
            int productMax = Integer.parseInt(addProductMax.getText());


            for (int a = 0; a < productName.length(); a++) {
                if (a == 0 && productName.charAt(a) == '-')
                    continue;
                if (!Character.isDigit(productName.charAt(a)))
                    flag = false;


                if (Character.isDigit(productName.charAt(a))) {
                    flag = true;
                }
            }
            if (flag == true) {
                Alert nameNumAlert = new Alert(Alert.AlertType.WARNING);
                nameNumAlert.setTitle("Critical Name Error");
                nameNumAlert.setContentText("Name field contains numbers");
                nameNumAlert.showAndWait();
            } else if (productMin > productMax) {
                Alert minMaxAlert = new Alert(Alert.AlertType.WARNING);
                minMaxAlert.setTitle("Critical Min/Max Error");
                minMaxAlert.setContentText("Error: The Min is Greater then the Max.");
                minMaxAlert.showAndWait();
            } else if (productInv > productMax || productMin > productInv) {
                Alert inventoryAlert = new Alert(Alert.AlertType.WARNING);
                inventoryAlert.setTitle("Critical Inventory Error");
                inventoryAlert.setContentText("Inventory input is not within the Min/Max Parameters");
                inventoryAlert.showAndWait();

            } else if (addProductName.getText() == null) {
                Alert nameAlert = new Alert(Alert.AlertType.WARNING);
                nameAlert.setTitle("Critical Name Error");
                nameAlert.setContentText("Name field is empty please input a name");
                nameAlert.showAndWait();


            } else {
                inventoryDatabase.productAdd(productName, String.valueOf(productPrice), String.valueOf(productInv), String.valueOf(productMin), String.valueOf(productMax));
                String productId = inventoryDatabase.getProductId(productName);
                if (!associatedParts.isEmpty()) {
                    associatedPartsList = AssociatedPartsTable.getItems();
                    for (Part part : associatedPartsList) {
                        String partid = String.valueOf(part.getId());
                        String partName = part.getName();
                        String partPrice = String.valueOf(part.getPrice());
                        String partStock = String.valueOf(part.getStock());
                        String partMin = String.valueOf(part.getMin());
                        String partMax = String.valueOf(part.getMax());
                        inventoryDatabase.addAssociatedPart(partName, partPrice, partStock, partMin, partMax, productId, partid);
                    }
                }
                boolean a = true;
                signUpController.SaveFile("\nAdd Product attempt on: ", a);
                stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
                scene = FXMLLoader.load(getClass().getResource("/Views/MainForm.fxml"));
                stage.setScene(new Scene(scene));
                stage.show();
            }

        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning Dialog");
            alert.setContentText("Please input valid values for all text fields");
            alert.showAndWait();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Exits Out of the Add Product View and Returns to the main view
     *
     * @param actionEvent
     * @throws IOException
     */
    @FXML
    void addProductCancelButtonClick(ActionEvent actionEvent) throws IOException {

        Alert Alertcancel = new Alert(Alert.AlertType.CONFIRMATION);
        Alertcancel.setTitle("Confirm");
        Alertcancel.setContentText("Are you sure? All data Inputted will be lost.");
        Optional<ButtonType> result = Alertcancel.showAndWait();
        if (result.get() == ButtonType.OK) {
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            scene = FXMLLoader.load(getClass().getResource("/Views/MainForm.fxml"));
            stage.setTitle("Inventory Management");
            stage.setScene(new Scene(scene));
            stage.show();
        }
    }

    /**
     * Adds a part to a product
     *
     * @param actionEvent
     */
    @FXML
    void addProductPartClick(ActionEvent actionEvent) {
        Part partSelected = partsOfProductTable.getSelectionModel().getSelectedItem();
        if (partsOfProductTable.getSelectionModel().getSelectedItem() == null) {
            Alert alertNull = new Alert(Alert.AlertType.WARNING);
            alertNull.setTitle("Error");
            alertNull.setContentText("Nothing is Selected.");
            alertNull.showAndWait();
        } else
            associatedParts.add(partSelected);
        AssociatedPartsTable.setItems(associatedParts);

    }

    /**
     * Deletes parts associated with selected product
     *
     * @param actionEvent
     */
    @FXML
    void removeAssociatedPartClick(ActionEvent actionEvent) {

        Part partA = AssociatedPartsTable.getSelectionModel().getSelectedItem();
        if (AssociatedPartsTable.getSelectionModel().getSelectedItem() == null) {
            Alert alertNull = new Alert(Alert.AlertType.WARNING);
            alertNull.setTitle("Error");
            alertNull.setContentText("Nothing is Selected.");
            alertNull.showAndWait();
        } else {
            Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmationAlert.setTitle("Confirmation");
            confirmationAlert.setContentText("Are you sure you want to Delete this part?");
            Optional<ButtonType> result = confirmationAlert.showAndWait();
            if (result.get() == ButtonType.OK) {
                associatedParts.remove(partA);
            }
        }

    }

    /**
     * Searches throught the parts table in product
     *
     * @param actionEvent
     */
    @FXML
    void productPartSearch(ActionEvent actionEvent) {
        String searchinput = productPartSearchBar.getText();
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

            partsOfProductTable.setItems(partSearch);

        } catch (Exception e) {
            partsOfProductTable.setItems(null);
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Search Error");
            alert.setContentText("No Matching Results found.");
            alert.showAndWait();
        }

    }

    /**
     * increment Id
     *
     * @return
     */


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        partsOfProductTable.setItems(Inventory.getAllPart());
        partID.setCellValueFactory(new PropertyValueFactory<>("id"));
        partName.setCellValueFactory(new PropertyValueFactory<>("name"));
        partsInventoryLevel.setCellValueFactory(new PropertyValueFactory<>("stock"));
        partsPrice.setCellValueFactory(new PropertyValueFactory<>("price"));

        partID1.setCellValueFactory(new PropertyValueFactory<>("id"));
        partName1.setCellValueFactory(new PropertyValueFactory<>("name"));
        partsInventoryLevel1.setCellValueFactory(new PropertyValueFactory<>("stock"));
        partsPrice1.setCellValueFactory(new PropertyValueFactory<>("price"));


    }

}
