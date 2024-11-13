package com.example.capstone.Controllers;
//here rev 1

import com.example.capstone.Database.InventoryDatabase;
import com.example.capstone.Model.*;
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
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Optional;
import java.util.ResourceBundle;


public class modifyProductPartsController implements Initializable {
    private static final String DB_NAME = "Inventory";
    private static final String url = "jdbc:sqlite:" + DB_NAME + ".db";
    private static final String Product_Table = "Products";
    private static final String Product_Id = "id";
    private static final String Product_Name = "name";
    private static final String Product_Price = "price";
    private static final String Product_Stock = "stock";
    private static final String Product_Min = "min";
    private static final String Product_Max = "max";
    private final ObservableList<Part> associatedParts = FXCollections.observableArrayList();
    Stage stage;
    Parent scene;
    AssociatedPart associatedPart = new AssociatedPart(0, 0, "", 0, 0, 0, 0);
    int id;
    int id2;
    ArrayList<Integer> numbersDelete = new ArrayList<>();
    ArrayList<Integer> numbersAdd = new ArrayList<>();
    ArrayList<Integer> numbersCheck = new ArrayList<>();
    private Product newProduct;
    @FXML
    private TextField modifyProductName;
    @FXML
    private TextField modifyProductInv;
    @FXML
    private TextField modifyProductPrice;
    @FXML
    private TextField modifyProductMax;
    @FXML
    private TextField modifyProductMin;
    @FXML
    private TableView<Part> modifypartsOfProductTable;
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
    private TableView<AssociatedPart> modifyAssociatedPartsTable;
    @FXML
    private TableColumn<AssociatedPart, Integer> partID1;
    @FXML
    private TableColumn<AssociatedPart, String> partName1;
    @FXML
    private TableColumn<AssociatedPart, Integer> partsInventoryLevel1;
    @FXML
    private TableColumn<AssociatedPart, Double> partsPrice1;
    private InventoryDatabase inventoryDatabase = new InventoryDatabase();

    /**
     * Loads Data into modify form associatedParts Table and make a product object newProduct.
     *
     * @param
     */
    public void modifyProductSelectedData(int productId) {
        id = productId;
        inventoryDatabase.associatedPartsTablePopulate(String.valueOf(id));
        productModifyPopulate(String.valueOf(id));
        id2 = id;


    }

    /**
     * Adds new associated part in modify view.
     *
     * @param actionEvent
     */
    @FXML
    void modifyProductPartClick(ActionEvent actionEvent) {

        Part partSelected = modifypartsOfProductTable.getSelectionModel().getSelectedItem();
        if (modifypartsOfProductTable.getSelectionModel().getSelectedItem() == null) {
            Alert alertNull = new Alert(Alert.AlertType.WARNING);
            alertNull.setTitle("Error");
            alertNull.setContentText("Nothing is Selected.");
            alertNull.showAndWait();
        } else
            associatedParts.add(partSelected);
        numbersAdd.add(partSelected.getId());
        toAssociatedPartTable(partSelected.getId(), partSelected.getName(), partSelected.getPrice(), partSelected.getStock(), partSelected.getMin(), partSelected.getMax());


    }

