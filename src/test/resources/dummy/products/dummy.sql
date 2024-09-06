INSERT INTO seller (id, name, username, password, two_factor_enabled, phone_number, street_name, city, zip_code, state, country, gst_id)
VALUES (1, 'Alice Johnson', 'alicej', 'securePass123', TRUE, '+1234567890', 'Maple Street', 'Springfield', '12345', 'CA', 'USA', 'GST123456');

INSERT INTO seller (id, name, username, password, two_factor_enabled, phone_number, street_name, city, zip_code, state, country, gst_id)
VALUES (2, 'Bob Smith', 'bobsmith', 'strongPassword456', FALSE, '+0987654321', 'Oak Avenue', 'Metropolis', '54321', 'NY', 'USA', 'GST654321');
-- Products for Seller 1
INSERT INTO product (id, name, description, quantity, amount, image, seller_id)
VALUES (1, 'Product A1', 'Description for Product A1', 10, 99.99, 'image_a1.jpg', 1);

INSERT INTO product (id, name, description, quantity, amount, image, seller_id)
VALUES (2, 'Product A2', 'Description for Product A2', 15, 149.99, 'image_a2.jpg', 1);

-- Products for Seller 2
INSERT INTO product (id, name, description, quantity, amount, image, seller_id)
VALUES (3, 'Product B1', 'Description for Product B1', 20, 199.99, 'image_b1.jpg', 2);

INSERT INTO product (id, name, description, quantity, amount, image, seller_id)
VALUES (4, 'Product B2', 'Description for Product B2', 25, 249.99, 'image_b2.jpg', 2);
