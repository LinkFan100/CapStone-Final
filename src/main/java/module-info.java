module com.example.capstone {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.xerial.sqlitejdbc;




    exports com.example.capstone.Main;
    opens com.example.capstone.Main to javafx.fxml;
    exports com.example.capstone.Controllers;
    opens com.example.capstone.Controllers to javafx.fxml;
    opens com.example.capstone.Model to javafx.base;


}
