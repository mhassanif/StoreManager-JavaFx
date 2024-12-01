-- USERS Table
INSERT INTO USERS (name, email, password, role, address, phone) VALUES
('Shahmeer Ahmed', 'shahmeer.ahmed@gmail.com', 'password123', 'Staff', '123 Admin St, Karachi', '03001234567'),
('Muhammad Danish', 'danish.manager@gmail.com', 'password123', 'Staff', '456 Manager Ave, Lahore', '03012345678'),
('Hassan Imran', 'hassan.warehouse@gmail.com', 'password123', 'Staff', '789 Warehouse Rd, Islamabad', '03023456789'),
('Ali Raza', 'ali.raza@gmail.com', 'password123', 'Customer', '101 Customer Blvd, Karachi', '03034567890'),
('Sarah Khan', 'sarah.khan@gmail.com', 'password123', 'Customer', '202 Customer Lane, Lahore', '03045678901'),
('Hamza Malik', 'hamza.malik@gmail.com', 'password123', 'Customer', '303 Malik St, Karachi', '03056789012'),
('Ayesha Siddiqui', 'ayesha.siddiqui@gmail.com', 'password123', 'Customer', '404 Siddiqui Ave, Islamabad', '03067890123'),
('Zain Ul Abideen', 'zain.abideen@gmail.com', 'password123', 'Customer', '505 Abideen Rd, Lahore', '03078901234'),
('Fatima Noor', 'fatima.noor@gmail.com', 'password123', 'Customer', '606 Noor Blvd, Karachi', '03089012345'),
('Usman Tariq', 'usman.tariq@gmail.com', 'password123', 'Customer', '707 Tariq Lane, Lahore', '03090123456'),
('Mariam Ashraf', 'mariam.ashraf@gmail.com', 'password123', 'Customer', '808 Ashraf Rd, Islamabad', '03101234567'),
('Bilal Ahmed', 'bilal.ahmed@gmail.com', 'password123', 'Customer', '909 Ahmed St, Karachi', '03112345678'),
('Anam Sheikh', 'anam.sheikh@gmail.com', 'password123', 'Customer', '1010 Sheikh Blvd, Lahore', '03123456789'),
('Imran Qureshi', 'imran.qureshi@gmail.com', 'password123', 'Customer', '1111 Qureshi Ave, Islamabad', '03134567890'),
('Sana Zafar', 'sana.zafar@gmail.com', 'password123', 'Customer', '1212 Zafar Lane, Karachi', '03145678901');

-- CUSTOMER Table
INSERT INTO CUSTOMER (user_id, balance) VALUES
(4, 100.00),
(5, 200.00),
(6, 150.00),
(7, 250.00),
(8, 300.00),
(9, 350.00),
(10, 400.00),
(11, 450.00),
(12, 500.00),
(13, 550.00),
(14, 600.00),
(15, 650.00);

-- STAFF Table
INSERT INTO STAFF (user_id, position) VALUES
(1, 'Admin'),
(2, 'Manager'),
(3, 'Warehouse Staff');

-- CATEGORY Table
INSERT INTO CATEGORY (name) VALUES
('Electronics'),
('Clothing'),
('Groceries'),
('Books'),
('Toys');

-- PRODUCT Table
INSERT INTO PRODUCT (name, brand, description, price, category_id, url) VALUES
('Smartphone', 'Samsung', 'Latest smartphone with AMOLED display', 499.99, 1, 'http://example.com/smartphone'),
('Laptop', 'Dell', 'High-performance laptop for work and play', 899.99, 1, 'http://example.com/laptop'),
('T-Shirt', 'Nike', 'Comfortable cotton t-shirt', 29.99, 2, 'http://example.com/tshirt'),
('Jeans', 'Levis', 'Classic blue jeans', 59.99, 2, 'http://example.com/jeans'),
('Milk', 'Nestle', '1-liter milk pack', 2.99, 3, 'http://example.com/milk'),
('Bread', 'Sunrise', 'Fresh whole grain bread', 1.99, 3, 'http://example.com/bread'),
('Novel', 'Penguin', 'Best-selling fiction novel', 19.99, 4, 'http://example.com/novel'),
('Action Figure', 'Hasbro', 'Popular superhero action figure', 24.99, 5, 'http://example.com/actionfigure'),
('Puzzle', 'Ravensburger', '1000-piece jigsaw puzzle', 14.99, 5, 'http://example.com/puzzle');

