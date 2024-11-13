package com.example.capstone.Database;



import com.example.capstone.Model.AssociatedPart;
import com.example.capstone.Model.Inventory;
import com.example.capstone.Model.Part;
import com.example.capstone.Model.Product;
import javafx.scene.control.Alert;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Set;

/**
 * The InventoryDatabase class handles the database operations related to the inventory system.
 * It includes methods for creating and managing product, part, and user data.
 */
public class InventoryDatabase {


    //  database Name
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
    private static final String Logged_User_Pass = "loggedPass";

    // Associated Parts Table
    private static final String Associated_Parts_Table = "associatedParts";
    private static final String Associated_Part_Id = "associatedpartId";
    private static final String Associated_Part_Name = "part";
    private static final String Associated_Part_Price = "price";
    private static final String Associated_Part_Stock = "stock";
    private static final String Associated_Part_Min = "min";
    private static final String Associated_Part_Max = "max";
    private static final String Associated_Part_Product_Id = "partProductId";

    public void dbCreate() {

        try (var conn = DriverManager.getConnection(url)) {
            if (conn != null) {
                var meta = conn.getMetaData();
                conn.close();
            }
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    public void tableCreate() {
        var sql1 = "CREATE TABLE IF NOT EXISTS " + Part_Table + " ("
                + Part_Id + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Part_Name + " TEXT,"
                + Part_Price + " TEXT,"
                + Part_Stock + " TEXT,"
                + Part_Min + " TEXT,"
                + Part_Max + " TEXT,"
                + Part_Product_Id + " TEXT,"
                + OutsourcedInhouse + " TEXT)";

        var sql2 = "CREATE TABLE IF NOT EXISTS " + Product_Table + " ("
                + Product_Id + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Product_Name + " TEXT,"
                + Product_Price + " TEXT,"
                + Product_Stock + " TEXT,"
                + Product_Min + " TEXT,"
                + Product_Max + " TEXT)";

        var sql3 = "CREATE TABLE IF NOT EXISTS " + Inhouse_Part_Table + " ("
                + Inhouse_Part_Id + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Machine_Id + " TEXT)";

        var sql4 = "CREATE TABLE IF NOT EXISTS " + Outsourced_Part_Table + " ("
                + Outsourced_Part_Id + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Company_Name + " TEXT)";

        var sql5 = "CREATE TABLE IF NOT EXISTS " + Logged_User_Table + " ("
                + Logged_User_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Logged_User_Name + " TEXT,"
                + Logged_User_Pass + " TEXT)";

        var sql6 = "CREATE TABLE IF NOT EXISTS " + Associated_Parts_Table + " ("
                + Associated_Part_Id + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Associated_Part_Name + " TEXT,"
                + Associated_Part_Price + " TEXT,"
                + Associated_Part_Stock + " TEXT,"
                + Associated_Part_Min + " TEXT,"
                + Associated_Part_Max + " TEXT,"
                + Associated_Part_Product_Id + " TEXT,"
                + Part_Id + " TEXT)";


        try {
            var conn = DriverManager.getConnection(url);
            var stmt = conn.createStatement();
            stmt.execute(sql1);
            stmt.execute(sql2);
            stmt.execute(sql3);
            stmt.execute(sql4);
            stmt.execute(sql5);
            stmt.execute(sql6);
            conn.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }

    }

    // Add To Product Database Methods
    public void productAdd(String n, String p, String s, String mn, String mx) {
        String sqlProductAdd = "Insert INTO " + Product_Table + "(" + Product_Name + "," + Product_Price + "," +
                Product_Stock + "," + Product_Min + "," + Product_Max + ")" + " Values(?,?,?,?,?)";

        try {
            var conn = DriverManager.getConnection(url);
            var pstmt = conn.prepareStatement(sqlProductAdd);
            pstmt.setString(1, n);
            pstmt.setString(2, p);
            pstmt.setString(3, s);
            pstmt.setString(4, mn);
            pstmt.setString(5, mx);
            pstmt.executeUpdate();
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean productAddCheck(String name) throws SQLException {

        boolean matchedName;
        var sqlRead = "SELECT * From " + Product_Table + " WHERE " + Product_Name + " = ?";
        var conn = DriverManager.getConnection(url);
        var pstmt = conn.prepareStatement(sqlRead);
        pstmt.setString(1, name);
        var rs = pstmt.executeQuery();

        if (!rs.next()) {
            matchedName = false;
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Incorrect Username/Password.");
            alert.setContentText("Incorrect User Name or Password, Try Again.");
            alert.showAndWait();
        } else {
            matchedName = true;
        }
        conn.close();
        return matchedName;
    }

    // Update Product Database Methods
    public void productUpdate(String n, String p, String s, String mn, String mx, String id) {
        String sqlProductAdd = "UPDATE " + Product_Table + " SET " + Product_Name + " = ?" + "," + Product_Price + " = ?" + "," +
                Product_Stock + " = ?" + "," + Product_Min + " = ?" + "," + Product_Max + " = ? " + " WHERE " + Product_Id + " = ?";

        try {
            var conn = DriverManager.getConnection(url);
            var pstmt = conn.prepareStatement(sqlProductAdd);
            pstmt.setString(1, n);
            pstmt.setString(2, p);
            pstmt.setString(3, s);
            pstmt.setString(4, mn);
            pstmt.setString(5, mx);
            pstmt.setString(6, id);
            pstmt.executeUpdate();
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    // Delete From Product Database Methods
    public void productDelete(String id) {
        String sqlProductAdd = "DELETE FROM " + Product_Table + " WHERE " + Product_Id + " = ?";

        try {
            var conn = DriverManager.getConnection(url);
            var pstmt = conn.prepareStatement(sqlProductAdd);
            pstmt.setString(1, id);
            pstmt.executeUpdate();
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    // Get Product ID from Database
    public String getProductId(String productName) throws SQLException {
        String id = "";
        var sqlSelect = "Select " + Product_Id + " FROM " + Product_Table + " WHERE " + Product_Name + " = ?";
        var conn = DriverManager.getConnection(url);
        var pstmt = conn.prepareStatement(sqlSelect);
        pstmt.setString(1, productName);
        var rs = pstmt.executeQuery();
        if (rs.next()) {
            id = rs.getString(Product_Id);
        }
        conn.close();
        return id;
    }

    /**
     * Populates the product table by reading all records from the Product_Table in the database.
     * It retrieves product data such as ID, name, price, stock, min, and max values,
     * then creates new Product objects and adds them to the Inventory.
     *
     * The method handles SQL exceptions by throwing a RuntimeException.
     */

    public void productTablePopulate() {
        var sqlRead = "SELECT * From " + Product_Table;

        try {
            var conn = DriverManager.getConnection(url);
            var stmt = conn.createStatement();
            var rs = stmt.executeQuery(sqlRead);
            while (rs.next()) {
                Product newProduct = new Product(0, "", 0, 0, 0, 0);
                newProduct.setId(Integer.parseInt(rs.getString(Product_Id)));
                newProduct.setName(rs.getString(Product_Name));
                newProduct.setPrice(Double.parseDouble(rs.getString(Product_Price)));
                newProduct.setStock(Integer.parseInt(rs.getString(Product_Stock)));
                newProduct.setMin(Integer.parseInt(rs.getString(Product_Min)));
                newProduct.setMax(Integer.parseInt(rs.getString(Product_Max)));
                Inventory.addProduct(newProduct);

            }
            conn.close();
        } catch (SQLException e) {

            throw new RuntimeException(e);
        }


    }

    // Checks for associated parts for product
    public boolean productAssociatedPartCheck(String id) throws SQLException {
        boolean found = false;
        var sqlCheck = "Select * From " + Part_Table + " Where " + Part_Product_Id + " = ?";
        var conn = DriverManager.getConnection(url);
        var pstmt = conn.prepareStatement(sqlCheck);
        pstmt.setString(1, id);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            found = true;
        }
        return found;
    }

    // Parts Database Add Method
    public void addPart(String n, String p, String s, String mn, String mx, String oi) {
        var sqlAdd = "INSERT INTO " + Part_Table + "(" + Part_Name + "," + Part_Price + "," + Part_Stock + "," + Part_Min + "," + Part_Max + "," + OutsourcedInhouse + ")" + " Values(?,?,?,?,?,?)";
        try {
            var conn = DriverManager.getConnection(url);
            var pstmt = conn.prepareStatement(sqlAdd);
            pstmt.setString(1, n);
            pstmt.setString(2, p);
            pstmt.setString(3, s);
            pstmt.setString(4, mn);
            pstmt.setString(5, mx);
            pstmt.setString(6, oi);
            pstmt.executeUpdate();
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Update Part Database
    public void partUpdate(String n, String p, String s, String mn, String mx, String oi, String id) {
        String sqlProductAdd = "UPDATE " + Part_Table + " SET " + Part_Name + " = ?" + "," + Part_Price + " = ?" + "," +
                Part_Stock + " = ?" + "," + Part_Min + " = ?" + "," + Part_Max + " = ?" + "," + OutsourcedInhouse + " = ?" + " WHERE " + Part_Id + " = ?";

        try {
            var conn = DriverManager.getConnection(url);
            var pstmt = conn.prepareStatement(sqlProductAdd);
            pstmt.setString(1, n);
            pstmt.setString(2, p);
            pstmt.setString(3, s);
            pstmt.setString(4, mn);
            pstmt.setString(5, mx);
            pstmt.setString(6, oi);
            pstmt.setString(7, id);
            pstmt.executeUpdate();
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void partUpdateProductId(String pid, String id) {
        String sqlProductAdd = "UPDATE " + Part_Table + " SET " + Part_Product_Id + " = ?" + " WHERE " + Part_Id + " = ?";

        try {
            var conn = DriverManager.getConnection(url);
            var pstmt = conn.prepareStatement(sqlProductAdd);
            pstmt.setString(1, pid);
            pstmt.setString(2, id);
            pstmt.executeUpdate();
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    // Delete From Part Database
    public void partDelete(String id) {
        String sqlProductAdd = "DELETE FROM " + Part_Table + " WHERE " + Part_Id + " = ?";

        try {
            var conn = DriverManager.getConnection(url);
            var pstmt = conn.prepareStatement(sqlProductAdd);
            pstmt.setString(1, id);
            pstmt.executeUpdate();
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    // OutSource Part Add
    public void outsourcePartAdd(String id, String cName) throws SQLException {

        var sqlInsert = "Insert Into " + Outsourced_Part_Table + "(" + Outsourced_Part_Id + "," + Company_Name + ")" + " Values(?,?)";
        var conn = DriverManager.getConnection(url);
        var pstmt = conn.prepareStatement(sqlInsert);
        pstmt.setString(1, id);
        pstmt.setString(2, cName);
        pstmt.executeUpdate();
        conn.close();
    }

    // OutSource Part update
    public void outSourcedPartUpdate(String cN, String pId) {
        String sqlOutPartUdt = "UPDATE " + Outsourced_Part_Table + " SET " + Company_Name + " = ?" + " WHERE " + Outsourced_Part_Id + " = ?";

        try {
            var conn = DriverManager.getConnection(url);
            var pstmt = conn.prepareStatement(sqlOutPartUdt);
            pstmt.setString(1, cN);
            pstmt.setString(2, pId);

            pstmt.executeUpdate();
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Delete From Part Database
    public void outsourcePartDelete(String id) {
        String sqlDelete = "DELETE FROM " + Outsourced_Part_Table + " WHERE " + Outsourced_Part_Id + " = ?";

        try {
            var conn = DriverManager.getConnection(url);
            var pstmt = conn.prepareStatement(sqlDelete);
            pstmt.setString(1, id);
            pstmt.executeUpdate();
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    // Inhouse Part Add
    public void inHousePartAdd(String id, String mId) throws SQLException {

        var sqlInsert = "Insert Into " + Inhouse_Part_Table + "(" + Inhouse_Part_Id + "," + Machine_Id + ")" + " Values(?,?)";
        var conn = DriverManager.getConnection(url);
        var pstmt = conn.prepareStatement(sqlInsert);
        pstmt.setString(1, id);
        pstmt.setString(2, mId);
        pstmt.executeUpdate();
        conn.close();
    }

    // Inhouse Part update
    public void inHousePartUpdate(String mId, String pId) {
        String sqlOutPartUdt = "UPDATE " + Inhouse_Part_Table + " SET " + Machine_Id + " = ?" + " WHERE " + Inhouse_Part_Id + " = ?";

        try {
            var conn = DriverManager.getConnection(url);
            var pstmt = conn.prepareStatement(sqlOutPartUdt);
            pstmt.setString(1, mId);
            pstmt.setString(2, pId);

            pstmt.executeUpdate();
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // Delete From Part Database
    public void inHousePartDelete(String id) {
        String sqlProductAdd = "DELETE FROM " + Inhouse_Part_Table + " WHERE " + Inhouse_Part_Id + " = ?";

        try {
            var conn = DriverManager.getConnection(url);
            var pstmt = conn.prepareStatement(sqlProductAdd);
            pstmt.setString(1, id);
            pstmt.executeUpdate();
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    // Part Table View Populate Methods
    public void partTablePopulate() {
        var sqlRead = "SELECT * From " + Part_Table;

        try {
            var conn = DriverManager.getConnection(url);
            var stmt = conn.createStatement();
            var rs = stmt.executeQuery(sqlRead);
            while (rs.next()) {
                Part newPart = new Part(0, "", 0, 0, 0, 0) {
                };
                newPart.setId(Integer.parseInt(rs.getString(Part_Id)));
                newPart.setName(rs.getString(Part_Name));
                newPart.setPrice(Double.parseDouble(rs.getString(Part_Price)));
                newPart.setStock(Integer.parseInt(rs.getString(Part_Stock)));
                newPart.setMin(Integer.parseInt(rs.getString(Part_Min)));
                newPart.setMax(Integer.parseInt(rs.getString(Part_Max)));
                Inventory.addPart(newPart);

            }
            conn.close();
        } catch (SQLException e) {

            throw new RuntimeException(e);
        }


    }

    // Get Part ID
    public int getPartID(String name) throws SQLException {
        int id = 0;
        var sqlSelect = "Select " + Part_Id + " FROM " + Part_Table + " WHERE " + Part_Name + " = ?";
        var conn = DriverManager.getConnection(url);
        var pstmt = conn.prepareStatement(sqlSelect);
        pstmt.setString(1, name);
        var rs = pstmt.executeQuery();
        if (rs.next()) {
            id = Integer.parseInt(rs.getString(Part_Id));
        }
        conn.close();
        return id;
    }

    // Get Inhouse MachineId
    public String getMachineID(String name) throws SQLException {
        String machineId = "";
        var sqlSelect = "Select " + Machine_Id + " FROM " + Inhouse_Part_Table + " WHERE " + Inhouse_Part_Id + " = ?";
        var conn = DriverManager.getConnection(url);
        var pstmt = conn.prepareStatement(sqlSelect);
        pstmt.setString(1, name);
        var rs = pstmt.executeQuery();
        if (rs.next()) {
            machineId = rs.getString(Machine_Id);
        }
        conn.close();
        return machineId;
    }

    // Get OutSourced Company Name
    public String getCompanyName(String name) throws SQLException {
        String companyName = "";
        var sqlSelect = "Select " + Company_Name + " FROM " + Outsourced_Part_Table + " WHERE " + Outsourced_Part_Id + " = ?";
        var conn = DriverManager.getConnection(url);
        var pstmt = conn.prepareStatement(sqlSelect);
        pstmt.setString(1, name);
        var rs = pstmt.executeQuery();
        if (rs.next()) {
            companyName = rs.getString(Company_Name);
        }
        conn.close();
        return companyName;
    }

    /*** Associated Parts Code ***/
    public void addAssociatedPart(String n, String p, String s, String mn, String mx, String ppi, String partid) {
        var sqlAdd = "INSERT INTO " + Associated_Parts_Table + "(" + Associated_Part_Name + "," + Associated_Part_Price + "," + Associated_Part_Stock + "," + Associated_Part_Min + "," + Associated_Part_Max + "," + Associated_Part_Product_Id + "," + Part_Id + ")" + " Values(?,?,?,?,?,?,?)";
        try {
            var conn = DriverManager.getConnection(url);
            var pstmt = conn.prepareStatement(sqlAdd);
            pstmt.setString(1, n);
            pstmt.setString(2, p);
            pstmt.setString(3, s);
            pstmt.setString(4, mn);
            pstmt.setString(5, mx);
            pstmt.setString(6, ppi);
            pstmt.setString(7, partid);

            pstmt.executeUpdate();
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void modifyAddAssociatedPart(String id, String ppi) throws SQLException {
        var sqlSelect = "Select * From " + Part_Table + " WHERE " + Part_Id + " = ?";
        var sqlAdd = "INSERT INTO " + Associated_Parts_Table + "(" + Associated_Part_Name + "," + Associated_Part_Price + "," + Associated_Part_Stock + "," + Associated_Part_Min + "," + Associated_Part_Max + "," + Associated_Part_Product_Id + "," + Part_Id + ")" + " Values(?,?,?,?,?,?,?)";
        var conn = DriverManager.getConnection(url);
        var pstmt = conn.prepareStatement(sqlSelect);
        pstmt.setString(1, id);
        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            String name = rs.getString(Part_Name);
            String price = rs.getString(Part_Price);
            String stock = rs.getString(Part_Stock);
            String min = rs.getString(Part_Min);
            String max = rs.getString(Part_Max);
            var pstmt2 = conn.prepareStatement(sqlAdd);
            pstmt2.setString(1, name);
            pstmt2.setString(2, price);
            pstmt2.setString(3, stock);
            pstmt2.setString(4, min);
            pstmt2.setString(5, max);
            pstmt2.setString(6, ppi);
            pstmt2.setString(7, id);

            pstmt2.executeUpdate();

        }
        conn.close();
    }

    /**
     * Delete Associated Parts
     *
     * @param numbers
     */
    public void associatedPartDelete(ArrayList<Integer> numbers) {
        String sqlProductAdd = "DELETE FROM " + Associated_Parts_Table + " WHERE " + Associated_Part_Id + " IN (?)";

        try {
            var conn = DriverManager.getConnection(url);
            var pstmt = conn.prepareStatement(sqlProductAdd);

            for (int num : numbers) {
                pstmt.setString(1, String.valueOf(num));
                pstmt.executeUpdate();

            }
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * Associated Parts Table View Populate Methods
     *
     * @param productId
     */
    public void associatedPartsTablePopulate(String productId) {
        var sqlRead = "SELECT * From " + Associated_Parts_Table + " Where " + Associated_Part_Product_Id + " = ?";

        try {
            var conn = DriverManager.getConnection(url);
            var pstmt = conn.prepareStatement(sqlRead);
            pstmt.setString(1, productId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                AssociatedPart associatedPart = new AssociatedPart(0, 0, "", 0, 0, 0, 0);
                associatedPart.setAssociatedId(Integer.parseInt(rs.getString(Associated_Part_Id)));
                associatedPart.setAssociatedname(rs.getString(Associated_Part_Name));
                associatedPart.setAssociatedprice(Double.parseDouble(rs.getString(Associated_Part_Price)));
                associatedPart.setAssociatedstock(Integer.parseInt(rs.getString(Associated_Part_Stock)));
                associatedPart.setAssociatedmin(Integer.parseInt(rs.getString(Associated_Part_Min)));
                associatedPart.setAssociatedmax(Integer.parseInt(rs.getString(Associated_Part_Max)));
                associatedPart.setPartProductId(Integer.parseInt(rs.getString(Associated_Part_Product_Id)));
                Inventory.addAssociatedParts(associatedPart);
            }
            conn.close();
        } catch (SQLException e) {

            throw new RuntimeException(e);
        }


    }

    public boolean associatedPartCheck(String id) throws SQLException {

        boolean matched;
        var sqlRead = "SELECT * From " + Associated_Parts_Table + " WHERE " + Associated_Part_Id + " = ?";
        var conn = DriverManager.getConnection(url);
        var pstmt = conn.prepareStatement(sqlRead);
        pstmt.setString(1, id);
        var rs = pstmt.executeQuery();

        if (!rs.next()) {
            matched = false;
        } else {
            matched = true;
        }
        conn.close();
        return matched;
    }
    /*** End of Associated Parts Code ***/


    /***User Log-In Code Block***/
    public void userCreate(String n, String p) {
        String sqlProductAdd = "Insert INTO " + Logged_User_Table + "(" + Logged_User_Name + "," + Logged_User_Pass + ")" + " Values(?,?)";

        try {
            var conn = DriverManager.getConnection(url);
            var pstmt = conn.prepareStatement(sqlProductAdd);
            pstmt.setString(1, n);
            pstmt.setString(2, p);
            pstmt.executeUpdate();
            conn.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }


    // Database Checks
    public boolean checkLoggedUserName(String s) throws SQLException {
        boolean found;
        var sqlRead = "SELECT * From " + Logged_User_Table + " WHERE " + Logged_User_Name + " = ?";
        var conn = DriverManager.getConnection(url);
        var pstmt = conn.prepareStatement(sqlRead);
//        var stmt = conn.createStatement();
        pstmt.setString(1, s);
        var rs = pstmt.executeQuery();
        if (rs.next()) {
            found = true;
        } else {
            found = false;
        }
        conn.close();
        return found;
    }

    /**
     * Checks if user_name and password matches in database
     *
     * @param U
     * @param P
     */
    public boolean UserNameCheck(String U, String P) throws SQLException {

        boolean matched;
        var sqlRead = "SELECT * From " + Logged_User_Table + " WHERE " + Logged_User_Name + " = ?" + " And " + Logged_User_Pass + " = ?";
        var conn = DriverManager.getConnection(url);
        var pstmt = conn.prepareStatement(sqlRead);
        pstmt.setString(1, U);
        pstmt.setString(2, P);
        var rs = pstmt.executeQuery();

        if (!rs.next()) {
            matched = false;
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Incorrect Username/Password.");
            alert.setContentText("Incorrect User Name or Password, Try Again.");
            alert.showAndWait();
        } else {
            matched = true;
        }
        conn.close();
        return matched;
    }

    /**
     * Gets user_id from database using user_name and password
     *
     * @param U1
     * @param P2
     */
    public boolean getUser_ID(String U1, String P2) throws SQLException {
        boolean idFound;
        var sqlRead = "SELECT " + Logged_User_ID + " From " + Logged_User_Table + " WHERE " + Logged_User_Name + " = ?" + " And " + Logged_User_Pass + " = ?";
        var conn = DriverManager.getConnection(url);
        var pstmt = conn.prepareStatement(sqlRead);
        pstmt.setString(1, U1);
        pstmt.setString(2, P2);
        var rs = pstmt.executeQuery();

        if (!rs.next()) {
            idFound = false;
        } else {
            idFound = true;
        }
        conn.close();
        return idFound;

    }


    /***End of User Log-In Code Block***/

    public boolean nameCheck(String tableName,String tableColumn, String name) throws SQLException {
        // Define whitelists for tables and columns
        Set<String> allowedTables = Set.of(Part_Table, Product_Table);
        Set<String> allowedColumns = Set.of(Part_Name, Product_Name);

        // Validate table and column names against the whitelist
        if (!allowedTables.contains(tableName) || !allowedColumns.contains(tableColumn)) {
            throw new IllegalArgumentException("Invalid table or column name provided");
        }

        boolean matched;
        var sqlRead = "SELECT * From " + tableName + " WHERE " + tableColumn + " = ?";
        var conn = DriverManager.getConnection(url);
        var pstmt = conn.prepareStatement(sqlRead);
        pstmt.setString(1, name);

        var rs = pstmt.executeQuery();
        if (rs.next()) {

            Alert existingName= new Alert(Alert.AlertType.WARNING);
            existingName.setTitle("Name Error");
            existingName.setContentText("Part Name Already Exists, Try Again.");
            existingName.showAndWait();
            matched = false;
        }
        else{
            matched=true;
        }
        conn.close();
        return matched;
    }
    public boolean nameCheckEdit(String tableName,String tableColumn, String name,String tableColumn2, String id) throws SQLException {
        // Define whitelists for tables and columns
        Set<String> allowedTables = Set.of(Part_Table, Product_Table);
        Set<String> allowedColumns = Set.of(Part_Name, Product_Name,Part_Id,Product_Id);

        // Validate table and column names against the whitelist
        if (!allowedTables.contains(tableName) || !allowedColumns.contains(tableColumn)||!allowedColumns.contains(tableColumn2)) {
            throw new IllegalArgumentException("Invalid table or column name provided");
        }
        boolean matched;
        var sqlRead = "SELECT * From " + tableName + " WHERE " + tableColumn + " = ?" + " AND " + tableColumn2 + " = ?";
        var conn = DriverManager.getConnection(url);
        var pstmt = conn.prepareStatement(sqlRead);
        pstmt.setString(1, name);
        pstmt.setString(2, id);
        var rs = pstmt.executeQuery();
        if (rs.next()) {
            matched = true;

        }
        else {
            matched=nameCheck(tableName,tableColumn,name);

        }
        conn.close();
        return matched;
    }

}
