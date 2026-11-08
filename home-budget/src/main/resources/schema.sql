CREATE TABLE category (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    name          VARCHAR(40)    NOT NULL UNIQUE,
    monthly_limit DECIMAL(12, 2) NOT NULL
);

CREATE TABLE expense (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    category_id  BIGINT         NOT NULL REFERENCES category (id),
    amount       DECIMAL(12, 2) NOT NULL,
    expense_date DATE           NOT NULL,
    description  VARCHAR(100)
);

CREATE TABLE closed_month (
    month_key VARCHAR(7) PRIMARY KEY
);
