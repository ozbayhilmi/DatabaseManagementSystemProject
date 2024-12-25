package com.example.databaseproject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
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

public class OrderController {
    @FXML
    private TableView<Order> orderTable;

    @FXML
    private TableColumn<Order, Integer> colOrderId;
    @FXML
    private TableColumn<Order, Integer> colItemId;
    @FXML
    private TableColumn<Order, Integer> colQuantity;
    @FXML
    private TableColumn<Order, String> colStatus;
    @FXML
    private TableColumn<Order, String> colItemName;
    @FXML
    private TableColumn<Order, Double> colItemPrice;

    @FXML
    private TextField searchField;

    private ObservableList<Order> orderList;

    @FXML
    public void initialize() {
        colOrderId.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        colItemId.setCellValueFactory(new PropertyValueFactory<>("itemId"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colItemName.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        colItemPrice.setCellValueFactory(new PropertyValueFactory<>("itemPrice"));

        loadOrderData();
    }


    private void loadOrderData() {
        orderList = FXCollections.observableArrayList();

        try {
            // Veritabanı bağlantısı
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/veri?useSSL=false&serverTimezone=UTC", "root", "Data.12345"
            );

            // SQL sorgusu
            String query = "SELECT o.order_id, o.user_id, o.item_id, o.quantity, o.status, " +
                    "i.name AS item_name, i.price AS item_price " +
                    "FROM Orders o " +
                    "JOIN Items i ON o.item_id = i.item_id " +
                    "WHERE o.user_id = ?";

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, HelloController.userId); // Kullanıcı ID'yi alıyoruz

            ResultSet rs = stmt.executeQuery();

            // Verileri Order listesine ekliyoruz
            while (rs.next()) {
                orderList.add(new Order(
                        rs.getInt("order_id"),
                        rs.getInt("user_id"),
                        rs.getInt("item_id"),
                        rs.getInt("quantity"),
                        rs.getString("status"),
                        rs.getString("item_name"),
                        rs.getDouble("item_price")
                ));
            }

            // TableView'e verileri set ediyoruz
            orderTable.setItems(orderList);

        } catch (Exception e) {
            e.printStackTrace();
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
    void delete_button(ActionEvent event) {
        Order selectedOrder = orderTable.getSelectionModel().getSelectedItem();
        if (selectedOrder == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Uyarı");
            alert.setHeaderText("Silme işlemi başarısız!");
            alert.setContentText("Lütfen silmek için bir öğe seçin.");
            alert.showAndWait();
            return;
        }

        int orderId = selectedOrder.getOrderId();
        try {

            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/veri?useSSL=false&serverTimezone=UTC", "root", "Data.12345"
            );
            String query = "DELETE FROM Orders WHERE order_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, orderId); // Silinecek öğenin ID'sini bağla
            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {

                orderList.remove(selectedOrder);
                orderTable.refresh();
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Bilgi");
                alert.setContentText("Sipariş başarıyla silindi.");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Hata");
                alert.setHeaderText("Silme işlemi başarısız!");
                alert.setContentText("Sipariş silinemedi, tekrar deneyin.");
                alert.showAndWait();
            }

        } catch (Exception e) {
            e.printStackTrace();

            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Hata");
            alert.setHeaderText("Bir hata oluştu!");
            alert.setContentText("Silme işlemi sırasında bir hata oluştu: " + e.getMessage());
            alert.showAndWait();
        }
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

        if (searchQuery.isEmpty()) {
            loadOrderData();
            return;
        }
        orderList.clear();

        try {
            Connection conn = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/veri?useSSL=false&serverTimezone=UTC", "root", "Data.12345"
            );
            String query = "SELECT o.order_id, o.user_id, o.item_id, o.quantity, o.status, " +
                    "i.name AS item_name, i.price AS item_price " +
                    "FROM Orders o " +
                    "JOIN Items i ON o.item_id = i.item_id " +
                    "WHERE o.user_id = ? AND i.name LIKE ?";

            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, HelloController.userId); // Kullanıcı ID
            stmt.setString(2, "%" + searchQuery + "%"); // Arama terimiyle eşleştir

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                orderList.add(new Order(
                        rs.getInt("order_id"),
                        rs.getInt("user_id"),
                        rs.getInt("item_id"),
                        rs.getInt("quantity"),
                        rs.getString("status"),
                        rs.getString("item_name"),
                        rs.getDouble("item_price")
                ));
            }
            orderTable.setItems(orderList);
            orderTable.refresh();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    void ana_menu(ActionEvent event)throws IOException {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("Ana menuye donuluyor!");
        alert.showAndWait();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/databaseproject/anamenu.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();

    }



}