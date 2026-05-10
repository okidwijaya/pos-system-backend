CREATE TABLE users (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    tenant_id UUID,
    role VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    phone VARCHAR(255),
    is_active BOOLEAN DEFAULT TRUE,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    last_login TIMESTAMP,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE categories (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at TIMESTAMP,
    deleted_at TIMESTAMP,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE customers (
    id UUID PRIMARY KEY,
    name VARCHAR(255),
    email VARCHAR(255),
    phone VARCHAR(255),
    created_at TIMESTAMP
);

CREATE TABLE products (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    price NUMERIC(38, 2) NOT NULL,
    stock INTEGER NOT NULL,
    sku VARCHAR(255) NOT NULL UNIQUE,
    barcode VARCHAR(255),
    cost_price NUMERIC(15, 2) NOT NULL,
    image_url TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    deleted_at TIMESTAMP,
    category_id UUID REFERENCES categories(id),
    tax_rate NUMERIC(5, 2) DEFAULT 0.00
);

CREATE TABLE carts (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id),
    cart_status VARCHAR(50) NOT NULL,
    total NUMERIC(38, 2),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE TABLE cart_items (
    id UUID PRIMARY KEY,
    cart_id UUID NOT NULL REFERENCES carts(id),
    product_id UUID NOT NULL REFERENCES products(id),
    quantity INTEGER NOT NULL,
    price NUMERIC(15, 2) NOT NULL,
    CONSTRAINT uk_cart_items_cart_product UNIQUE (cart_id, product_id)
);

CREATE TABLE orders (
    id UUID PRIMARY KEY,
    idempotency_key VARCHAR(255) UNIQUE,
    order_number VARCHAR(255) NOT NULL UNIQUE,
    cashier_id UUID NOT NULL REFERENCES users(id),
    customer_id UUID REFERENCES customers(id),
    user_id UUID NOT NULL REFERENCES users(id),
    total_amount NUMERIC(15, 2) NOT NULL DEFAULT 0,
    notes VARCHAR(255),
    order_status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    total_before_tax NUMERIC(15, 2),
    total_tax NUMERIC(15, 2) DEFAULT 0,
    version BIGINT
);

CREATE TABLE order_items (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL REFERENCES orders(id),
    product_name VARCHAR(255) NOT NULL,
    price NUMERIC(15, 2) NOT NULL,
    product_id UUID REFERENCES products(id),
    quantity INTEGER NOT NULL,
    subtotal NUMERIC(15, 2) NOT NULL,
    tax_rate NUMERIC(5, 2) DEFAULT 0,
    tax_amount NUMERIC(15, 2) DEFAULT 0,
    subtotal_before_tax NUMERIC(15, 2)
);

CREATE TABLE payments (
    id UUID PRIMARY KEY,
    order_id UUID NOT NULL UNIQUE REFERENCES orders(id),
    external_id VARCHAR(255) NOT NULL UNIQUE,
    idempotency_key VARCHAR(255) UNIQUE,
    transaction_id VARCHAR(255),
    method VARCHAR(50),
    payment_url VARCHAR(255),
    snap_url VARCHAR(255),
    status VARCHAR(50) NOT NULL,
    amount NUMERIC(15, 2) NOT NULL,
    currency VARCHAR(10),
    expired_at TIMESTAMP,
    paid_at TIMESTAMP,
    stock_rolled_back_at TIMESTAMP,
    failure_reason VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    proof_image VARCHAR(255),
    notes VARCHAR(255),
    verified_by VARCHAR(255),
    rejected_by VARCHAR(255)
);

CREATE TABLE refresh_token (
    id BIGSERIAL PRIMARY KEY,
    token VARCHAR(255) NOT NULL UNIQUE,
    user_id UUID REFERENCES users(id),
    expiry_date TIMESTAMP
);

CREATE TABLE password_reset_token (
    id BIGSERIAL PRIMARY KEY,
    hash_token VARCHAR(255),
    user_id UUID REFERENCES users(id),
    expired_date TIMESTAMP,
    used BOOLEAN NOT NULL
);

CREATE INDEX idx_users_email_active ON users (email, is_active, is_deleted);
CREATE INDEX idx_products_active_search ON products (is_deleted, is_active, name);
CREATE INDEX idx_products_sku ON products (sku);
CREATE INDEX idx_carts_user_status ON carts (user_id, cart_status);
CREATE INDEX idx_orders_cashier_created_status ON orders (cashier_id, created_at DESC, order_status);
CREATE INDEX idx_orders_idempotency_key ON orders (idempotency_key);
CREATE INDEX idx_payments_external_id ON payments (external_id);
CREATE INDEX idx_payments_status_created ON payments (status, created_at);
CREATE INDEX idx_refresh_token_user ON refresh_token (user_id);
CREATE INDEX idx_password_reset_token_hash ON password_reset_token (hash_token);
