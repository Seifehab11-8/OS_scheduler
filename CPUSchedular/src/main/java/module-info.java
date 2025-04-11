module org.example.cpuschedular {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.cpuschedular to javafx.fxml;
    exports org.example.cpuschedular;
}