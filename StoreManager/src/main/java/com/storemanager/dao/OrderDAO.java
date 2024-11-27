package com.storemanager.dao;

import com.storemanager.model.order.Order;
import com.storemanager.model.order.OrderItem;
import com.storemanager.model.order.Payment;
import com.storemanager.model.users.Customer;
import com.storemanager.db.DBconnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {

    // Method to fetch payment information for an order by order_id
    public static Payment getPaymentByOrderId(int orderId) throws SQLException {
        String sql = "SELECT payment_id, amount, status, date FROM PAYMENT WHERE order_id = ?";
        try (Connection conn = DBconnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, orderId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Payment object created and populated via setters
                    Payment payment = new Payment();
                    payment.setId(rs.getInt("payment_id"));
                    payment.setAmount(rs.getDouble("amount"));
                    //payment.setType(rs.getString("type"));
                    payment.setStatus(rs.getString("status"));
                    payment.setDate(rs.getString("date"));
                    return payment;
                }
            }
        }
        return null; // Return null if no payment found
    }

    // Method to fetch all orders along with payment information
    public static List<Order> getAllOrders() throws SQLException {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT o.order_id, o.customer_id, o.order_date, o.total_amount, o.status, " +
                "p.payment_id, p.amount, p.status AS payment_status, p.date AS payment_date " +
                "FROM ORDERTABLE o " +
                "INNER JOIN PAYMENT p ON o.order_id = p.order_id";  // Using LEFT JOIN to get payment info if exists

        try (Connection conn = DBconnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                // Order object created and populated via setters
                Order order = new Order();
                order.setOrderId(rs.getInt("order_id"));
                order.setCustomer(CustomerDAO.getCustomerById(rs.getInt("customer_id"))); // Fetch customer details
                order.setOrderDate(rs.getString("order_date"));
                order.setTotalPrice(rs.getDouble("total_amount"));
                order.setStatus(rs.getString("status"));

                // Fetch payment details directly from the result set
                Payment payment = new Payment();
                payment.setId(rs.getInt("payment_id"));
                payment.setAmount(rs.getDouble("amount"));
                payment.setStatus(rs.getString("payment_status"));
                payment.setDate(rs.getString("payment_date"));

                // Set the payment for the order
                order.setPayment(payment);

                orders.add(order);
            }
        }
        return orders;
    }


    // Method to fetch order by ID along with payment information
    public static Order getOrderById(int orderId) throws SQLException {
        String sql = "SELECT o.customer_id, o.order_date, o.total_amount, o.status FROM ORDERTABLE o WHERE o.order_id = ?";
        try (Connection conn = DBconnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, orderId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Order object created and populated via setters
                    Order order = new Order();
                    order.setOrderId(orderId);
                    order.setCustomer(CustomerDAO.getCustomerById(rs.getInt("customer_id"))); // Fetch customer details
                    order.setOrderDate(rs.getString("order_date"));
                    order.setTotalPrice(rs.getDouble("total_amount"));
                    order.setStatus(rs.getString("status"));

                    // Fetch payment details using the new method
                    Payment payment = getPaymentByOrderId(orderId);
                    order.setPayment(payment); // Set the payment for the order

                    return order;
                }
            }
        }
        return null; // Return null if order not found
    }

    // Method to fetch orders by customer ID along with payment information
    public static List<Order> getOrdersByCustomerId(int customerId) throws SQLException {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM ORDERTABLE WHERE customer_id = ?";

        try (Connection conn = DBconnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // Order object created and populated via setters
                    Order order = new Order();
                    order.setOrderId(rs.getInt("order_id"));
                    order.setCustomer(CustomerDAO.getCustomerById(customerId)); // Fetch customer details
                    order.setOrderDate(rs.getString("order_date"));
                    order.setTotalPrice(rs.getDouble("total_amount"));
                    order.setStatus(rs.getString("status"));

                    // Fetch payment details using the new method
                    Payment payment = getPaymentByOrderId(order.getOrderId());
                    order.setPayment(payment); // Set the payment for the order

                    orders.add(order);
                }
            }
        }
        return orders;
    }

    // Method to fetch all orders for a given status along with payment information
    public static List<Order> getOrdersByStatus(String status) throws SQLException {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT o.order_id, o.customer_id, o.order_date, o.total_amount FROM ORDERTABLE o WHERE o.status = ?";

        try (Connection conn = DBconnector.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, status);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    // Order object created and populated via setters
                    Order order = new Order();
                    order.setOrderId(rs.getInt("order_id"));
                    order.setCustomer(CustomerDAO.getCustomerById(rs.getInt("customer_id"))); // Fetch customer details
                    order.setOrderDate(rs.getString("order_date"));
                    order.setTotalPrice(rs.getDouble("total_amount"));
                    order.setStatus(rs.getString("status"));

                    // Fetch payment details using the new method
                    Payment payment = getPaymentByOrderId(order.getOrderId());
                    order.setPayment(payment); // Set the payment for the order

                    orders.add(order);
                }
            }
        }
        return orders;
    }

    // Static method to create a new order with its items
    public static boolean createOrder(Order order) throws SQLException {
        String orderSql = "INSERT INTO ORDERTABLE (customer_id, order_date, total_amount, status) VALUES (?, GETDATE(), ?, ?)";
        String itemsSql = "INSERT INTO ORDERITEM (order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";

        try (Connection connection = DBconnector.getConnection()) {
            connection.setAutoCommit(false); // Begin transaction

            // Insert into ORDERTABLE
            try (PreparedStatement orderStmt = connection.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS)) {
                orderStmt.setInt(1, order.getCustomer().getCustomerId());
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

    // Static method to update the status of an order
    public static boolean updateOrderStatus(int orderId, String status) throws SQLException {
        String sql = "UPDATE ORDERTABLE SET status = ? WHERE order_id = ?";

        try (Connection connection = DBconnector.getConnection();
             PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, orderId);

            return stmt.executeUpdate() > 0; // Return true if rows are updated
        }
    }

    // Static method to delete an order and its items and payment
    public static boolean deleteOrder(int orderId) throws SQLException {
        String paymentSql = "DELETE FROM PAYMENT WHERE order_id = ?";
        String itemsSql = "DELETE FROM ORDERITEM WHERE order_id = ?";
        String orderSql = "DELETE FROM ORDERTABLE WHERE order_id = ?";

        try (Connection connection = DBconnector.getConnection()) {
            connection.setAutoCommit(false); // Begin transaction

            // Delete from PAYMENT
            try (PreparedStatement paymentStmt = connection.prepareStatement(paymentSql)) {
                paymentStmt.setInt(1, orderId);
                paymentStmt.executeUpdate();
            }

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

    public static boolean deleteOrderByCustomer(int customerId) throws SQLException {
        String fetchOrdersSql = "SELECT order_id FROM ORDERTABLE WHERE customer_id = ?";
        String paymentSql = "DELETE FROM PAYMENT WHERE order_id = ?";
        String orderSql = "DELETE FROM ORDERTABLE WHERE order_id = ?";

        try (Connection connection = DBconnector.getConnection()) {
            connection.setAutoCommit(false); // Begin transaction

            // Fetch all order IDs for the given customer
            List<Integer> orderIds = new ArrayList<>();
            try (PreparedStatement fetchOrdersStmt = connection.prepareStatement(fetchOrdersSql)) {
                fetchOrdersStmt.setInt(1, customerId);
                try (ResultSet rs = fetchOrdersStmt.executeQuery()) {
                    while (rs.next()) {
                        orderIds.add(rs.getInt("order_id"));
                    }
                }
            }

            // Delete order items for each order
            for (int orderId : orderIds) {
                OrderItemDAO.deleteByOrderId(orderId); // Call the delete method in OrderItemDAO
            }

            // Delete associated payments and orders for each order
            for (int orderId : orderIds) {
                // Delete from PAYMENT
                try (PreparedStatement paymentStmt = connection.prepareStatement(paymentSql)) {
                    paymentStmt.setInt(1, orderId);
                    paymentStmt.executeUpdate();
                }

                // Delete from ORDERTABLE
                try (PreparedStatement orderStmt = connection.prepareStatement(orderSql)) {
                    orderStmt.setInt(1, orderId);
                    orderStmt.executeUpdate();
                }
            }

            connection.commit(); // Commit transaction
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            throw e; // Rollback transaction in case of error
        }
    }
}
