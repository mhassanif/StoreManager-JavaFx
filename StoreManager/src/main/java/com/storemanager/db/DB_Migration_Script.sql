-- Create USERS Table
CREATE TABLE USERS (
    user_id INT IDENTITY(1,1) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL, -- "Customer" or "Staff"
    address TEXT,
    phone VARCHAR(15),
);

-- Create SHOPPINGCART Table
CREATE TABLE SHOPPINGCART (
    cart_id INT IDENTITY(1,1) PRIMARY KEY,
);

-- Create CUSTOMER Table
CREATE TABLE CUSTOMER (
    customer_id INT IDENTITY(1,1) PRIMARY KEY,
    user_id INT UNIQUE NOT NULL,
    cart_id INT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES USERS(user_id),
    FOREIGN KEY (cart_id) REFERENCES SHOPPINGCART(cart_id)
);

-- Create STAFF Table
CREATE TABLE STAFF (
    staff_id INT IDENTITY(1,1) PRIMARY KEY,
    user_id INT UNIQUE NOT NULL,
    position VARCHAR(50),
    FOREIGN KEY (user_id) REFERENCES USERS(user_id)
);

-- Create CATEGORY Table
CREATE TABLE CATEGORY (
    category_id INT IDENTITY(1,1) PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

-- Create PRODUCT Table
CREATE TABLE PRODUCT (
    product_id INT IDENTITY(1,1) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
	brand VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    category_id INT,
    FOREIGN KEY (category_id) REFERENCES CATEGORY(category_id)
);

-- Create PAYMENT Table
CREATE TABLE PAYMENT (
    payment_id INT IDENTITY(1,1) PRIMARY KEY,
    amount DECIMAL(10, 2) NOT NULL,
    date DATETIME DEFAULT GETDATE(),
    status VARCHAR(10) DEFAULT 'Pending' -- "Pending", "Completed", "Failed"
);

-- Create ORDERTABLE Table
CREATE TABLE ORDERTABLE (
    order_id INT IDENTITY(1,1) PRIMARY KEY,
    customer_id INT NOT NULL,
    payment_id INT, -- References Payment table
    order_date DATETIME DEFAULT GETDATE(),
    total_amount DECIMAL(10, 2),
    status VARCHAR(50),
    FOREIGN KEY (customer_id) REFERENCES CUSTOMER(customer_id),
    FOREIGN KEY (payment_id) REFERENCES PAYMENT(payment_id)
);

-- Create ORDERLINEITEM Table
CREATE TABLE ORDERITEM (
    order_item_id INT IDENTITY(1,1) PRIMARY KEY,
    order_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (order_id) REFERENCES ORDERTABLE(order_id),
    FOREIGN KEY (product_id) REFERENCES PRODUCT(product_id)
);

-- Create CARTLINEITEM Table
CREATE TABLE CARTITEM (
    cart_item_id INT IDENTITY(1,1) PRIMARY KEY,
    cart_id INT NOT NULL,
    product_id INT NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (cart_id) REFERENCES SHOPPINGCART(cart_id),
    FOREIGN KEY (product_id) REFERENCES PRODUCT(product_id)
);

-- Create INVENTORY Table
CREATE TABLE INVENTORY (
    inventory_id INT IDENTITY(1,1) PRIMARY KEY,
    product_id INT NOT NULL,
	stock_quantity INT NOT NULL,
    restock_quantity INT NOT NULL,
    restock_date DATETIME DEFAULT GETDATE(),
    FOREIGN KEY (product_id) REFERENCES PRODUCT(product_id)
);

-- Create FEEDBACK Table
CREATE TABLE FEEDBACK (
    feedback_id INT IDENTITY(1,1) PRIMARY KEY,
    customer_id INT NOT NULL,
    comments TEXT,
    FOREIGN KEY (customer_id) REFERENCES CUSTOMER(customer_id)
);

-- Updated NOTIFICATION Table
CREATE TABLE NOTIFICATION (
    notification_id INT IDENTITY(1,1) PRIMARY KEY,
    message TEXT NOT NULL,
    date DATETIME DEFAULT GETDATE()
);


-- Junction Table for Notification Recipients
CREATE TABLE NOTIFICATION_RECIPIENT (
    notification_id INT NOT NULL,
    user_id INT NOT NULL,
    status VARCHAR(10) DEFAULT 'Unread', -- "Unread" or "Read"
    PRIMARY KEY (notification_id, user_id),
    FOREIGN KEY (notification_id) REFERENCES NOTIFICATION(notification_id),
    FOREIGN KEY (user_id) REFERENCES USERS(user_id)
);
