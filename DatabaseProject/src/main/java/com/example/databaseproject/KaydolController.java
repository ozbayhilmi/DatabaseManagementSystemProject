package com.example.databaseproject;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class KaydolController {

    @FXML
    private TextField address;

    @FXML
    private TextField mail;

    @FXML
    private TextField name;

    @FXML
    private PasswordField password;

    @FXML
    private Text text;

    @FXML
    void register(ActionEvent event) {
        String isim = name.getText();
        String email = mail.getText();
        String sifre = password.getText();
        String adres = address.getText();

        if (isim.isEmpty() || email.isEmpty() || sifre.isEmpty() || adres.isEmpty()) {
            // Eğer herhangi bir alan boşsa, kullanıcıya uyarı göster
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Uyarı");
            alert.setHeaderText("Eksik Bilgi");
            alert.setContentText("Lütfen tüm alanları doldurun!");
            alert.showAndWait();
            return; // İşlemi durdur
        }


        try (Connection conn = Database.getConnection()) {
            // SQL INSERT sorgusu

            String query = "INSERT INTO Users (name, email, password, address) VALUES (?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(query);

            // Parametreleri ayarla
            stmt.setString(1, isim);
            stmt.setString(2, email);
            stmt.setString(3, sifre);
            stmt.setString(4, adres);

            // Sorguyu çalıştır
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                text.setText("Kayıt başarılı!");
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("Kullanıcı başarıyla oluşturuldu!");
                alert.showAndWait();

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/databaseproject/hello-view.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();

            } else {
                text.setText("Kayıt başarısız, tekrar deneyin.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            text.setText("Hata: " + e.getMessage());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    @FXML
    void back(ActionEvent event)throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/databaseproject/hello-view.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();

    }

}
