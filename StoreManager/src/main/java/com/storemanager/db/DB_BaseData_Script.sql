USE StoreManagerDB;
-- Inserting Users (Customers and Staff)
INSERT INTO USERS (name, email, password, role, address, phone)
VALUES
('James Walker', 'james.walker@tradetrack.com', 'password123', 'Admin', '45 Ridge Road, City', '1234509876'),  -- Admin
('David Harris', 'david.harris@tradetrack.com', 'password456', 'Manager', '123 Maple Street, City', '2345610987'),  -- Manager
('Michael Brown', 'michael.brown@tradetrack.com', 'password789', 'WarehouseStaff', '78 Oak Avenue, City', '3456721890'),  -- Warehouse Staff
('Emily Johnson', 'emily.johnson@tradetrack.com', 'password321', 'Customer', '56 Birch Boulevard, City', '4567832109'),  -- Customer
('Sophia Martinez', 'sophia.martinez@tradetrack.com', 'password654', 'Customer', '34 Cedar Lane, City', '5678943210');  -- Customer

-- Inserting Customers
INSERT INTO CUSTOMER (user_id)
VALUES
(4), -- Emily Johnson
(5); -- Sophia Martinez

-- Inserting Shopping Carts
INSERT INTO SHOPPINGCART (customer_id)
VALUES
(1), -- Emily Johnson's cart
(2); -- Sophia Martinez's cart

-- Inserting Staff Members
INSERT INTO STAFF (user_id, position)
VALUES
(1, 'Admin'), -- James Walker
(2, 'Manager'), -- David Harris
(3, 'Warehouse Staff'); -- Michael Brown

-- Inserting Categories for Products
INSERT INTO CATEGORY (name)
VALUES
('Electronics'),
('Clothing'),
('Home Appliances');

-- Inserting Products
INSERT INTO PRODUCT (name, brand, description, price, category_id)
VALUES
('Smartphone', 'Samsung', 'Latest model smartphone with 5G', 799.99, 1), -- Electronics
('Jeans', 'Outfitters', 'Classic blue denim jeans', 49.99, 2), -- Clothing
('Blender', 'Philips', 'High-speed kitchen blender', 89.99, 3),
('Laptop', 'Dell', '15-inch laptop with 16GB RAM and 512GB SSD', 999.99, 1), -- Electronics
('T-shirt', 'Nike', 'Cotton T-shirt with modern design', 29.99, 2), -- Clothing
('Microwave', 'LG', 'Compact microwave with multiple functions', 129.99, 3), -- Home Appliances
('Headphones', 'Sony', 'Noise-cancelling wireless headphones', 199.99, 1), -- Electronics
('Jacket', 'Adidas', 'Waterproof winter jacket', 129.99, 2), -- Clothing
('Dishwasher', 'Bosch', 'Energy-efficient dishwasher', 599.99, 3), -- Home Appliances
('Smartwatch', 'Apple', 'Water-resistant smartwatch with fitness tracking', 399.99, 1); -- Electronics

-- Inserting Orders
INSERT INTO ORDERTABLE (customer_id, total_amount, status)
VALUES
(4, 799.99, 'Completed'), -- Order for Emily Johnson
(5, 49.99, 'Completed'); -- Order for Sophia Martinez

-- Inserting Payments (After Orders are created)
INSERT INTO PAYMENT (order_id, amount, status)
VALUES
(1, 799.99, 'Completed'), -- Payment for Emily Johnson's order
(2, 49.99, 'Completed'); -- Payment for Sophia Martinez's order

-- Inserting Order Items
INSERT INTO ORDERITEM (order_id, product_id, quantity, price)
VALUES
(1, 1, 1, 799.99), -- Emily Johnson buys 1 Smartphone
(2, 2, 1, 49.99); -- Sophia Martinez buys 1 Jeans

-- Inserting Cart Items
INSERT INTO CARTITEM (cart_id, product_id, quantity, price)
VALUES
(1, 1, 1, 799.99), -- Emily Johnson adds 1 Smartphone to cart
(2, 2, 2, 49.99); -- Sophia Martinez adds 2 Jeans to cart

-- Inserting Inventory Data
INSERT INTO INVENTORY (product_id, stock_quantity, restock_quantity)
VALUES
(1, 150, 50), -- 150 smartphones in stock, 50 restocked
(2, 200, 100), -- 200 jeans in stock, 100 restocked
(3, 100, 30); -- 100 blenders in stock, 30 restocked

-- Inserting Feedback from Customers
INSERT INTO FEEDBACK (customer_id, comments)
VALUES
(1, 'Great shopping experience!'), -- Feedback from Emily Johnson
(2, 'Fast delivery, great product!'); -- Feedback from Sophia Martinez

-- Inserting Notifications
INSERT INTO NOTIFICATION (message)
VALUES
('Welcome to TradeTrack!'); -- Welcome notification for all users

-- Inserting Notification Recipients
INSERT INTO NOTIFICATION_RECIPIENT (notification_id, user_id, status)
VALUES
(1, 1, 'Unread'), (1, 2, 'Unread'), (1, 3, 'Unread'), (1, 4, 'Unread'), (1, 5, 'Unread');


