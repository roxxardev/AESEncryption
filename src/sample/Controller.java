package sample;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.FileChooser;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;

public class Controller {

    public Label fileLabel;
    public Button fileEncryptButton;
    public Button textDecryptButton;
    public TextArea plainTextField;
    public TextArea encryptedTextField;
    public Button fileDecryptButton;

    private AESEncryption textAesEncryption;
    private AESEncryption fileAesEncryption;

    private File fileToEncrypt;

    public void openFile(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(((Node) actionEvent.getSource()).getScene().getWindow());
        if (file != null) {
            fileLabel.setText(file.getName());
            fileEncryptButton.setVisible(true);
            fileToEncrypt = file;
        }
    }

    public void encryptText(ActionEvent actionEvent) {
        try {
            textAesEncryption = new AESEncryption();
            String encryptedText = textAesEncryption.encryptAndEncodeBASE64(plainTextField.getText());
            saveTextToFile(encryptedText);
            plainTextField.setText("");
            encryptedTextField.setText(encryptedText);
            textDecryptButton.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void decryptText(ActionEvent actionEvent) {
        if(textAesEncryption == null) return;
        try {
            String encryptedText = readTextFromFile();
            String decryptedText = textAesEncryption.decodeBASE64AndDecrypt(encryptedText);
            plainTextField.setText(decryptedText);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveTextToFile(String s) throws IOException {
            FileWriter fileWriter = new FileWriter("encryptedText.txt");
            fileWriter.write(s);
            fileWriter.close();
    }

    private String readTextFromFile() throws IOException {
        return new String(Files.readAllBytes(Paths.get("encryptedText.txt")));
    }

    public void encryptFile(ActionEvent actionEvent) {
        try {
            fileAesEncryption = new AESEncryption();
            File outputFile = new File("encryptedFile");
            fileAesEncryption.encryptFile(fileToEncrypt, outputFile);
            saveToDatabase(fileToEncrypt, outputFile);
            fileDecryptButton.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void decryptFile(ActionEvent actionEvent) {
        File encryptedFile = new File("encryptedFile");
        try {
            fileAesEncryption.decryptFile(encryptedFile, new File("decryptedFile"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void doAll(ActionEvent actionEvent) {
        if(fileToEncrypt == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Blad");
            alert.setContentText("Wybierz plik");
            alert.showAndWait();
            return;
        }

        encryptText(actionEvent);
        decryptText(actionEvent);
        encryptFile(actionEvent);
        decryptFile(actionEvent);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Sukces");
        alert.setHeaderText(null);
        alert.setContentText("Wykonano pomyslnie");
        alert.showAndWait();
    }

    private void saveToDatabase(File fileToEncrypt, File encryptedFile) throws Exception {
        Connection connection = ConnectionConfiguration.getConnection();

        try {
            String query = " insert into pliki (`key`, file, extension)"
                    + " values (?, ?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query);

            byte[] key = fileAesEncryption.getKeyValue();

            FileInputStream inputStream = new FileInputStream(encryptedFile);
            byte[] file = new byte[(int) encryptedFile.length()];
            inputStream.read(file);

            String extension = "";
            int i = fileToEncrypt.getName().lastIndexOf('.');
            if(i>0) extension = fileToEncrypt.getName().substring(i+1);

            preparedStatement.setBytes(1, key);
            preparedStatement.setBytes(2, file);
            preparedStatement.setString(3, extension);

            preparedStatement.execute();

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
