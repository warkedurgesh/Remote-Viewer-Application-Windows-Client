package utils;

import javafx.scene.control.Alert;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;

public class Utils {

    public static byte[] getFileBytes() {
        File file = new File("arhat.jpg");
        try {
            return Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

    public static byte[] convertObjToByteArray(Object obj) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ObjectOutputStream os = null;
        try {
            os = new ObjectOutputStream(outputStream);
            os.writeObject(obj);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputStream.toByteArray();
    }


    public static void showAlert(Alert.AlertType error, String title, String header) {
        Alert alert = new Alert(error);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.show();
    }
}
