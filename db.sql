CREATE DATABASE Techify;
GO
USE Techify;

CREATE TABLE account (
                         id NVARCHAR(20) PRIMARY KEY,
                         email NVARCHAR(50) UNIQUE NOT NULL,
                         password_hash NVARCHAR(255),
                         role NVARCHAR(20) NOT NULL,
                         refresh_token NVARCHAR(255),
                         google_id NVARCHAR(50),
                         facebook_id NVARCHAR(50),
);

CREATE TABLE customer
(
    id            NVARCHAR(20) PRIMARY KEY,
    full_name     NVARCHAR(50) NOT NULL,
    phone         NVARCHAR(20),
    alt_phone     NVARCHAR(20),
    province      NVARCHAR(50),
    district      NVARCHAR(50),
    ward          NVARCHAR(50),
    address       NVARCHAR(255),
    alt_address   NVARCHAR(255),
    FOREIGN KEY (id) REFERENCES account(id)
);
GO
CREATE TABLE staff
(
    id            NVARCHAR(20) PRIMARY KEY,
    full_name     NVARCHAR(50) NOT NULL,
    dob           DATE,
    gender        NVARCHAR(10),
    citizen_id    NVARCHAR(20),
    phone         NVARCHAR(20),
    address       NVARCHAR(255),
    FOREIGN KEY (id) REFERENCES account(id)
);
GO
CREATE TABLE parent_category
(
    id   INT PRIMARY KEY IDENTITY,
    name NVARCHAR(50) NOT NULL UNIQUE
);
GO
CREATE TABLE category
(
    id                 INT PRIMARY KEY IDENTITY,
    parent_category_id INT FOREIGN KEY REFERENCES parent_category (id),
    name               NVARCHAR(50) NOT NULL UNIQUE
);
GO
CREATE TABLE color
(
    id         TINYINT PRIMARY KEY IDENTITY,
    color_json NVARCHAR(255) NOT NULL
)
    GO
CREATE TABLE image
(
    id         TINYINT PRIMARY KEY IDENTITY,
    image_json NVARCHAR(1000) NOT NULL
)
    GO
CREATE TABLE attribute
(
    id             TINYINT PRIMARY KEY IDENTITY,
    attribute_json NVARCHAR(4000) NOT NULL
)
    GO
CREATE TABLE product
(
    id           NVARCHAR(20) PRIMARY KEY,
    category_id  INT FOREIGN KEY REFERENCES category (id),
    name         NVARCHAR(50) NOT NULL,
    thumbnail    NVARCHAR(255),
    brand        NVARCHAR(50),
    origin       NVARCHAR(50) NOT NULL,
    unit         NVARCHAR(20) NOT NULL,
    serial       NVARCHAR(50),
    warranty     INT,
    buy_price    MONEY        NOT NULL,
    sell_price   MONEY        NOT NULL,
    tax          FLOAT        NOT NULL,
    description  NVARCHAR(255),
    color_id     TINYINT FOREIGN KEY REFERENCES color (id),
    image_id     TINYINT FOREIGN KEY REFERENCES image (id),
    attribute_id TINYINT FOREIGN KEY REFERENCES attribute (id),
);
GO
CREATE TABLE voucher
(
    id             NVARCHAR(20) PRIMARY KEY,
    discount_type  BIT       NOT NULL,
    discount_value FLOAT     NOT NULL,
    min_order      MONEY     NOT NULL,
    max_discount   MONEY,
    usage_limit    INT,
    start_date     DATETIME2 NOT NULL,
    end_date       DATETIME2 NOT NULL,
);
GO
CREATE TABLE promotion
(
    id             INT PRIMARY KEY IDENTITY,
    name           NVARCHAR(50) NOT NULL,
    description    NVARCHAR(255),
    discount_type  BIT          NOT NULL,
    discount_value FLOAT        NOT NULL,
    start_date     DATETIME2    NOT NULL,
    end_date       DATETIME2    NOT NULL,
);
GO
CREATE TABLE product_promotion
(
    id           INT PRIMARY KEY IDENTITY,
    product_id   NVARCHAR(20) FOREIGN KEY REFERENCES product (id),
    promotion_id INT FOREIGN KEY REFERENCES promotion (id),
);
GO
CREATE TABLE review
(
    id          INT PRIMARY KEY IDENTITY,
    product_id  NVARCHAR(20) FOREIGN KEY REFERENCES product (id),
    customer_id NVARCHAR(20) FOREIGN KEY REFERENCES customer (id),
    rating      TINYINT   NOT NULL,
    comment     NVARCHAR(255),
    created_at  DATETIME2 NOT NULL
);
GO
CREATE TABLE payment_method
(
    id   TINYINT PRIMARY KEY IDENTITY,
    name NVARCHAR(50) NOT NULL UNIQUE
);
GO
CREATE TABLE transport_vendor
(
    id     NVARCHAR(15) PRIMARY KEY,
    name   NVARCHAR(50) NOT NULL,
    phone  NVARCHAR(20),
    email  NVARCHAR(50),
    status BIT          NOT NULL DEFAULT 1
);
GO
CREATE TABLE [order]
(
    id                  NVARCHAR(40) PRIMARY KEY,
    customer_id         NVARCHAR(20) FOREIGN KEY REFERENCES customer (id),
    staff_id            NVARCHAR(20) FOREIGN KEY REFERENCES staff (id),
    payment_method_id   TINYINT FOREIGN KEY REFERENCES payment_method (id),
    transport_vendor_id NVARCHAR(15) FOREIGN KEY REFERENCES transport_vendor (id),
    voucher_id          NVARCHAR(20) FOREIGN KEY REFERENCES voucher (id),
    shipping_address    NVARCHAR(255),
    status              TINYINT   NOT NULL,
    created_at          DATETIME2 NOT NULL,
    updated_at          DATETIME2 NOT NULL
    );
GO
CREATE TABLE order_detail
(
    id         INT PRIMARY KEY IDENTITY,
    order_id   NVARCHAR(40) FOREIGN KEY REFERENCES [order] (id),
    product_id NVARCHAR(20) FOREIGN KEY REFERENCES product (id),
    color      NVARCHAR(50),
    price      MONEY NOT NULL,
    quantity   INT   NOT NULL
);