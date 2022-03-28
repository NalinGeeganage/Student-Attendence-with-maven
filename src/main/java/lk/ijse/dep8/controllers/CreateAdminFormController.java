package lk.ijse.dep8.controllers;

import lk.ijse.dep8.db.DBConnection;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CreateAdminFormController {

    public TextField txtName;
    public TextField txtUserName;
    public PasswordField txtPassword;
    public PasswordField txtConfirmPassword;
    public Button btnCreateAccount;

    public Rectangle rect1;

    public void initialize() {
       handlePasswordStrength();
    }


        private void handlePasswordStrength() {
            rect1.setFill(Color.TRANSPARENT);


            txtPassword.textProperty().addListener((observable, oldValue, newValue) -> {
                rect1.setFill(Color.TRANSPARENT);

                if (!newValue.trim().isEmpty() && newValue.trim().length() < 4) {
                    rect1.setFill(Paint.valueOf("#ff1f1f")); // set rectangular color
                } else if (newValue.trim().length() == 4) {
                    rect1.setFill(Paint.valueOf("#ffef21"));

                } else if (newValue.trim().length() >= 4) {
                    rect1.setFill(Paint.valueOf("#21ff56"));

                }
            });
        }

    public void btnCreateAccount_OnAction(ActionEvent event) {
        if (!isValidate()){
            return;
        }
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            PreparedStatement stm = connection.
                    prepareStatement("INSERT INTO user (name,username,password,role) VALUES (?,?,?,?)");
            stm.setString(1,txtName.getText());
            stm.setString(2,txtUserName.getText());
            stm.setString(3,txtPassword.getText());
            stm.setString(4,"ADMIN");
            stm.executeUpdate();

            new Alert(Alert.AlertType.CONFIRMATION,"You have successfully create Admin account").showAndWait();

            AnchorPane pane = FXMLLoader.load(this.getClass().getResource("/view/loggingForm.fxml"));
            Scene scene = new Scene(pane);
            Stage primaryStage = new Stage();
            primaryStage.setScene(scene);
            primaryStage.setTitle("Student Attendance system : Log in");
            primaryStage.centerOnScreen();
            primaryStage.setResizable(false);
            primaryStage.show();
            Platform.runLater(primaryStage::sizeToScene);

            ((Stage)(btnCreateAccount.getScene().getWindow())).close();

        } catch (SQLException | IOException e) {
            new Alert(Alert.AlertType.ERROR,"Something went wrong", ButtonType.OK).show();
            e.printStackTrace();
        }


    }

    private boolean isValidate(){


        if (!txtName.getText().matches("\\b[A-Za-z ]+\\b")){
            new Alert(Alert.AlertType.ERROR,"Enter only characters").show();
            txtName.requestFocus();
            return false;
        }
        else if (txtUserName.getText().length() < 4){
            new Alert(Alert.AlertType.ERROR,"UserName Should have at least 4 letters").show();
            txtName.requestFocus();
            return false;
        }
        else if (!txtUserName.getText().matches("\\b[A-Za-z0-9]*\\b")){
            new Alert(Alert.AlertType.ERROR,"UserName Should have characters and numbers").show();
            txtUserName.requestFocus();
            return false;
        }
        else if (txtPassword.getText().length() < 4 ) {
            new Alert(Alert.AlertType.ERROR, "Your password should have at least 4 characters").show();
            txtPassword.requestFocus();
            return false;
        }
        else if (!txtPassword.getText().equals(txtConfirmPassword.getText())) {

            new Alert(Alert.AlertType.ERROR, "Mismatch combination").show();
            txtConfirmPassword.requestFocus();
            return false;
        }
        return true;

    }
}