-- SHOPPINGCART Table
INSERT INTO SHOPPINGCART (customer_id) VALUES
(4),
(5),
(6),
(7),
(8);

-- CARTITEM Table
INSERT INTO CARTITEM (cart_id, product_id, quantity, price) VALUES
(1, 1, 1, 499.99),
(2, 3, 2, 29.99),
(3, 5, 1, 2.99),
(4, 7, 1, 19.99),
(5, 9, 2, 24.99);

-- ORDERTABLE Table
INSERT INTO ORDERTABLE (customer_id, total_amount, status) VALUES
(4, 100.00, 'Completed'),
(5, 200.00, 'Pending'),
(6, 150.00, 'Cancelled'),
(7, 300.00, 'Completed'),
(8, 400.00, 'Completed');

-- PAYMENT Table
INSERT INTO PAYMENT (order_id, amount, status) VALUES
(1, 100.00, 'Completed'),
(2, 200.00, 'Pending'),
(3, 150.00, 'Failed'),
(4, 300.00, 'Completed'),
(5, 400.00, 'Completed');

-- ORDERITEM Table
INSERT INTO ORDERITEM (order_id, product_id, quantity, price) VALUES
(1, 1, 1, 499.99),
(2, 3, 2, 29.99),
(3, 5, 1, 2.99),
(4, 7, 1, 19.99),
(5, 9, 2, 24.99);

-- INVENTORY Table
INSERT INTO INVENTORY (product_id, stock_quantity, restock_quantity, restock_date) VALUES
(1, 50, 10, GETDATE()),
(2, 30, 5, GETDATE()),
(3, 100, 20, GETDATE()),
(4, 75, 15, GETDATE()),
(5, 200, 50, GETDATE()),
(6, 150, 25, GETDATE()),
(7, 60, 10, GETDATE()),
(8, 40, 10, GETDATE()),
(9, 90, 30, GETDATE());

-- FEEDBACK Table
INSERT INTO FEEDBACK (customer_id, comments) VALUES
(4, 'Great service!'),
(5, 'Fast delivery!'),
(6, 'Good quality products.'),
(7, 'Highly recommend this store.'),
(8, 'Very satisfied with the purchase.');

-- NOTIFICATION Table
INSERT INTO NOTIFICATION (message) VALUES
('Welcome to TradeTrack');

-- NOTIFICATION_RECIPIENT Table
INSERT INTO NOTIFICATION_RECIPIENT (notification_id, user_id) VALUES
(1, 1), (1, 2), (1, 3), (1, 4), (1, 5), (1, 6), (1, 7), (1, 8), (1, 9), (1, 10),
(1, 11), (1, 12), (1, 13), (1, 14), (1, 15);

-- Adding low stock notifications to the NOTIFICATION Table
INSERT INTO NOTIFICATION (message) VALUES
('Restock needed for product: 2'),
('Restock needed for product: 5'),
('Restock needed for product: 6');

INSERT INTO NOTIFICATION_RECIPIENT (notification_id, user_id, status) VALUES
(2, 1, 'Unread'),
(2, 2, 'Unread'),
(2, 3, 'Unread');

-- Notification 3: Low stock for Laptop
INSERT INTO NOTIFICATION_RECIPIENT (notification_id, user_id, status) VALUES
(3, 1, 'Unread'),
(3, 2, 'Unread'),
(3, 3, 'Unread');

-- Notification 4: Low stock for Action Figure
INSERT INTO NOTIFICATION_RECIPIENT (notification_id, user_id, status) VALUES
(4, 1, 'Unread'),
(4, 2, 'Unread'),
(4, 3, 'Unread');
