package com.example.databaseproject;
import javafx.fxml.FXMLLoader;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.scene.text.Text;


import java.io.IOException;
import java.sql.*;

public class AnaMenuController {

    @FXML
    private TableColumn<Item, Integer> itemId;

    @FXML
    private Text text;

    @FXML
    private TableColumn<Item, String> name;

    @FXML
    private TableColumn<Item, String> description;

    @FXML
    private TableColumn<Item, Double> price;

    @FXML
    private TableColumn<Item, Integer> categoryId;

    @FXML
    private TableColumn<Item, Integer> userId;

    @FXML
    private TableColumn<Item, String> konum;

    @FXML
    private TableView<Item> table;

    @FXML
    private TextField searchText;

    // Veritabanından çekilen öğeleri tutacak ObservableList
    private ObservableList<Item> itemList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Kolonları model sınıfı özelliklerine bağlama
        itemId.setCellValueFactory(new PropertyValueFactory<>("itemId"));
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        description.setCellValueFactory(new PropertyValueFactory<>("description"));
        price.setCellValueFactory(new PropertyValueFactory<>("price"));
        categoryId.setCellValueFactory(new PropertyValueFactory<>("categoryId"));
        userId.setCellValueFactory(new PropertyValueFactory<>("userId"));
        konum.setCellValueFactory(new PropertyValueFactory<>("location"));

        // Verileri yükle
        loadItemsFromDatabase();

        // Verileri TableView'e bağla
        table.setItems(itemList);

        text.setText(HelloController.mail);
    }


    private void loadItemsFromDatabase() {
        String query = "SELECT * FROM Items WHERE user_id != ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, HelloController.userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("item_id");
                String name = rs.getString("name");
                String description = rs.getString("description");
                double price = rs.getDouble("price");
                int categoryId = rs.getInt("category_id");
                int userId = rs.getInt("user_id");
                String location = rs.getString("location");

                itemList.add(new Item(id, name, description, price, categoryId, userId, location));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void account(ActionEvent event) {

    }

    @FXML
    void favorilere_ekle(ActionEvent event) {

        Item selectedItem = table.getSelectionModel().getSelectedItem();// TableView dan eleman seçme.
        if (selectedItem == null) {
            System.out.println("Lütfen favorilere eklemek için bir ürün seçin.");
            return;
        }

        int itemId = selectedItem.getItemId();
        int userId = HelloController.userId; // Şu anki kullanıcı ID

        String insertFavoriteSQL = """
        INSERT INTO Favorites (user_id, item_id)
        VALUES (?, ?)
    """;

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertFavoriteSQL)) {


            stmt.setInt(1, userId);
            stmt.setInt(2, itemId);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Ürün favorilere başarıyla eklendi.");
            } else {
                System.out.println("Ürün favorilere eklenemedi.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @FXML
    void exit(ActionEvent event) throws IOException {

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
    void favories(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/databaseproject/favoriler.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    void products(ActionEvent event) throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/databaseproject/urunlerim.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    void search(ActionEvent event) {

        String searchQuery = searchText.getText().trim();
        if (searchQuery.isEmpty()) {
            loadItemsFromDatabase();
            return;
        }
        itemList.clear();
        String query = "SELECT * FROM Items WHERE user_id != ? AND name LIKE ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, HelloController.userId);
            stmt.setString(2, "%" + searchQuery + "%"); // Arama terimini LIKE ile kullan

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("item_id");
                String name = rs.getString("name");
                String description = rs.getString("description");
                double price = rs.getDouble("price");
                int categoryId = rs.getInt("category_id");
                int userId = rs.getInt("user_id");
                String location = rs.getString("location");

                itemList.add(new Item(id, name, description, price, categoryId, userId, location));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void siparis_ver(ActionEvent event) {
        // TableView'den seçili öğeyi al
        Item selectedItem = table.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            System.out.println("Lütfen sipariş vermek için bir ürün seçin.");
            return;
        }

        // Siparişe ait bilgiler
        int itemId = selectedItem.getItemId();
        int userId = HelloController.userId; // Siparişi veren kullanıcı
        int quantity = 1; // Varsayılan olarak 1 adet sipariş verilebilir
        String status = "cart"; // Başlangıç durumu 'cart'

        String insertOrderSQL = """
        INSERT INTO Orders (user_id, item_id, quantity, status)
        VALUES (?, ?, ?, ?)
    """;

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertOrderSQL)) {

            // Değerleri sorguya yerleştir
            stmt.setInt(1, userId);
            stmt.setInt(2, itemId);
            stmt.setInt(3, quantity);
            stmt.setString(4, status);

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Sipariş başarıyla oluşturuldu.");
            } else {
                System.out.println("Sipariş oluşturulamadı.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void siparisler(ActionEvent event)throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/databaseproject/siparislerim.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();

    }


}