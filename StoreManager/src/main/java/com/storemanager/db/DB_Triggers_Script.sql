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
    -- Insert a notification if stock is low for any product
    INSERT INTO NOTIFICATION (message)
    SELECT 'Restock needed for product: ' + CAST(product_id AS VARCHAR)
    FROM inserted
    WHERE stock_quantity < restock_quantity
    AND NOT EXISTS (
        -- Avoid inserting the same notification if it's already present
        SELECT 1
        FROM NOTIFICATION n
        WHERE CAST(n.message AS VARCHAR(MAX)) = 'Restock needed for product: ' + CAST(inserted.product_id AS VARCHAR)
    );

    -- Insert into NOTIFICATION_RECIPIENT table, avoiding duplicate entries
    INSERT INTO NOTIFICATION_RECIPIENT (notification_id, user_id)
    SELECT n.notification_id, s.user_id
    FROM NOTIFICATION n
    INNER JOIN STAFF s ON s.position IN ('Manager', 'Warehouse Staff')
    WHERE EXISTS (
        -- Match the notification_id with the relevant product notifications
        SELECT 1
        FROM inserted i
        WHERE i.product_id = CAST(SUBSTRING(CAST(n.message AS VARCHAR(MAX)), 25, LEN(CAST(n.message AS VARCHAR(MAX)))) AS INT)
    )
    AND NOT EXISTS (
        -- Prevent duplicate user_id and notification_id combinations in NOTIFICATION_RECIPIENT
        SELECT 1
        FROM NOTIFICATION_RECIPIENT nr
        WHERE nr.notification_id = n.notification_id
        AND nr.user_id = s.user_id
    );
END;
GO






DECLARE @sql NVARCHAR(MAX) = '';

-- Generate DROP TRIGGER statements for all triggers in the current database
SELECT @sql = @sql + 'DROP TRIGGER IF EXISTS ' + QUOTENAME(name) + ';' + CHAR(13)
FROM sys.triggers
WHERE parent_id = OBJECT_ID('dbo.ORDERTABLE') OR parent_id = OBJECT_ID('dbo.PRODUCT')
   OR parent_id = OBJECT_ID('dbo.CUSTOMER') OR parent_id = OBJECT_ID('dbo.SHOPPINGCART')
   OR parent_id = OBJECT_ID('dbo.ORDERITEM') OR parent_id = OBJECT_ID('dbo.INVENTORY')
   OR parent_id = OBJECT_ID('dbo.PAYMENT');

-- Execute the dynamic SQL to drop the triggers
EXEC sp_executesql @sql;