package com.example.databaseproject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class FavorilerController {

    @FXML
    private TableView<FavoriteItem> favoritesTable;
    @FXML
    private TextField searchField;

    @FXML
    private TableColumn<FavoriteItem, Integer> favoriteIdColumn;

    @FXML
    private TableColumn<FavoriteItem, Integer> itemIdColumn;

    @FXML
    private TableColumn<FavoriteItem, String> nameColumn;

    @FXML
    private TableColumn<FavoriteItem, String> descriptionColumn;

    @FXML
    private TableColumn<FavoriteItem, Double> priceColumn;

    @FXML
    private TableColumn<FavoriteItem, String> locationColumn;

    private ObservableList<FavoriteItem> favoriteItems = FXCollections.observableArrayList();

    @FXML
    public void initialize() {

        favoriteIdColumn.setCellValueFactory(new PropertyValueFactory<>("favoriteId"));
        itemIdColumn.setCellValueFactory(new PropertyValueFactory<>("itemId"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        locationColumn.setCellValueFactory(new PropertyValueFactory<>("location"));

        loadFavoriteItems();
    }

    private void loadFavoriteItems() {
        String query = "SELECT Favorites.favorite_id, Items.item_id, Items.name, Items.description, Items.price, Items.location " +
                "FROM Favorites INNER JOIN Items ON Favorites.item_id = Items.item_id WHERE Favorites.user_id = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, HelloController.userId); // Giriş yapan kullanıcının ID'sini kullan

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int favoriteId = rs.getInt("favorite_id");
                int itemId = rs.getInt("item_id");
                String name = rs.getString("name");
                String description = rs.getString("description");
                double price = rs.getDouble("price");
                String location = rs.getString("location");
                
                favoriteItems.add(new FavoriteItem(favoriteId, itemId, name, description, price, location));
            }

            // Verileri tabloya bağla
            favoritesTable.setItems(favoriteItems);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void delete_button(ActionEvent event) {
        // TableView'de seçili öğeyi al
        FavoriteItem selectedFavorite = favoritesTable.getSelectionModel().getSelectedItem();

        if (selectedFavorite != null) {
            int favoriteId = selectedFavorite.getFavoriteId(); // Silinecek öğenin ID'si

            String deleteQuery = "DELETE FROM Favorites WHERE favorite_id = ?";

            try (Connection conn = Database.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(deleteQuery)) {

                // Silinecek öğenin ID'sini sorguya ekle
                stmt.setInt(1, favoriteId);

                int rowsAffected = stmt.executeUpdate(); // Veritabanında kaydı sil

                if (rowsAffected > 0) {
                    // ObservableList'ten öğeyi kaldır
                    favoriteItems.remove(selectedFavorite);
                    // Tabloyu güncelle
                    favoritesTable.refresh();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Silmek için bir öğe seçin.");
        }
    }

    @FXML
    void accountButton(ActionEvent event) {

    }

    @FXML
    void cikis_button(ActionEvent event) throws IOException {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("Cikis yapiliyor!!");
        alert.showAndWait();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/databaseproject/hello-view.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }


    @FXML
    void productsButton(ActionEvent event) throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/databaseproject/urunlerim.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    void searchButton(ActionEvent event) {

        String searchQuery = searchField.getText().trim();
        // Arama metni boşsa, tüm verileri yükle
        if (searchQuery.isEmpty()) {
            favoriteItems.clear(); // Listeyi temizle
            loadFavoriteItems();   // Tüm favori öğelerini yükle
            return;
        }

        // ObservableList'i temizle
        favoriteItems.clear();

        String query = "SELECT Favorites.favorite_id, Items.item_id, Items.name, Items.description, Items.price, Items.location " +
                "FROM Favorites INNER JOIN Items ON Favorites.item_id = Items.item_id " +
                "WHERE Favorites.user_id = ? AND Items.name LIKE ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, HelloController.userId); // Kullanıcı ID'sini ayarla
            stmt.setString(2, "%" + searchQuery + "%"); // Arama terimini LIKE ile bağla

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int favoriteId = rs.getInt("favorite_id");
                int itemId = rs.getInt("item_id");
                String name = rs.getString("name");
                String description = rs.getString("description");
                double price = rs.getDouble("price");
                String location = rs.getString("location");
                favoriteItems.add(new FavoriteItem(favoriteId, itemId, name, description, price, location));
            }

            // Verileri tabloya bağla ve tabloyu yenile
            favoritesTable.setItems(favoriteItems);
            favoritesTable.refresh();

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Hata");
            alert.setHeaderText("Bir hata oluştu");
            alert.setContentText("Hata mesajı: " + e.getMessage());
            alert.showAndWait();
        }
    }


    @FXML
    void sepetim_button(ActionEvent event)throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/databaseproject/siparislerim.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();

    }

    @FXML
    void ana_sayfa(ActionEvent event)throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/databaseproject/anamenu.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();

    }



}