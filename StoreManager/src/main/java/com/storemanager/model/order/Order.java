package com.storemanager.model.order;

import com.storemanager.model.users.Customer;
import com.storemanager.model.order.Payment;
import com.storemanager.model.items.Product;
import java.util.List;

public class Order {
    private int orderId;
    private Customer customer; // A reference to the customer who placed the order
    private String orderDate;
    private double totalPrice;
    private List<OrderItem> orderItems; // List of items in this order
    private String status; // Status of the order (e.g., "Pending", "Shipped", "Delivered")
    private Payment payment; // Payment associated with this order

    // Constructor for Order
    public Order(Customer customer, List<OrderItem> orderItems) {
        this.customer = customer;
        this.orderItems = orderItems;
        this.orderDate = getCurrentDate(); // Get today's date
        this.totalPrice = calculateTotalPrice(orderItems); // Calculate total price from order items
        this.status = "Pending"; // Default status when the order is created
        this.payment = null; // Payment will be added later
    }
    public Order(Customer customer, List<OrderItem> orderItems, int orderId, String orderDate, double totalPrice, String status) {
        this.customer = customer;
        this.orderItems = orderItems;
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.totalPrice = totalPrice;
        this.status = status;
        this.payment = null; // Payment will be added later
    }

    // Method to calculate the total price of the order
    private double calculateTotalPrice(List<OrderItem> items) {
        double total = 0;
        for (OrderItem item : items) {
            total += item.getPriceAtPurchase() * item.getQuantity(); // Price * Quantity
        }
        return total;
    }

    // Method to get the current date in YYYY-MM-DD format
    private String getCurrentDate() {
        return java.time.LocalDate.now().toString();
    }

    // Method to confirm the payment for this order
    public void confirmPayment() {
        if (payment != null) {
            payment.confirmPayment(); // Confirm the payment
            this.status = "Paid"; // Update the order status to "Paid"
        }
    }

    // Getters and setters
    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
    public Customer getCustomer() { return customer; }
    public void setCustomer(Customer customer) { this.customer = customer; }
    public List<OrderItem> getOrderItems() { return orderItems; }
    public void setOrderItems(List<OrderItem> orderItems) { this.orderItems = orderItems; }
    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Payment getPayment() { return payment; }
    public void setPayment(Payment payment) { this.payment = payment; }
    public String getOrderDate() { return orderDate; }
    public void setOrderDate(String orderDate) { this.orderDate = orderDate; }

    // ToString method to represent the order as a string
    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", customer=" + (customer != null ? customer.getUsername() : "null") +
                ", orderDate='" + orderDate + '\'' +
                ", totalPrice=" + totalPrice +
                ", status='" + status + '\'' +
                ", payment=" + payment +
                ", orderItems=" + orderItems +
                '}';
    }


}
