package com.directprint.directprint;

import com.google.gson.Gson;
import io.socket.client.IO;
import io.socket.client.Socket;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.util.Duration;
import net.sf.jasperreports.engine.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.controlsfx.control.Notifications;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import java.io.*;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.prefs.Preferences;


public class PrinterViewController implements Initializable {


    @FXML
    private ComboBox<String> cmbPrinter;

    @FXML
    private Label lblDefaultPrinterValue;

    @FXML
    private Label lblConnectedSocket;

    @FXML
    private TextArea txtToken;

    @FXML
    private TextArea txtareaLog;

    private Socket socket;

    public static class Clients {
        public String customId;
    }

    public static class TiketAdmisiIRJA {
        public String no_register;
        public String nama_pasien;
        public String nama_poli;
    }

    private static final String SocketAddressDevelopment = "http://localhost:3000";
    private static final String SocketAddressProduction = "http://174.138.22.139:4444";


    @FXML
    protected void onBtnTestPrintClick() {

        if(!IsDefaultPrinterSet())
        {
            Dialog<String> dialog = new Dialog<>();
            dialog.setTitle("Warning");
            ButtonType type = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
            dialog.setContentText("Default Printer not set!");
            dialog.getDialogPane().getButtonTypes().add(type);
            dialog.showAndWait();
            return;
        }

        Notifications.create()
                .title("HIS PRINTER TOOL")
                .text("Printing..").hideAfter(Duration.seconds(2))
                .showInformation();

        Utils.writeLog(txtareaLog,"test print");

        try {

            new PrintReport().PrintTest(txtareaLog);
        } catch (JRException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void onBtnCopyTokenClick() {
        final Clipboard clipboard = Clipboard.getSystemClipboard();
        final ClipboardContent content = new ClipboardContent();
        content.putString(txtToken.getText());
        clipboard.setContent(content);

        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Information");
        ButtonType type = new ButtonType("Ok", ButtonBar.ButtonData.OK_DONE);
        dialog.setContentText("copied to clipboard!");
        dialog.getDialogPane().getButtonTypes().add(type);
        dialog.showAndWait();
    }

    @FXML
    protected void onSetDefaultPrinterClick() {
        Preferences pref;
        pref = Preferences.userNodeForPackage(PrinterApplication.class);
        pref.put("default_printer", cmbPrinter.getValue());

        pref = Preferences.userNodeForPackage(PrinterApplication.class);
        String preference = pref.get("default_printer", "yourPreferenceValue");

        lblDefaultPrinterValue.setText(preference);

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        PrintService[] printServices = PrintServiceLookup.lookupPrintServices(null, null);
        cmbPrinter.getItems().clear();
        for (PrintService printer : printServices)
            cmbPrinter.getItems().add(printer.getName());

        Preferences pref;
        pref = Preferences.userNodeForPackage(PrinterApplication.class);
        String preference = pref.get("default_printer", "");
        lblDefaultPrinterValue.setText(preference);


        InetAddress localHost;
        String mac_address = "";

        try {
            localHost = InetAddress.getLocalHost();
            NetworkInterface ni = NetworkInterface.getByInetAddress(localHost);
            byte[] hardwareAddress = ni.getHardwareAddress();

            String[] hexadecimal = new String[hardwareAddress.length];
            for (int i = 0; i < hardwareAddress.length; i++) {
                hexadecimal[i] = String.format("%02X", hardwareAddress[i]);
            }
            mac_address = String.join("-", hexadecimal);
            System.out.println(mac_address);

        } catch (Exception e) {
            e.printStackTrace();
        }

        String sha256hex = DigestUtils.sha256Hex(mac_address);
        txtToken.setText(sha256hex);

        try {
            socket = IO.socket(SocketAddressProduction);
            socket.connect();
            System.out.println(socket.connected());

            Clients client = new Clients();
            client.customId = sha256hex;

            Gson gson = new Gson();
            socket.on(Socket.EVENT_CONNECT, objects -> {
                System.out.println("connected to socket");
                Platform.runLater(() -> {
                    Utils.writeLog(txtareaLog,"connected to socket");
                    lblConnectedSocket.setText("connected to socket");
                });
                socket.emit("storeClientInfo", gson.toJson(client));
            });

            socket.on("messages", objects -> System.out.println(Arrays.toString(objects)));

            socket.on("print:ticket-admisi-irja", objects -> {
                System.out.println("printing...");
                Platform.runLater(() -> {
                    Notifications.create()
                            .title("HIS PRINTER TOOL")
                            .text("Printing..").hideAfter(Duration.seconds(2))
                            .showInformation();

                    System.out.println(Arrays.toString(objects));
                    System.out.println(objects[0]);

                    var tiketObj =
                            gson.fromJson(objects[0].toString(), TiketAdmisiIRJA.class);

                    try {
                        Utils.writeLog(txtareaLog,"print tiket admisi irja");
                        new PrintReport().PrintTicketAdmisiIRJA(tiketObj,txtareaLog);
                    } catch (JRException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                });
            });

            socket.on("disconnect", objects -> Platform.runLater(() -> {
                Utils.writeLog(txtareaLog,"disconnect from socket");
                lblConnectedSocket.setText("disconnect from socket");
            }));

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        try {
            File myObj = new File("directprint.log");
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
            BufferedWriter writer = Files.newBufferedWriter(Paths.get("directprint.log"));
            writer.write("");
            writer.flush();
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

    }

    private boolean IsDefaultPrinterSet()
    {
        Preferences pref;
        pref = Preferences.userNodeForPackage(PrinterApplication.class);
        String preference = pref.get("default_printer", "");

        return !Objects.equals(preference, "");
    }
}


