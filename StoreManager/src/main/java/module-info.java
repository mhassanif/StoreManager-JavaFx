module com.mhassanif.storemanager {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.microsoft.sqlserver.jdbc;

    opens com.storemanager to javafx.fxml;
    exports com.storemanager;
    exports com.storemanager.controlers;
    opens com.storemanager.controlers to javafx.fxml;
    opens com.storemanager.model.order to javafx.base;
    opens com.storemanager.model.items to javafx.base;

}