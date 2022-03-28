package lk.ijse.dep8.controllers;

import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import lk.ijse.dep8.db.DBConnection;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;

import java.sql.Connection;
import java.util.HashMap;

public class viewReportController {
    public Button btnDateRange;
    public Button btnTodayAttendance;
    public Button btnFindStudentAttendance;

    public void btnTodayAttendanceClickOnAction(ActionEvent event) throws JRException {
        try {
            Connection connection = DBConnection.getInstance().getConnection();
            JasperDesign jasperDesign = JRXmlLoader.load(this.getClass().getResourceAsStream("/report/student-attendance.jrxml"));
            JasperReport jasperReport = JasperCompileManager.compileReport(jasperDesign);

            HashMap<String, Object> parameters = new HashMap<>();
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, connection);
            JasperViewer.viewReport(jasperPrint);
        }catch (JRException e){
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR,"Failed to load Jasper report", ButtonType.OK).show();
        }
    }

    public void btnFindStudentAttendanceClickOnAction(ActionEvent event) {
    }

    public void btnDateRangeClickOnAction(ActionEvent event) {
    }
}
