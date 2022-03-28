package lk.ijse.dep8.controllers;

import lk.ijse.dep8.db.DBConnection;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import lk.ijse.dep8.db.DBConnection;
import lk.ijse.dep8.sequrity.Principle;
import lk.ijse.dep8.sequrity.SecurityContextHolder;
import lk.ijse.dep8.sequrity.Principle;
import lk.ijse.dep8.sequrity.SecurityContextHolder;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoggingFormController {
    public TextField txtUserName;
    public TextField txtPassword;
    public Label lblResult;
    public Button btnSignIn;

    public void btnSignInClickOnAction(ActionEvent event) {

        if (!isValidated()){
            new Alert(Alert.AlertType.ERROR,"Invalid username or password");
            txtUserName.selectAll();
            txtUserName.requestFocus();
            return;

        }
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            PreparedStatement stm = connection.prepareStatement("SELECT name , role FROM dep8_student_attendance.user WHERE username=? AND password=?");
            stm.setString(1,txtUserName.getText());
            stm.setString(2,txtPassword.getText());
            ResultSet resultSet = stm.executeQuery();

            if (!resultSet.next()){
                new Alert(Alert.AlertType.ERROR,"Invalid username and password").show();
                txtUserName.selectAll();
                txtUserName.requestFocus();
            }
            else {

                if(resultSet.getString("role").equals("ADMIN")){
                    FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("/view/AdminHomeForm.fxml"));
                    AnchorPane pane = fxmlLoader.load();
                    Scene scene = new Scene(pane);
                    AdminHomeFormController controller = fxmlLoader.getController();
                    controller.initGreeting(resultSet.getString("name"));
                    Stage primaryStage = (Stage) (btnSignIn.getScene().getWindow());
                    primaryStage.setScene(scene);
                    primaryStage.setResizable(false);

                    primaryStage.setTitle("Student Attendance system : Admin Home");
                    Platform.runLater(() -> {
                        primaryStage.centerOnScreen();
                        primaryStage.sizeToScene();
                    });
                    SecurityContextHolder.setPrinciple(new Principle(
                            txtUserName.getText(),
                            resultSet.getString("name"),
                            Principle.UserRole.valueOf(resultSet.getString("role"))));
                }
                else {
                    FXMLLoader fxmlLoader = new FXMLLoader(this.getClass().getResource("/view/UserHomeForm.fxml"));
                    AnchorPane pane = fxmlLoader.load();
                    Scene scene = new Scene(pane);
                    UserHomeFormController controller = fxmlLoader.getController();
                    controller.initGreeting(resultSet.getString("name"));
                    Stage primaryStage = (Stage) (btnSignIn.getScene().getWindow());
                    primaryStage.setScene(scene);
                    primaryStage.setResizable(false);
                    primaryStage.setTitle("Student Attendance system : User Home");

                    Platform.runLater(() -> {
                        primaryStage.centerOnScreen();
                        primaryStage.sizeToScene();
                    });
                    SecurityContextHolder.setPrinciple(new Principle(
                            txtUserName.getText(),
                            resultSet.getString("name"),
                            Principle.UserRole.valueOf(resultSet.getString("role"))));
                }
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
    private boolean isValidated(){
        return (txtUserName.getText().length() < 4 || !txtUserName.getText().matches("\\b[A-Za-z0-9]*\\b") || txtPassword.getText().trim().length() < 6
                || !txtPassword.getText().trim().matches("\\b[A-Za-z0-9 ]*\\b"));

        }

    }

