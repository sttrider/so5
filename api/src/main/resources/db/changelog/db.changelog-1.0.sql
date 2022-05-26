-- liquibase formatted SQL

-- changeset joao.moreira:creating_tables dbms:mysql
CREATE TABLE address
(
    id            BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    street        VARCHAR(100)       NOT NULL,
    number        VARCHAR(50)        NOT NULL,
    district      VARCHAR(100)       NOT NULL,
    city          VARCHAR(100)       NOT NULL,
    state         VARCHAR(50)        NOT NULL,
    country       VARCHAR(50)        NOT NULL,
    zip_code      VARCHAR(15)        NOT NULL,
    creation_date DATETIME           NOT NULL
);

CREATE TABLE user
(
    id            BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    name          VARCHAR(60)        NOT NULL,
    email         VARCHAR(60)        NOT NULL,
    password      VARCHAR(60)        NOT NULL,
    enabled       BOOLEAN            NOT NULL,
    user_type     INT                NOT NULL,
    creation_date DATETIME           NOT NULL
);

CREATE TABLE customer
(
    id                  BIGINT NOT NULL,
    shipping_address_id BIGINT,
    billing_address_id  BIGINT,
    CONSTRAINT FOREIGN KEY (id) REFERENCES user (id),
    CONSTRAINT FOREIGN KEY (shipping_address_id) REFERENCES address (id),
    CONSTRAINT FOREIGN KEY (billing_address_id) REFERENCES address (id)
);

CREATE TABLE admin
(
    id BIGINT NOT NULL,
    CONSTRAINT FOREIGN KEY (id) REFERENCES user (id)
);

CREATE TABLE credit_card_data
(
    id                BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    customer_id       BIGINT             NOT NULL,
    holder            VARCHAR(60)        NOT NULL,
    number            VARCHAR(16)        NOT NULL,
    verification_code VARCHAR(3)         NOT NULL,
    expiration_date   DATE               NOT NULL,
    creation_date     DATETIME           NOT NULL,
    CONSTRAINT FOREIGN KEY (customer_id) REFERENCES customer (id)
);

CREATE TABLE product_category
(
    id   BIGINT PRIMARY KEY NOT NULL AUTO_INCREMENT,
    name VARCHAR(60)        NOT NULL
);

CREATE TABLE product
(
    sku                     VARCHAR(60) PRIMARY KEY NOT NULL,
    name                    VARCHAR(60)             NOT NULL,
    description             TEXT                    NOT NULL,
    price                   DECIMAL(10, 2)          NOT NULL,
    image                   VARCHAR(200)            NOT NULL,
    inventory               INT                     NOT NULL,
    shipment_delivery_times INT                     NOT NULL,
    enabled                 BOOLEAN                 NOT NULL,
    category_id             BIGINT                  NOT NULL,
    creation_date           DATETIME                NOT NULL,
    CONSTRAINT FOREIGN KEY (category_id) REFERENCES product_category (id)
);
-- ROLLBACK DROP TABLE credit_card_data;
-- ROLLBACK DROP TABLE customer;
-- ROLLBACK DROP TABLE admin;
-- ROLLBACK DROP TABLE user;
-- ROLLBACK DROP TABLE address;
-- ROLLBACK DROP TABLE product;
-- ROLLBACK DROP TABLE product_category;
