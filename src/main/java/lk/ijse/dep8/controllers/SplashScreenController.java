package lk.ijse.dep8.controllers;

import lk.ijse.dep8.db.DBConnection;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lk.ijse.dep8.db.DBConnection;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class SplashScreenController {
    public Label lblLoading;
    public ProgressBar pgbLoading;
    private final SimpleObjectProperty<File> fileProperty = new SimpleObjectProperty<>();     //To restore Backup file
    private final SimpleDoubleProperty progress = new SimpleDoubleProperty(0.0);
    public void initialize(){
        pgbLoading.progressProperty().bind(progress);
        establishDBConnection();
    }
    private void establishDBConnection(){
        lblLoading.setText("Establishing DB connection");
        progress.set(0.1);

        // Use new thread to run the database loading part
        new Thread(()->{
            try{
                sleep(500);
                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection connection = DriverManager.
                        getConnection("jdbc:mysql://localhost:3306/dep8_student_attendance", "root", "mysql");
                Platform.runLater(() -> lblLoading.setText("Setting up the UI"));
                sleep(500);

                Platform.runLater(() -> loadLoginForm(connection));

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e) {
               if (e.getSQLState().equals("42000")){  // Can't found database
                   Platform.runLater(this::loadImportDBForm);
               }else {
                   shutdownApp(e);
               }
            }
        }).start();
    }
    private void loadImportDBForm(){
        try {
            Stage stage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader
                     (this.getClass().getResource("/view/ImportDBConnection.fxml"));
            AnchorPane pane = fxmlLoader.load();
            ImportDBConnectionController controller = fxmlLoader.getController();
            controller.initFileProperty(fileProperty);

//            AnchorPane pane = FXMLLoader.load(this.getClass().getResource("/view/ImportDBConnection.fxml"));
            Scene scene = new Scene(pane);
            stage.setScene(scene);
            stage.sizeToScene();
            stage.setTitle("Student Attendance System: First time boot");
            stage.setResizable(false);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initOwner(lblLoading.getScene().getWindow());
            stage.centerOnScreen();
            stage.setOnCloseRequest(event -> {
                event.consume();
            });
            stage.showAndWait();
            System.out.println(fileProperty);
            if (fileProperty.getValue() == null){       // If we get value for file property then it should be restored
                lblLoading.setText("Creating a new DB");
                new Thread(()->{
                    try{
                        sleep(500);

                        Platform.runLater(() -> lblLoading.setText("Loading database"));

                        /*Read Db Script */
                        InputStream stream = this.getClass().
                                getResourceAsStream("/assets/db-script.sql");
                        byte[] buffer=new byte[stream.available()];
                        stream.read(buffer);
                        String script = new String(buffer);
                        /*Get connection*/
                        Connection connection = DriverManager.
                                getConnection("jdbc:mysql://localhost:3306?allowMultiQueries=true", "root", "mysql");
                        Platform.runLater(() -> lblLoading.setText("Execute database script"));
                        Statement stm = connection.createStatement();
                        stm.execute(script);
                        sleep(500);

                        Platform.runLater(() -> lblLoading.setText("Obtaining a new DB connection"));
                        connection= DriverManager.
                                getConnection("jdbc:mysql://localhost:3306/dep8_student_attendance","root","mysql");
                        sleep(500);

//                      init database connection to singleton class
                        DBConnection.getInstance().init(connection);

                        Platform.runLater(() -> {
                            lblLoading.setText("Setting Up the UI....");
                            loadCreateAdminForm();
                        });


                    } catch (IOException | SQLException e) {
                        if(e instanceof SQLException){
                            dropDatabase();
                        }
                        shutdownApp(e);
                    }
                }).start();
            }
            else{
                System.out.println("Restoring.....");
                lblLoading.setText("Restoring the database");

                ProcessBuilder processBuilder = new ProcessBuilder("mysql",
                        "-h","localhost",
                        "--port","3306",
                        "-u", "root",
                        "-pmysql");
                Process start = processBuilder.redirectInput(fileProperty.getValue()).start();
                int exitCode = start.waitFor();
                if (exitCode == 0){
                    Connection connection= DriverManager.
                            getConnection("jdbc:mysql://localhost:3306/dep8_student_attendance","root","mysql");
                    loadLoginForm(connection);

                    new Alert(Alert.AlertType.CONFIRMATION,"Restore Process Succeeded").show();

                    }
                else {
                    new Alert(Alert.AlertType.ERROR, "Failed to Restore", ButtonType.OK).show();
                    System.out.println(exitCode);

                }

            }

        }catch (IOException e) {
            shutdownApp(e);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void loadLoginForm(Connection connection){

        DBConnection.getInstance().init(connection);
        try{
            Stage stage = new Stage();
            AnchorPane pane = FXMLLoader.load(this.getClass().getResource("/view/loggingForm.fxml"));
            Scene scene = new Scene(pane);
            stage.setScene(scene);
            stage.setTitle("Student attendant System : Logging Form");
            stage.sizeToScene();
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.show();

            ((Stage)(lblLoading.getScene().getWindow())).close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void loadCreateAdminForm(){
        try{
            Stage stage = new Stage();
            AnchorPane pane = FXMLLoader.load(this.getClass().getResource("/view/CreateAdminForm.fxml"));
            Scene scene = new Scene(pane);
            stage.setScene(scene);
            stage.setTitle("Student attendant System ; Create admin");
            stage.sizeToScene();
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.setOnCloseRequest((e)->dropDatabase()); // if you not complete that stage then database will be dropped
            stage.show();

            ((Stage)(lblLoading.getScene().getWindow())).close();

        } catch (IOException e) {
            System.out.println("errrrrorrr");
            e.printStackTrace();
        }
    }
    private void shutdownApp(Throwable t){

        Platform.runLater(() -> {
            lblLoading.setText("Fail to connect ");
        });
        sleep(1000);

        if (t != null) {
            t.printStackTrace();
        }
        System.exit(1);
    }

    private void dropDatabase(){
        Connection connection = null;
        try {
            connection = DriverManager.
                    getConnection("jdbc:mysql://localhost:3306", "root", "mysql");
            Statement stm = connection.createStatement();
            stm.execute("DROP DATABASE IF EXISTS dep8_student_attendance");
            connection.close();
        } catch (SQLException e) {
            shutdownApp(e);
        }
    }
    private void sleep(long millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
