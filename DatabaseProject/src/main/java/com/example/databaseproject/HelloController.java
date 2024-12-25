package com.example.databaseproject;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.w3c.dom.Node;
import javafx.scene.text.Text;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class HelloController {

    static String mail; // Kullanıcının e-posta adresi
    static String sifre; // Kullanıcının şifresi
    static int userId;

    @FXML
    private PasswordField password;

    @FXML
    private TextField email;

    @FXML
    private Text text;

    @FXML
    void login_button(ActionEvent event) {
        String user = email.getText();
        String pass = password.getText();

        try (Connection conn = Database.getConnection()) {
            // Giriş kontrolü ve kullanıcı ID'sini tek bir sorgu ile alıyoruz
            String query = "SELECT user_id FROM Users WHERE email = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, user);
            stmt.setString(2, pass);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // Kullanıcı doğrulandı ve ID bulundu
                userId = rs.getInt("user_id");  // Kullanıcı ID'sini alıyoruz
                mail = user;              // E-posta adresini kaydediyoruz
                sifre = pass;             // Şifreyi kaydediyoruz

                text.setText("Giriş başarılı, Kullanıcı ID: " + userId);


                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("Giriş Başarılı. Ana Menüye Yönlendiriliyorsunuz!");
                alert.showAndWait();

                // Ana menüye geçiş
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/databaseproject/anamenu.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();

            } else {

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setContentText("Kullanıcı veya şifre hatalı!");
                alert.showAndWait();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText("Veritabanı Hatası!");
            alert.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            text.setText("Hata: " + e.getMessage());
        }
    }

    @FXML
    void register_button(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/databaseproject/kaydol.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            text.setText("Yeni kayıt ekranına geçerken hata oluştu.");
        }
    }

}
