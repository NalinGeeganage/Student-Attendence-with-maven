package lk.ijse.dep8.controllers;

import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;

public class BackupAndRestoreFormController {

    public Button btnBackup;
    public Button btnRestore;

    public void btnBackup_OnAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chose Backup file");
        fileChooser.setInitialFileName(LocalDate.now() + "-sas-bak");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Backup files (*.dep8bak)", "*.dep8bak"));
        File file = fileChooser.showSaveDialog(btnBackup.getScene().getWindow());

        if (file != null){
            ProcessBuilder mysqlDumpProcessBuilder = new ProcessBuilder("mysqldump",
                    "-h", "localhost",
                    "--port", "3306",
                    "-u", "root",
                    "-pmysql",
                    "--add-drop-database",
                    "--databases", "dep8_student_attendance");

            /*mysql -h localhost --port 3306 -u root -pmysql >backup-file-path*/
//            mysqlDumpProcessBuilder.redirectOutput(file);

            /*check operating system */

            mysqlDumpProcessBuilder.redirectOutput(System.getProperty("os.name").equalsIgnoreCase("windows")
                    || file.getAbsolutePath().endsWith(".dep8bak") ? file : new File(file.getAbsolutePath() + ".dep8bak"));

            try {
                Process mysqlDump = mysqlDumpProcessBuilder.start();
                int exitCode = mysqlDump.waitFor();
                if (exitCode == 0){
                    new Alert(Alert.AlertType.CONFIRMATION,"Backup process Succeeded").show();
                }
                else {
                    new Alert(Alert.AlertType.ERROR,"Failed to Backup", ButtonType.OK).show();
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void btnRestore_OnAction(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chose Backup file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Backup files (*.dep8bak)", "*.dep8bak"));
        File file = fileChooser.showOpenDialog(btnBackup.getScene().getWindow());

        if (file != null){
            /*mysql -h localhost --port 3306 -u root -pmysql < backup-file-path*/
            ProcessBuilder processBuilder = new ProcessBuilder("mysql",
                    "-h","localhost",
                    "--port","3306",
                    "-u","root",
                    "-pmysql");
            processBuilder.redirectInput(file);

            try{
                Process mysql = processBuilder.start();
                int exitCode = mysql.waitFor();

                if (exitCode == 0){
                    new Alert(Alert.AlertType.CONFIRMATION,"Restore Process Succeeded").show();
                }
                else {
                    new Alert(Alert.AlertType.ERROR,"Failed to Restore", ButtonType.OK).show();
                    System.out.println(exitCode);
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }

        }
    }
}
