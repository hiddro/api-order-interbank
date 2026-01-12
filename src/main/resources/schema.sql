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