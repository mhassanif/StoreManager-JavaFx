package com.storemanager.dao;

import com.storemanager.model.order.OrderItem;
import com.storemanager.model.items.Product;
import com.storemanager.db.DBconnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderItemDAO {

    private final ProductDAO productDAO = new ProductDAO();

    // Retrieve OrderItems by Order ID
    public static List<OrderItem> getOrderItemsByOrderId(int orderId) throws SQLException {
        String sql = "SELECT * FROM ORDERITEM WHERE order_id = ?";
        List<OrderItem> orderItems = new ArrayList<>();

        try (Connection connection = DBconnector.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, orderId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int productId = rs.getInt("product_id");
                    Product product = productDAO.getProductById(productId); // Fetch product details
                    int quantity = rs.getInt("quantity");
                    double priceAtPurchase = rs.getDouble("price");
                    OrderItem orderItem = new OrderItem(product, quantity, priceAtPurchase);
                    orderItems.add(orderItem);
                }
            }
        }
        return orderItems;
    }

    // Calculate total price for a list of OrderItems
    public static double calculateTotalPrice(List<OrderItem> orderItems) {
        double total = 0;
        for (OrderItem item : orderItems) {
            total += item.calculateTotalPrice(); // Calculate price per item and sum them up
        }
        return total;
    }
}