    /**
     * Remove Associated Part in Modify View
     *
     * @param actionEvent
     */
    @FXML
    void modifyRemoveAssociatedPartClick(ActionEvent actionEvent) throws SQLException {

        AssociatedPart partA = modifyAssociatedPartsTable.getSelectionModel().getSelectedItem();
        if (modifyAssociatedPartsTable.getSelectionModel().getSelectedItem() == null) {
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
                String idString = String.valueOf(partA.getAssociatedId());
                boolean exists = inventoryDatabase.associatedPartCheck(idString);
                if (!exists) {
                    Inventory.deleteAssociatedPart(partA);
                } else {
                    numbersDelete.add(partA.getAssociatedId());
                    Inventory.deleteAssociatedPart(partA);
                }

            }
        }
    }

    /**
     * Save Changes Done to Object in Modify Form.
     *
     * @param actionEvent
     * @throws IOException
     */
    @FXML
    void modifyProductSaveChangesClick(ActionEvent actionEvent) throws IOException {

        try {
            Boolean flag = false;


            String productName = modifyProductName.getText();
            double productPrice = Double.parseDouble(modifyProductPrice.getText());
            int productInv = Integer.parseInt(modifyProductInv.getText());
            int productMin = Integer.parseInt(modifyProductMin.getText());
            int productMax = Integer.parseInt(modifyProductMax.getText());

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

            } else if (productName == null) {
                Alert nameAlert = new Alert(Alert.AlertType.WARNING);
                nameAlert.setTitle("Critical Name Error");
                nameAlert.setContentText("Name field is empty please input a name");
                nameAlert.showAndWait();

            } else {

                inventoryDatabase.productUpdate(modifyProductName.getText(), modifyProductPrice.getText(), modifyProductInv.getText(),
                        modifyProductMin.getText(), modifyProductMax.getText(), String.valueOf(id));
                if (!numbersDelete.isEmpty()) {
                    inventoryDatabase.associatedPartDelete(numbersDelete);
                }
                if (!numbersAdd.isEmpty()) {
                    for (int add : numbersAdd)
                        inventoryDatabase.modifyAddAssociatedPart(String.valueOf(add), String.valueOf(id));
                }
                Inventory.clearAllAssociatedParts();
                boolean a = true;
                signUpController.SaveFile("\nModify Product attempt on: ", a);
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
     * Cancel Modify and return to Main View.
     *
     * @param actionEvent
     * @throws IOException
     */
    @FXML
    void modifyProductCancelButtonClick(ActionEvent actionEvent) throws IOException {
        Alert Alertcancel = new Alert(Alert.AlertType.CONFIRMATION);
        Alertcancel.setTitle("Confirm");
        Alertcancel.setContentText("Are you sure? All data Inputted will be lost.");
        Optional<ButtonType> result = Alertcancel.showAndWait();
        if (result.get() == ButtonType.OK) {
            Inventory.clearAllAssociatedParts();
            Stage stage = (Stage) ((Button) actionEvent.getSource()).getScene().getWindow();
            scene = FXMLLoader.load(getClass().getResource("/Views/MainForm.fxml"));
            stage.setTitle("Inventory Management");
            stage.setScene(new Scene(scene));
            stage.show();
        }
    }

    /**
     * Search Associated Part In Modify View.
     *
     * @param actionEvent
     */
    @FXML
    void modifyProductPartSearch(ActionEvent actionEvent) {
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

            modifypartsOfProductTable.setItems(partSearch);

        } catch (Exception e) {
            modifypartsOfProductTable.setItems(null);
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Search Error");
            alert.setContentText("No Matching Results found.");
            alert.showAndWait();
        }
    }

    /**
     * Set VAlues of Modify View Associated parts tables
     *
     * @param url
     * @param resourceBundle
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        modifypartsOfProductTable.setItems(Inventory.getAllPart());
        partID.setCellValueFactory(new PropertyValueFactory<>("id"));
        partName.setCellValueFactory(new PropertyValueFactory<>("name"));
        partsInventoryLevel.setCellValueFactory(new PropertyValueFactory<>("stock"));
        partsPrice.setCellValueFactory(new PropertyValueFactory<>("price"));

        modifyAssociatedPartsTable.setItems(Inventory.getAllAssociatedParts());
        partID1.setCellValueFactory(new PropertyValueFactory<>("associatedId"));
        partName1.setCellValueFactory(new PropertyValueFactory<>("associatedname"));
        partsInventoryLevel1.setCellValueFactory(new PropertyValueFactory<>("associatedstock"));
        partsPrice1.setCellValueFactory(new PropertyValueFactory<>("associatedprice"));


    }

    public void productModifyPopulate(String id) {
        var sqlRead = "SELECT * From " + Product_Table + " WHERE " + Product_Id + " = ?";

        try {
            var conn = DriverManager.getConnection(url);
            var pstmt = conn.prepareStatement(sqlRead);
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                modifyProductName.setText(rs.getString(Product_Name));
                modifyProductInv.setText(rs.getString(Product_Stock));
                modifyProductPrice.setText(rs.getString(Product_Price));
                modifyProductMax.setText(rs.getString(Product_Max));
                modifyProductMin.setText(rs.getString(Product_Min));

            }
            conn.close();
        } catch (SQLException e) {

            throw new RuntimeException(e);
        }


    }

    public void modifyTableRePopulate() {

        productModifyPopulate(String.valueOf(id2));
        modifyAssociatedPartsTable.setItems(Inventory.getAllAssociatedParts());
        partID1.setCellValueFactory(new PropertyValueFactory<>("associatedId"));
        partName1.setCellValueFactory(new PropertyValueFactory<>("associatedname"));
        partsInventoryLevel1.setCellValueFactory(new PropertyValueFactory<>("associatedstock"));
        partsPrice1.setCellValueFactory(new PropertyValueFactory<>("associatedprice"));
    }

    public void toAssociatedPartTable(int associatedPartId, String partNameSet, double partsPriceSet, int partStock, int partMin, int partMax) {
        AssociatedPart associatedPart = new AssociatedPart(0, 0, "", 0, 0, 0, 0);
        ArrayList<AssociatedPart> loop = new ArrayList<>(modifyAssociatedPartsTable.getItems());
//        numbersCheck.add(part2.getAssociatedId());
        for (AssociatedPart num : loop) {
            if (num.getAssociatedId() == associatedPartId) {
                associatedPartId++;
            }
        }
        associatedPart.setAssociatedId(associatedPartId);
        associatedPart.setAssociatedname(partNameSet);
        associatedPart.setAssociatedprice(partsPriceSet);
        associatedPart.setAssociatedstock(partStock);
        associatedPart.setAssociatedmin(partMin);
        associatedPart.setAssociatedmax(partMax);
        associatedPart.setPartProductId(id2);
        modifyAssociatedPartsTable.getItems().add(associatedPart);
    }

}
