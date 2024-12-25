package com.example.databaseproject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UrunlerimController {

    @FXML
    private TextField aciklama;

    @FXML
    private TextField searchField;

    @FXML
    private TextField konum;

    @FXML
    private TextField urun_adi;

    @FXML
    private TextField fiyat;

    @FXML
    private ChoiceBox<String> kategoriChoiceBox;

    @FXML
    private TableView<ItemClass> itemTable;

    @FXML
    private TableColumn<ItemClass, String> columnName;

    @FXML
    private TableColumn<ItemClass, String> columnDescription;

    @FXML
    private TableColumn<ItemClass, Double> columnPrice;

    @FXML
    private TableColumn<ItemClass, String> columnLocation;

    @FXML
    private TableColumn<ItemClass, String> columnCategory;

    private ObservableList<ItemClass> itemList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        columnName.setCellValueFactory(new PropertyValueFactory<>("name"));
        columnDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        columnPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        columnLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        columnCategory.setCellValueFactory(new PropertyValueFactory<>("category"));

        loadCategories();
        loadData();
    }

    private void loadCategories() {
        String url = "jdbc:mysql://localhost:3306/veri";
        String user = "root";
        String password = "Data.12345";

        String sql = "SELECT name FROM Categories";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                kategoriChoiceBox.getItems().add(rs.getString("name"));
            }

            if (!kategoriChoiceBox.getItems().isEmpty()) {
                kategoriChoiceBox.setValue(kategoriChoiceBox.getItems().get(0));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadData() {
        String url = "jdbc:mysql://localhost:3306/veri";
        String user = "root";
        String password = "Data.12345";

        String sql = """
        SELECT 
            item_name, 
            item_description, 
            item_price, 
            item_location, 
            category_name
        FROM ViewM
        WHERE user_id = ?; 
    """;

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, HelloController.userId);
            ResultSet rs = stmt.executeQuery();
            itemList.clear(); // Eski verileri temizle

            while (rs.next()) {
                // Yeni verileri listeye ekle
                itemList.add(new ItemClass(
                        rs.getString("item_name"),
                        rs.getString("item_description"),
                        rs.getDouble("item_price"),
                        rs.getString("item_location"),
                        rs.getString("category_name")
                ));
            }

            itemTable.setItems(itemList); // Listeyi tabloya ata
            itemTable.refresh(); // Tabloyu yenile
            System.out.println("Tablo güncellendi, ürün sayısı: " + itemList.size());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    void ekle_button(ActionEvent event) {
        String name = urun_adi.getText().trim();
        String description = aciklama.getText().trim();
        String location = konum.getText().trim();
        String category = kategoriChoiceBox.getValue();
        String priceText = fiyat.getText().trim();

        if (name.isEmpty() || description.isEmpty() || location.isEmpty() || category == null || priceText.isEmpty()) {
            System.out.println("Lütfen tüm alanları doldurun.");
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceText);
        } catch (NumberFormatException e) {
            System.out.println("Fiyat geçerli bir sayı olmalıdır.");
            return;
        }

        String url = "jdbc:mysql://localhost:3306/veri";
        String user = "root";
        String password = "Data.12345";

        String getCategoryIdSQL = "SELECT category_id FROM Categories WHERE name = ?";
        String insertItemSQL = """
            INSERT INTO Items (name, description, price, location, category_id, user_id) 
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            conn.setAutoCommit(false);

            try (PreparedStatement categoryStmt = conn.prepareStatement(getCategoryIdSQL);
                 PreparedStatement insertStmt = conn.prepareStatement(insertItemSQL)) {

                categoryStmt.setString(1, category);
                ResultSet rs = categoryStmt.executeQuery();

                if (rs.next()) {
                    int categoryId = rs.getInt("category_id");

                    insertStmt.setString(1, name);
                    insertStmt.setString(2, description);
                    insertStmt.setDouble(3, price);
                    insertStmt.setString(4, location);
                    insertStmt.setInt(5, categoryId);
                    insertStmt.setInt(6, HelloController.userId);

                    int rowsAffected = insertStmt.executeUpdate();
                    if (rowsAffected > 0) {
                        conn.commit();
                        System.out.println("Ürün başarıyla eklendi.");
                        loadData(); // Tabloyu güncelle
                    }
                } else {
                    System.out.println("Kategori bulunamadı.");
                    conn.rollback();
                }
            } catch (Exception e) {
                conn.rollback();
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        loadData();
        itemTable.refresh();
    }

    @FXML
    void sil_button(ActionEvent event) {
        ItemClass selectedItem = itemTable.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            System.out.println("Lütfen silmek için bir ürün seçin.");
            return;
        }

        String url = "jdbc:mysql://localhost:3306/veri";
        String user = "root";
        String password = "Data.12345";

        String deleteSQL = "DELETE FROM Items WHERE name = ? AND location = ? AND user_id = ?";

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(deleteSQL)) {

            stmt.setString(1, selectedItem.getName());
            stmt.setString(2, selectedItem.getLocation());
            stmt.setInt(3, HelloController.userId);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Ürün başarıyla silindi.");
                loadData(); // Tabloyu güncelle
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        loadData();
        itemTable.refresh();
    }
    @FXML
    void siparislerim_buton(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/databaseproject/siparislerim.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();

    }


    @FXML
    void ana_menu(ActionEvent event) throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/databaseproject/anamenu.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();

    }


    @FXML
    void favoriler_button(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/databaseproject/favoriler.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();

    }

    @FXML
    void ara_button(ActionEvent event) {

        String searchQuery = searchField.getText().trim();
        if (searchQuery.isEmpty()) {
            loadData();
            return;
        }
        String url = "jdbc:mysql://localhost:3306/veri";
        String user = "root";
        String password = "Data.12345";

        String sql = """
        SELECT 
            i.name AS item_name, 
            i.description AS item_description, 
            i.price AS item_price, 
            i.location AS item_location, 
            c.name AS category_name
        FROM Items i
        JOIN Categories c ON i.category_id = c.category_id
        WHERE i.user_id = ? AND i.name LIKE ?;
    """;

        try (Connection conn = DriverManager.getConnection(url, user, password);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, HelloController.userId);
            stmt.setString(2, "%" + searchQuery + "%");

            ResultSet rs = stmt.executeQuery();
            itemList.clear();
            while (rs.next()) {
                itemList.add(new ItemClass(
                        rs.getString("item_name"),
                        rs.getString("item_description"),
                        rs.getDouble("item_price"),
                        rs.getString("item_location"),
                        rs.getString("category_name")
                ));
            }
            itemTable.setItems(itemList);
            itemTable.refresh();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    void bildirimler_table_onAction(ActionEvent event) {

    }

    @FXML
    void cikis_button(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/databaseproject/hello-view.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();

    }

    @FXML
    void duzenle_button(ActionEvent event) {
        // Tablo üzerinde seçili öğeyi al
        ItemClass selectedItem = itemTable.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            System.out.println("Lütfen düzenlemek için bir ürün seçin.");
            return;
        }

        // Kullanıcıdan alınan yeni değerler
        String newName = urun_adi.getText().trim();
        String newDescription = aciklama.getText().trim();
        String newLocation = konum.getText().trim();
        String newCategory = kategoriChoiceBox.getValue();
        String newPriceText = fiyat.getText().trim();

        if (newName.isEmpty() || newDescription.isEmpty() || newLocation.isEmpty() || newCategory == null || newPriceText.isEmpty()) {
            System.out.println("Lütfen tüm alanları doldurun.");
            return;
        }

        double newPrice;
        try {
            newPrice = Double.parseDouble(newPriceText);
        } catch (NumberFormatException e) {
            System.out.println("Fiyat geçerli bir sayı olmalıdır.");
            return;
        }

        String url = "jdbc:mysql://localhost:3306/veri";
        String user = "root";
        String password = "Data.12345";

        String getCategoryIdSQL = "SELECT category_id FROM Categories WHERE name = ?";
        String updateItemSQL = """
        UPDATE Items 
        SET name = ?, description = ?, price = ?, location = ?, category_id = ?
        WHERE name = ? AND location = ? AND user_id = ?
    """;

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            conn.setAutoCommit(false);

            try (PreparedStatement categoryStmt = conn.prepareStatement(getCategoryIdSQL);
                 PreparedStatement updateStmt = conn.prepareStatement(updateItemSQL)) {
                categoryStmt.setString(1, newCategory);
                ResultSet rs = categoryStmt.executeQuery();

                if (rs.next()) {
                    int newCategoryId = rs.getInt("category_id");


                    updateStmt.setString(1, newName);
                    updateStmt.setString(2, newDescription);
                    updateStmt.setDouble(3, newPrice);
                    updateStmt.setString(4, newLocation);
                    updateStmt.setInt(5, newCategoryId);


                    updateStmt.setString(6, selectedItem.getName());
                    updateStmt.setString(7, selectedItem.getLocation());
                    updateStmt.setInt(8, HelloController.userId);

                    int rowsAffected = updateStmt.executeUpdate();
                    if (rowsAffected > 0) {
                        conn.commit();
                        System.out.println("Ürün başarıyla güncellendi.");
                        loadData(); // Tabloyu güncelle
                    } else {
                        System.out.println("Ürün güncellenemedi.");
                        conn.rollback();
                    }
                } else {
                    System.out.println("Kategori bulunamadı.");
                    conn.rollback();
                }

            } catch (Exception e) {
                conn.rollback();
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Tabloyu yenile
        loadData();
        itemTable.refresh();
    }





}