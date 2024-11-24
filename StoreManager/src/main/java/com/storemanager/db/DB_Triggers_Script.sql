
-- Trigger for USERS Table: Automatically create role-based entries in CUSTOMER or STAFF
CREATE TRIGGER trg_AfterInsert_USERS
ON USERS
AFTER INSERT
AS
BEGIN
    -- Automatically add record to CUSTOMER if role is 'Customer'
    INSERT INTO CUSTOMER (user_id)
    SELECT user_id FROM inserted WHERE role = 'Customer';

    -- Automatically add record to STAFF if role is 'Staff' --> usually added manually
    --INSERT INTO STAFF (user_id, position)
    --SELECT user_id, NULL FROM inserted WHERE role = 'Staff';
END;
GO

-- Trigger for CUSTOMER Table: Cascade delete related records
CREATE TRIGGER trg_AfterDelete_CUSTOMER
ON CUSTOMER
AFTER DELETE
AS
BEGIN
    -- Delete related SHOPPINGCART records
    DELETE FROM SHOPPINGCART WHERE customer_id IN (SELECT customer_id FROM deleted);

    -- Delete related ORDERTABLE records
    DELETE FROM ORDERTABLE WHERE customer_id IN (SELECT customer_id FROM deleted);

    -- Delete related FEEDBACK records
    DELETE FROM FEEDBACK WHERE customer_id IN (SELECT customer_id FROM deleted);

    -- Delete corresponding entry from USERS table as well if needed
END;
GO


-- Trigger for SHOPPINGCART Table: Delete related CARTITEM records
CREATE TRIGGER trg_AfterDelete_SHOPPINGCART
ON SHOPPINGCART
AFTER DELETE
AS
BEGIN
    DELETE FROM CARTITEM WHERE cart_id IN (SELECT cart_id FROM deleted);
END;
GO

-- Trigger for PRODUCT Table: Cascade delete and auto-create INVENTORY entries
CREATE TRIGGER trg_AfterDelete_PRODUCT
ON PRODUCT
AFTER DELETE
AS
BEGIN
    -- Delete related INVENTORY records
    DELETE FROM INVENTORY WHERE product_id IN (SELECT product_id FROM deleted);

    -- Delete related CARTITEM records
    DELETE FROM CARTITEM WHERE product_id IN (SELECT product_id FROM deleted);

    -- Delete related ORDERITEM records
    DELETE FROM ORDERITEM WHERE product_id IN (SELECT product_id FROM deleted);
END;
GO

-- add entry for new product to the invetoty table
CREATE TRIGGER trg_AfterInsert_PRODUCT
ON PRODUCT
AFTER INSERT
AS
BEGIN
    -- Automatically create an INVENTORY entry for the new product
    INSERT INTO INVENTORY (product_id, stock_quantity, restock_quantity)
    SELECT product_id, 0, 10 FROM inserted;
END;
GO

-- Updated Trigger for ORDERTABLE Table: Auto-create PAYMENT, notify Manager and Customer

CREATE TRIGGER trg_AfterInsert_ORDERTABLE
ON ORDERTABLE
AFTER INSERT
AS
BEGIN
    -- Automatically create a PAYMENT record for the new order
    INSERT INTO PAYMENT (order_id, amount)
    SELECT order_id, total_amount FROM inserted;

    -- Notify the Manager about the new order
    INSERT INTO NOTIFICATION (message)
    SELECT 'New order placed: Order ID ' + CAST(order_id AS VARCHAR)
    FROM inserted;

    -- Assign the notification to staff members with position 'Manager'
    INSERT INTO NOTIFICATION_RECIPIENT (notification_id, user_id)
    SELECT n.notification_id, s.user_id
    FROM NOTIFICATION n
    CROSS JOIN STAFF s
    WHERE n.notification_id NOT IN (
        SELECT notification_id
        FROM NOTIFICATION_RECIPIENT
    )
    AND s.position = 'Manager';

    -- Notify the Customer about the order confirmation
    INSERT INTO NOTIFICATION (message)
    SELECT 'Your order has been confirmed: Order ID ' + CAST(order_id AS VARCHAR)
    FROM inserted;

    -- Assign the notification to the customer who placed the order
    INSERT INTO NOTIFICATION_RECIPIENT (notification_id, user_id)
    SELECT n.notification_id, u.user_id
    FROM NOTIFICATION n
    INNER JOIN USERS u ON u.user_id IN (SELECT user_id FROM inserted)
    WHERE n.notification_id NOT IN (
        SELECT notification_id
        FROM NOTIFICATION_RECIPIENT
    )
    AND u.role = 'Customer';
END;
GO


CREATE TRIGGER trg_AfterDelete_ORDERTABLE
ON ORDERTABLE
AFTER DELETE
AS
BEGIN
    -- Delete related ORDERITEM records
    DELETE FROM ORDERITEM WHERE order_id IN (SELECT order_id FROM deleted);

    -- Delete related PAYMENT records
    DELETE FROM PAYMENT WHERE order_id IN (SELECT order_id FROM deleted);
END;
GO

-- Trigger for PAYMENT Table: Update ORDERTABLE status
CREATE TRIGGER trg_AfterUpdate_PAYMENT
ON PAYMENT
AFTER UPDATE
AS
BEGIN
    -- Update the status of the corresponding order if payment is completed
    UPDATE ORDERTABLE
    SET status = 'Completed'
    WHERE order_id IN (SELECT order_id FROM inserted WHERE status = 'Completed');
END;
GO

-- Trigger for ORDERITEM Table: Update INVENTORY and send notification if stock is low
CREATE TRIGGER trg_AfterInsertOrUpdate_ORDERITEM
ON ORDERITEM
AFTER INSERT, UPDATE
AS
BEGIN
    -- Deduct quantity from INVENTORY
    UPDATE INVENTORY
    SET stock_quantity = stock_quantity - i.quantity
    FROM INVENTORY inv
    INNER JOIN inserted i ON inv.product_id = i.product_id;

    -- Send notification if stock is low
    INSERT INTO NOTIFICATION (message)
    SELECT 'Restock needed for product: ' + CAST(product_id AS VARCHAR)
    FROM INVENTORY
    WHERE stock_quantity < restock_quantity;
END;
GO



-- Trigger for FEEDBACK Table: Notify admin/staff
-- Trigger to notify admin and manager when new feedback is added
CREATE TRIGGER trg_AfterInsert_FEEDBACK
ON FEEDBACK
AFTER INSERT
AS
BEGIN
    -- Insert a notification about the new feedback
    INSERT INTO NOTIFICATION (message)
    SELECT 'New feedback from customer: ' + CAST(customer_id AS VARCHAR)
    FROM inserted;

    -- Assign the notification to staff members with positions 'Admin' and 'Manager'
    INSERT INTO NOTIFICATION_RECIPIENT (notification_id, user_id)
    SELECT n.notification_id, s.user_id
    FROM NOTIFICATION n
    CROSS JOIN STAFF s
    WHERE n.notification_id NOT IN (
        SELECT notification_id
        FROM NOTIFICATION_RECIPIENT
    )
    AND s.position IN ('Admin', 'Manager');
END;
GO


