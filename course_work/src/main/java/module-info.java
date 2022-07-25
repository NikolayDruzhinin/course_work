module com.example.course_work {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.logging;


    opens mvc to javafx.fxml;
    exports mvc;
}