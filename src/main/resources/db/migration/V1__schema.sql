CREATE TABLE seller (
    id VARCHAR(60) PRIMARY KEY NOT NULL,
    name VARCHAR(100)
);

CREATE TABLE billing (
    id VARCHAR(60) PRIMARY KEY NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    seller_id VARCHAR(60) NOT NULL,
    FOREIGN KEY (seller_id) REFERENCES seller(id)
);

CREATE TABLE payment (
    id BIGINT AUTO_INCREMENT PRIMARY KEY NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(20),
    billing_id VARCHAR(60) NOT NULL,
    FOREIGN KEY (billing_id) REFERENCES billing (id)
);