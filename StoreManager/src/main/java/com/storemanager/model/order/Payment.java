package com.storemanager.model.order;

import java.time.LocalDate;

public class Payment {
    private int id;
    private double amount;
    private String type;    // e.g., "Cash", "Card", "PayPal"
    private String status; // e.g., "Pending", "Completed", "Failed"
    private String date;
    private String paymentDetails;
    private boolean paymentConfirmation;
    private Order order;  // Reference to the associated Order

    // Constructor to initialize Payment object
    public Payment(int id, double amount, String type, Order order) {
        this.id = id;
        this.amount = amount;
        this.type = type;
        this.status = "Pending"; // Default status when payment is created
        this.date = LocalDate.now().toString();
        this.paymentConfirmation = false; // Initially set to false
        this.order = order; // Associate the payment with the order
    }

    public Payment() {
        this.id = -1;
        this.amount = -1.0;
        this.type = "";
        this.status = ""; // Default status when payment is created
        this.date = LocalDate.now().toString();
        this.paymentConfirmation = false; // Initially set to false
        this.order = null; // Associate the payment with the order
    }


    // Method to confirm payment
    public void confirmPayment() {
        this.status = "Completed";
        this.paymentConfirmation = true;
    }

    // Method to handle payment failure
    public void failPayment() {
        this.status = "Failed";
        this.paymentConfirmation = false;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }
    public String getPaymentDetails() { return paymentDetails; }
    public void setPaymentDetails(String paymentDetails) { this.paymentDetails = paymentDetails; }
    public boolean isPaymentConfirmation() { return paymentConfirmation; }
    public void setPaymentConfirmation(boolean paymentConfirmation) { this.paymentConfirmation = paymentConfirmation; }
    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    @Override
    public String toString() {
        return "Payment{" +
                "id=" + id +
                ", amount=" + amount +
                ", type='" + type + '\'' +
                ", status='" + status + '\'' +
                ", date='" + date + '\'' +
                ", paymentConfirmation=" + paymentConfirmation +
                ", order=" + order.getOrderId() +
                '}';
    }
}
