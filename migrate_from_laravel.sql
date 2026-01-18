-- Migrate data from defiance_db (Laravel) to defiance_spring (Spring Boot)
-- Run in phpMyAdmin or MySQL CLI.

SET FOREIGN_KEY_CHECKS=0;

TRUNCATE TABLE defiance_spring.order_items;
TRUNCATE TABLE defiance_spring.orders;
TRUNCATE TABLE defiance_spring.cart_items;
TRUNCATE TABLE defiance_spring.products;
TRUNCATE TABLE defiance_spring.users;

INSERT INTO defiance_spring.users
(id, first_name, middle_name, last_name, email, password_hash, phone, address_line, barangay, city, province, postal_code, is_admin, email_verified)
SELECT
id,
first_name,
middle_name,
last_name,
email,
password,
phone,
address_line,
barangay,
city,
province,
postal_code,
is_admin,
IF(email_verified_at IS NULL, 0, 1)
FROM defiance_db.users;

INSERT INTO defiance_spring.products
(id, name, price, image, category, is_active, stock)
SELECT
id,
name,
price,
image,
category,
is_active,
stock
FROM defiance_db.products;

INSERT INTO defiance_spring.orders
(id, user_id, full_name, email, phone, address_line, payment_method, receipt_path, total, status, created_at)
SELECT
id,
user_id,
full_name,
email,
phone,
address,
payment_method,
receipt_path,
total,
status,
created_at
FROM defiance_db.orders;

INSERT INTO defiance_spring.order_items
(id, order_id, product_id, qty, price, subtotal)
SELECT
id,
order_id,
product_id,
qty,
price,
subtotal
FROM defiance_db.order_items;

INSERT INTO defiance_spring.cart_items
(id, cart_id, product_id, qty, price, subtotal)
SELECT
id,
cart_id,
product_id,
qty,
price,
subtotal
FROM defiance_db.cart_items;

SET FOREIGN_KEY_CHECKS=1;
