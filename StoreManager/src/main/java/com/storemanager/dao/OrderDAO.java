package com.storemanager.dao;

import com.storemanager.model.order.Order;
import com.storemanager.model.order.OrderItem;
import com.storemanager.model.users.Customer;
import com.storemanager.db.DBconnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {

    // Retrieve an order by its ID
    public Order getOrderById(int orderId) throws SQLException {
        String orderSql = "SELECT * FROM ORDERTABLE WHERE order_id = ?";
        Order order = null;

        try (Connection connection = DBconnector.getConnection()) {
            // Fetch order details
            try (PreparedStatement orderStmt = connection.prepareStatement(orderSql)) {
                orderStmt.setInt(1, orderId);
                try (ResultSet orderRs = orderStmt.executeQuery()) {
                    if (orderRs.next()) {
                        int customerId = orderRs.getInt("customer_id");
                        Customer customer = CustomerDAO.getCustomerById(customerId);
                        order = new Order(customer, new ArrayList<>());
                        order.setOrderId(orderRs.getInt("order_id"));
                        order.setOrderDate(orderRs.getTimestamp("order_date").toLocalDateTime().toString());
                        order.setTotalPrice(orderRs.getDouble("total_amount"));
                        order.setStatus(orderRs.getString("status"));
                    }
                }
            }

            // Fetch order items
            if (order != null) {
                List<OrderItem> orderItems = OrderItemDAO.getOrderItemsByOrderId(orderId);
                order.setOrderItems(orderItems);
                order.setTotalPrice(OrderItemDAO.calculateTotalPrice(orderItems)); // Recalculate total price if needed
            }
        }
        return order;
    }

    // Retrieve all orders for a specific customer
    public List<Order> getOrdersByCustomerId(int customerId) throws SQLException {
        String sql = "SELECT * FROM ORDERTABLE WHERE customer_id = ?";
        List<Order> orders = new ArrayList<>();

        try (Connection connection = DBconnector.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, customerId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Order order = new Order(
                            CustomerDAO.getCustomerById(rs.getInt("customer_id")), // Fetch Customer using CustomerDAO
                            new ArrayList<>() // Initialize empty list for items
                    );
                    order.setOrderId(rs.getInt("order_id"));
                    order.setOrderDate(rs.getTimestamp("order_date").toLocalDateTime().toString());
                    order.setTotalPrice(rs.getDouble("total_amount"));
                    order.setStatus(rs.getString("status"));
                    orders.add(order);
                }
            }
        }
        return orders;
    }

    // Create a new order with its items
    public boolean createOrder(Order order) throws SQLException {
        String orderSql = "INSERT INTO ORDERTABLE (customer_id, order_date, total_amount, status) VALUES (?, GETDATE(), ?, ?)";
        String itemsSql = "INSERT INTO ORDERITEM (order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";

        try (Connection connection = DBconnector.getConnection()) {
            connection.setAutoCommit(false); // Begin transaction

            // Insert into ORDERTABLE
            try (PreparedStatement orderStmt = connection.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS)) {
                orderStmt.setInt(1, order.getCustomer().getId());
                orderStmt.setDouble(2, order.getTotalPrice());
                orderStmt.setString(3, order.getStatus());
                int affectedRows = orderStmt.executeUpdate();
                if (affectedRows == 0) throw new SQLException("Creating order failed, no rows affected.");

                // Retrieve generated order ID
                try (ResultSet generatedKeys = orderStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        order.setOrderId(generatedKeys.getInt(1));
                    } else {
                        throw new SQLException("Creating order failed, no ID obtained.");
                    }
                }
            }

            // Insert into ORDERITEM
            for (OrderItem item : order.getOrderItems()) {
                ProductDAO.getProductById(item.getProduct().getId()); // Ensure product exists
            }

            try (PreparedStatement itemsStmt = connection.prepareStatement(itemsSql)) {
                for (OrderItem item : order.getOrderItems()) {
                    itemsStmt.setInt(1, order.getOrderId());
                    itemsStmt.setInt(2, item.getProduct().getId());
                    itemsStmt.setInt(3, item.getQuantity());
                    itemsStmt.setDouble(4, item.getPriceAtPurchase());
                    itemsStmt.addBatch();
                }
                itemsStmt.executeBatch();
            }

            connection.commit(); // Commit transaction
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e; // Rollback transaction in case of error
        }
    }

    // Update the status of an order
    public boolean updateOrderStatus(int orderId, String status) throws SQLException {
        String sql = "UPDATE ORDERTABLE SET status = ? WHERE order_id = ?";

        try (Connection connection = DBconnector.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, orderId);

            return stmt.executeUpdate() > 0; // Return true if rows are updated
        }
    }

    // Delete an order and its items
    public boolean deleteOrder(int orderId) throws SQLException {
        String itemsSql = "DELETE FROM ORDERITEM WHERE order_id = ?";
        String orderSql = "DELETE FROM ORDERTABLE WHERE order_id = ?";

        try (Connection connection = DBconnector.getConnection()) {
            connection.setAutoCommit(false); // Begin transaction

            // Delete from ORDERITEM
            try (PreparedStatement itemsStmt = connection.prepareStatement(itemsSql)) {
                itemsStmt.setInt(1, orderId);
                itemsStmt.executeUpdate();
            }

            // Delete from ORDERTABLE
            try (PreparedStatement orderStmt = connection.prepareStatement(orderSql)) {
                orderStmt.setInt(1, orderId);
                orderStmt.executeUpdate();
            }

            connection.commit(); // Commit transaction
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e; // Rollback transaction in case of error
        }
    }
}
