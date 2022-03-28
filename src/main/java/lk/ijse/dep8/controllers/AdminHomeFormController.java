package lk.ijse.dep8.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminHomeFormController {
    public Label lblGreeting;
    public Button btnRecordAttendance;
    public Button btnUserProfile;
    public Button btnViewReports;
    public Button btnManageUsers;
    public Button btnBackup;
    public Button btnLogOut;
    public AnchorPane root;

    public void initialize(){
        root.setOnKeyReleased(event -> {
            switch (event.getCode()){
                case F1:
                    btnRecordAttendance.fire();
                    break;
                case F12:
                    btnLogOut.fire();
                    break;
            }
        });


//        lblGreeting.setText("welcome" + SecurityContextHolder.getPrinciple().getUsername() + "!");

    }
    public void initGreeting(String string){
        lblGreeting.setText(string);
    }


    public void btnRecordAttendanceClickOnAction(ActionEvent event) throws IOException {
        AnchorPane pane = FXMLLoader.load(this.getClass().getResource("/view/recordAttendance.fxml"));
        Scene scene = new Scene(pane);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("Student Attendance : Record Attendance");
        stage.initOwner(btnRecordAttendance.getScene().getWindow());
        stage.show();
        Platform.runLater(() -> {
            stage.sizeToScene();
            stage.centerOnScreen();
        });
    }

    public void btnUserProfileClickOnAction(ActionEvent event) {
    }

    public void btnViewReportsClickOnAction(ActionEvent event) throws IOException {
        AnchorPane pane = FXMLLoader.load(this.getClass().getResource("/view/viewReports.fxml"));
        Scene scene = new Scene(pane);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Student Attendance System : View Reports");
        stage.setResizable(false);
        stage.show();

        Platform.runLater(()->{
            stage.sizeToScene();
            stage.centerOnScreen();
        });
    }

    public void btnManageUsersClickOnAction(ActionEvent event) {

    }

    public void btnBackupClickOnAction(ActionEvent event) throws IOException {
        AnchorPane root = FXMLLoader.load(this.getClass().getResource("/view/backupAndRestoreForm.fxml"));
        Scene loginScene = new Scene(root);
        Stage stage = new Stage();
        stage.setScene(loginScene);
        stage.setTitle("Student Attendance System: Backup and Restore");
        stage.setResizable(false);
        stage.show();

        Platform.runLater(()->{
            stage.sizeToScene();
            stage.centerOnScreen();
        });
    }

    public void btnLogOutClickOnAction(ActionEvent event) throws IOException {
//        SecurityContextHolder.clear();
        AnchorPane root = FXMLLoader.load(this.getClass().getResource("/view/loggingForm.fxml"));
        Scene loginScene = new Scene(root);
        Stage stage = new Stage();
        stage.setScene(loginScene);
        stage.setTitle("Student Attendance System: Log In");
        stage.setResizable(false);
        stage.show();

        Platform.runLater(()->{
            stage.sizeToScene();
            stage.centerOnScreen();
        });

        ((Stage)(btnLogOut.getScene().getWindow())).close();
    }
}
