package com.example.capstone.Controllers;

import com.example.capstone.Database.InventoryDatabase;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.ResourceBundle;

public class partsModifyFormController implements Initializable {

    //  database Name
    private static final String DB_NAME = "Inventory";
    private static final String url = "jdbc:sqlite:" + DB_NAME + ".db";
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
    private static int id;
    InventoryDatabase inventoryDatabase = new InventoryDatabase();
    String outIn = "";
    Parent scene;
    Stage stage;
    String oldInOut;
    @FXML
    private TextField modifyId;
    @FXML
    private TextField modifyName;
    @FXML
    private TextField modifyInv;
    @FXML
    private TextField modifyPrice;
    @FXML
    private TextField modifyMax;
    @FXML
    private TextField modifyMin;
    @FXML
    private TextField modifyMachineId;
    @FXML
    private Text ModifyMachineIdLabel;
    @FXML
    private RadioButton modifyiHRadioButton;
    @FXML
    private RadioButton modifyOutRadioButton;

    /**
     * Gets selected Part data to modify from part object into parts Modify View.
     *
     * @param partId
     */
    public void modifySelectedData(int partId) {
        id = partId;
        partModifyPopulate(String.valueOf(id));
        try {
            oldInOut = oldOutIn(String.valueOf(id));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


    }

    /**
     * Change Machine Id Label name to "Machine Id"
     *
     * @param actionEvent
     */
    @FXML
    void modifyiHRadioButtonToggle(ActionEvent actionEvent) {
        ModifyMachineIdLabel.setText("Machine ID");

    }

    /**
     * Change Machine Id Label name to "Company Name"
     *
     * @param actionEvent
     */
    @FXML
    void modifyOutRadioToggle(ActionEvent actionEvent) {
        ModifyMachineIdLabel.setText("Company Name");
    }

    /**
     * Exits Modify Parts View Returns to Main View
     *
     * @param actionEvent
     * @throws IOException
     */
    @FXML
    void modifycancelAddPartClick(ActionEvent actionEvent) throws IOException {
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
     * Save modification of part and updates parts Table with changes
     * Index out of bounds error
     * Fixed by changes values in update part
     *
     * @param actionEvent
     * @throws IOException
     */
    @FXML
    void modifyaddPartSaveClick(ActionEvent actionEvent) throws IOException {

        try {
            Boolean flag = false;

            int partId = id;
            String partName = modifyName.getText();
            int partInv = Integer.parseInt(modifyInv.getText());
            double partPrice = Double.parseDouble(modifyPrice.getText());
            int partMin = Integer.parseInt(modifyMin.getText());
            int partMax = Integer.parseInt(modifyMax.getText());


            for (int a = 0; a < partName.length(); a++) {
                if (a == 0 && partName.charAt(a) == '-')
                    continue;
                if (!Character.isDigit(partName.charAt(a)))
                    flag = false;


                if (Character.isDigit(partName.charAt(a))) {
                    flag = true;
                }
            }
            if (flag == true) {
                Alert nameNumAlert = new Alert(Alert.AlertType.WARNING);
                nameNumAlert.setTitle("Critical Name Error");
                nameNumAlert.setContentText("Name field contains numbers");
                nameNumAlert.showAndWait();
            } else if (partMin > partMax) {
                Alert minMaxAlert = new Alert(Alert.AlertType.WARNING);
                minMaxAlert.setTitle("Critical Min/Max Error");
                minMaxAlert.setContentText("Error: The Min is Greater then the Max.");
                minMaxAlert.showAndWait();
            } else if (partInv > partMax || partMin > partInv) {
                Alert inventoryAlert = new Alert(Alert.AlertType.WARNING);
                inventoryAlert.setTitle("Critical Inventory Error");
                inventoryAlert.setContentText("Inventory input is not within the Min/Max Parameters");
                inventoryAlert.showAndWait();

            } else if (partName == null) {
                Alert nameAlert = new Alert(Alert.AlertType.WARNING);
                nameAlert.setTitle("Critical Name Error");
                nameAlert.setContentText("Name field is empty please input a name");
                nameAlert.showAndWait();

            } else {
                //In-House radio button is selected
                if (modifyiHRadioButton.isSelected()) {
                    int machineID = Integer.parseInt(modifyMachineId.getText());
                    inventoryDatabase.partUpdate(partName, String.valueOf(partPrice), String.valueOf(partInv), String.valueOf(partMin), String.valueOf(partMax), "IH", String.valueOf(partId));
                    if (oldInOut.equals("OS")) {
                        inventoryDatabase.outsourcePartDelete(String.valueOf(partId));
                        inventoryDatabase.inHousePartAdd(String.valueOf(partId), String.valueOf(machineID));
                    } else
                        inventoryDatabase.inHousePartUpdate(String.valueOf(machineID), String.valueOf(partId));
                } else if (modifyOutRadioButton.isSelected()) {
                    String companyName = modifyMachineId.getText();
                    inventoryDatabase.partUpdate(partName, String.valueOf(partPrice), String.valueOf(partInv), String.valueOf(partMin), String.valueOf(partMax), "OS", String.valueOf(partId));
                    if (oldInOut.equals("IH")) {
                        inventoryDatabase.outsourcePartAdd(String.valueOf(partId), String.valueOf(companyName));
                        inventoryDatabase.inHousePartDelete(String.valueOf(partId));
                    } else
                        inventoryDatabase.outSourcedPartUpdate(companyName, String.valueOf(partId));
                }
                boolean a = true;
                signUpController.SaveFile("\nModify Part attempt on: ", a);
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

    public void partModifyPopulate(String id) {
        var sqlRead = "SELECT * From " + Part_Table + " WHERE " + Part_Id + " = ?";

        try {
            var conn = DriverManager.getConnection(url);
            var pstmt = conn.prepareStatement(sqlRead);
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                modifyName.setText(rs.getString(Part_Name));
                modifyInv.setText(rs.getString(Part_Stock));
                modifyPrice.setText(rs.getString(Part_Price));
                modifyMax.setText(rs.getString(Part_Max));
                modifyMin.setText(rs.getString(Part_Min));
                outIn = rs.getString(OutsourcedInhouse);
//                modifyAssociatedPartsTable.setItems(newProduct.getAllAssociatedPart());

            }
            //Get In-House or Outsourced radio button
            if (outIn.equals("IH")) {
                modifyiHRadioButton.setSelected(true);
                ModifyMachineIdLabel.setText("Machine ID");
                String machineId = inventoryDatabase.getMachineID(id);
                modifyMachineId.setText(machineId);
            } else {
                modifyOutRadioButton.setSelected(true);
                ModifyMachineIdLabel.setText("Company Name");
                String companyName = inventoryDatabase.getCompanyName(id);
                modifyMachineId.setText(companyName);
            }
            conn.close();
        } catch (SQLException e) {

            throw new RuntimeException(e);
        }


    }

    public String oldOutIn(String id) throws SQLException {
        var sqlRead = "SELECT * From " + Part_Table + " WHERE " + Part_Id + " = ?";
        String inOut = "";
        try {
            var conn = DriverManager.getConnection(url);
            var pstmt = conn.prepareStatement(sqlRead);
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                inOut = rs.getString(OutsourcedInhouse);
            }
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return inOut;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}