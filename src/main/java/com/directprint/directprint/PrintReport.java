package com.directprint.directprint;

import javafx.scene.control.TextArea;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.engine.export.JRPrintServiceExporterParameter;

import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.print.attribute.HashPrintServiceAttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.PrintServiceAttributeSet;
import javax.print.attribute.standard.Copies;
import javax.print.attribute.standard.PrinterName;
import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.prefs.Preferences;

public class PrintReport extends JFrame {

    public void PrintTicketAdmisiIRJA(PrinterViewController.TiketAdmisiIRJA tiketAdmisiIrja,TextArea txtareaLog) throws JRException, ClassNotFoundException {

        try {
            String reportSrcFile = "report/ticket-admisi-irja.jrxml";

            // First, compile jrxml file.
            JasperReport jasperReport = JasperCompileManager.compileReport(reportSrcFile);
            // Fields for report
            HashMap<String, Object> parameters = new HashMap<>();

            parameters.put("no_register", tiketAdmisiIrja.no_register);
            parameters.put("nama_pasien", tiketAdmisiIrja.nama_pasien);
            parameters.put("nama_poli", tiketAdmisiIrja.nama_poli);

            ArrayList<HashMap<String, Object>> list = new ArrayList<>();
            list.add(parameters);

            JRBeanCollectionDataSource beanColDataSource = new JRBeanCollectionDataSource(list);
            JasperPrint print = JasperFillManager.fillReport(jasperReport, null, beanColDataSource);
            //JasperPrintManager.printReport(print,false);
            PrintReportToPrinter(print);
//        JRViewer viewer = new JRViewer(print);
//        viewer.setOpaque(true);
//        viewer.setVisible(true);
//        this.add(viewer);
//        this.setSize(700, 500);
//        this.setVisible(true);
            System.out.print("Done!");
        } catch (Exception ex) {
            Utils.writeLog(txtareaLog,"error print ticket admisi");
            ex.printStackTrace();
        }

    }

    public void PrintTest(TextArea txtareaLog) throws JRException, ClassNotFoundException {

        try {
            String reportSrcFile = "report/test-print.jrxml";

            // First, compile jrxml file.
            JasperReport jasperReport = JasperCompileManager.compileReport(reportSrcFile);

            ArrayList<HashMap<String, Object>> list = new ArrayList<>();

            JRBeanCollectionDataSource beanColDataSource = new JRBeanCollectionDataSource(list);

            JasperPrint print = JasperFillManager.fillReport(jasperReport, null, beanColDataSource);
            //JasperPrintManager.printReport(print,false);
            PrintReportToPrinter(print);
//        JRViewer viewer = new JRViewer(print);
//        viewer.setOpaque(true);
//        viewer.setVisible(true);
//        this.add(viewer);
//        this.setSize(700, 500);
//        this.setVisible(true);
            System.out.print("Done!");
        } catch (Exception ex) {
            Utils.writeLog(txtareaLog,"error test print");
            ex.printStackTrace();
        }

    }

    private void PrintReportToPrinter(JasperPrint jp) throws JRException {

        PrintRequestAttributeSet printRequestAttributeSet = new HashPrintRequestAttributeSet();
        //printRequestAttributeSet.add(MediaSizeName.); //setting page size
        printRequestAttributeSet.add(new Copies(1));

        Preferences pref;
        pref = Preferences.userNodeForPackage(PrinterApplication.class);
        String preference = pref.get("default_printer", "");

        PrinterName printerName = new PrinterName(preference, null); //gets printer

        PrintServiceAttributeSet printServiceAttributeSet = new HashPrintServiceAttributeSet();
        printServiceAttributeSet.add(printerName);

        JRPrintServiceExporter exporter = new JRPrintServiceExporter();

        exporter.setParameter(JRExporterParameter.JASPER_PRINT, jp);
        exporter.setParameter(JRPrintServiceExporterParameter.PRINT_REQUEST_ATTRIBUTE_SET, printRequestAttributeSet);
        exporter.setParameter(JRPrintServiceExporterParameter.PRINT_SERVICE_ATTRIBUTE_SET, printServiceAttributeSet);
        exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PAGE_DIALOG, Boolean.FALSE);
        exporter.setParameter(JRPrintServiceExporterParameter.DISPLAY_PRINT_DIALOG, Boolean.FALSE);
        exporter.exportReport();
    }
}
