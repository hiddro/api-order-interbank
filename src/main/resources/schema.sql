CREATE TABLE products (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          name VARCHAR(50) NOT NULL,
                          price DOUBLE NOT NULL,
                          stock BIGINT NOT NULL,
                          version BIGINT NOT NULL
);

CREATE TABLE orders (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          fecha VARCHAR(10) NOT NULL,
                          total DOUBLE NOT NULL,
                          estado VARCHAR(20) NOT NULL,
                          products_json CLOB,
                          version BIGINT NOT NULL
);

INSERT INTO products (name, price, stock, version) VALUES
                                                       ('Tarjeta Crédito Oro', 150.00, 50, 0),
                                                       ('Tarjeta Crédito Platinum', 300.00, 40, 0),
                                                       ('Cuenta Corriente Personal', 0.00, 100, 0),
                                                       ('Cuenta Ahorro Premium', 0.00, 80, 0),
                                                       ('Préstamo Personal', 5000.00, 30, 0),
                                                       ('Préstamo Hipotecario', 250000.00, 10, 0),
                                                       ('Plan Familiar Seguro', 200.00, 60, 0),
                                                       ('Seguro de Auto', 150.00, 50, 0),
                                                       ('Inversión Fondos Mutuos', 1000.00, 25, 0),
                                                       ('Depósito a Plazo', 1000.00, 70, 0);