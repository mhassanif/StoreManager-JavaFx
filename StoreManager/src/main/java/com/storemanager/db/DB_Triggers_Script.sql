-- Trigger for CUSTOMER Table: Cascade delete related records before deleting CUSTOMER
CREATE TRIGGER trg_BeforeDelete_CUSTOMER
ON CUSTOMER
INSTEAD OF DELETE
AS
BEGIN
    -- Delete related NOTIFICATION_RECIPIENT and NOTIFICATION records
    DELETE FROM NOTIFICATION_RECIPIENT WHERE user_id IN (SELECT user_id FROM CUSTOMER WHERE customer_id IN (SELECT customer_id FROM deleted));

    -- Delete related FEEDBACK records
    DELETE FROM FEEDBACK WHERE customer_id IN (SELECT customer_id FROM deleted);

    -- Delete related SHOPPINGCART records (CARTITEM deletion is handled by its trigger)
    DELETE FROM SHOPPINGCART WHERE customer_id IN (SELECT customer_id FROM deleted);

    -- Delete related ORDERTABLE records (ORDERITEM deletion is handled by its trigger)
    DELETE FROM ORDERTABLE WHERE customer_id IN (SELECT customer_id FROM deleted);

    -- Now delete the CUSTOMER record
    DELETE FROM CUSTOMER WHERE customer_id IN (SELECT customer_id FROM deleted);
END;
GO




-- Trigger for SHOPPINGCART Table: Delete related CARTITEM records before deleting SHOPPINGCART
CREATE TRIGGER trg_BeforeDelete_SHOPPINGCART
ON SHOPPINGCART
INSTEAD OF DELETE
AS
BEGIN
    -- Delete related CARTITEM records
    DELETE FROM CARTITEM WHERE cart_id IN (SELECT cart_id FROM deleted);

    -- Now delete from SHOPPINGCART
    DELETE FROM SHOPPINGCART WHERE cart_id IN (SELECT cart_id FROM deleted);
END;
GO

-- Trigger for ORDERTABLE Table: Delete related ORDERITEM records before deleting ORDERTABLE
CREATE TRIGGER trg_BeforeDelete_ORDERTABLE
ON ORDERTABLE
INSTEAD OF DELETE
AS
BEGIN
    -- Delete related ORDERITEM records
    DELETE FROM ORDERITEM WHERE order_id IN (SELECT order_id FROM deleted);

    -- Delete related PAYMENT records
    DELETE FROM PAYMENT WHERE order_id IN (SELECT order_id FROM deleted);

    -- Now delete from ORDERTABLE
    DELETE FROM ORDERTABLE WHERE order_id IN (SELECT order_id FROM deleted);
END;
GO


-- Trigger for PRODUCT Table: Cascade delete related records before deleting PRODUCT
CREATE TRIGGER trg_BeforeDelete_PRODUCT
ON PRODUCT
INSTEAD OF DELETE
AS
BEGIN
    -- Delete related INVENTORY records
    DELETE FROM INVENTORY WHERE product_id IN (SELECT product_id FROM deleted);

    -- Delete related CARTITEM records
    DELETE FROM CARTITEM WHERE product_id IN (SELECT product_id FROM deleted);

    -- Delete related ORDERITEM records
    DELETE FROM ORDERITEM WHERE product_id IN (SELECT product_id FROM deleted);

    -- Now delete the PRODUCT record
    DELETE FROM PRODUCT WHERE product_id IN (SELECT product_id FROM deleted);
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

-- Trigger for ORDERTABLE Table: Auto-create PAYMENT, notify Manager and Customer
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
    SELECT 'New order placed: Order ID ' + CAST(order_id AS VARCHAR(255))
    FROM inserted;

    -- Assign the notification to staff members with position 'Manager'
    INSERT INTO NOTIFICATION_RECIPIENT (notification_id, user_id)
    SELECT n.notification_id, s.user_id
    FROM NOTIFICATION n
    INNER JOIN STAFF s ON s.position = 'Manager'
    INNER JOIN inserted i ON i.order_id = n.order_id
    WHERE n.message = 'New order placed: Order ID ' + CAST(i.order_id AS VARCHAR(255));

    -- Notify the Customer about the order confirmation
    INSERT INTO NOTIFICATION (message)
    SELECT 'Your order has been confirmed: Order ID ' + CAST(order_id AS VARCHAR(255))
    FROM inserted;

    -- Assign the notification to the customer who placed the order
    INSERT INTO NOTIFICATION_RECIPIENT (notification_id, user_id)
    SELECT n.notification_id, u.user_id
    FROM NOTIFICATION n
    INNER JOIN USERS u ON u.user_id IN (SELECT user_id FROM inserted)
    WHERE n.message = 'Your order has been confirmed: Order ID ' + CAST(i.order_id AS VARCHAR(255))
    AND u.role = 'Customer';
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


-- Trigger for ORDERITEM Table: Update INVENTORY without sending notifications
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
END;
GO

-- Trigger for INVENTORY Table: Notify if stock is low
CREATE TRIGGER trg_AfterUpdate_INVENTORY
ON INVENTORY
AFTER UPDATE
AS
BEGIN
    -- Notify if stock is low for any product
    INSERT INTO NOTIFICATION (message)
    SELECT 'Restock needed for product: ' + CAST(product_id AS VARCHAR)
    FROM inserted
    WHERE stock_quantity < restock_quantity;

    -- Notify Managers and Warehouse Staff
    INSERT INTO NOTIFICATION_RECIPIENT (notification_id, user_id)
    SELECT n.notification_id, s.user_id
    FROM NOTIFICATION n
    INNER JOIN STAFF s ON s.position IN ('Manager', 'Warehouse Staff')
    WHERE n.notification_id IN (SELECT notification_id FROM inserted);
END;
GO



