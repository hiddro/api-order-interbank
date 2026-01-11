CREATE TABLE products (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          name VARCHAR(50) NOT NULL,
                          price DOUBLE NOT NULL,
                          stock BIGINT NOT NULL
);