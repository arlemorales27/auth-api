CREATE DATABASE authdb;
USE authdb;

CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       username VARCHAR(50) NOT NULL UNIQUE,
                       password VARCHAR(100) NOT NULL,
                       email VARCHAR(100) NOT NULL UNIQUE,
                       role ENUM('ADMIN', 'USER', 'CLIENT') NOT NULL,
                       reset_token VARCHAR(255),
                       reset_token_expiry DATETIME
);

INSERT INTO users (username, password, email, role)
VALUES ('admin', '$2a$12$5s2s3s4s5s6s7s8s9s0s1s2s', 'admin@example.com', 'ADMIN');