package com.directprint.directprint;

import javafx.scene.control.TextArea;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.nio.file.StandardOpenOption.CREATE;

public class Utils {

    public static void writeLog(TextArea txtareaLog, String message) {
        String logMessage = String.format("[%s] %s... \n", getCurrentTimeStamp(), message);
        try {
            Files.writeString(
                    Path.of("directprint.log"), logMessage,
                    CREATE, APPEND
            );
            System.out.println("Successfully wrote to the file.");

            String log = Files.readString(Path.of("directprint.log"));

            txtareaLog.setText(log);

        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }

    public static String getCurrentTimeStamp() {
        SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//dd/MM/yyyy
        Date now = new Date();
        return sdfDate.format(now);
    }
}
