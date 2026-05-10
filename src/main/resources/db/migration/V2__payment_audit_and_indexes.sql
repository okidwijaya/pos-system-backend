ALTER TABLE payments ADD COLUMN IF NOT EXISTS stock_rolled_back_at TIMESTAMP;
ALTER TABLE payments ADD COLUMN IF NOT EXISTS verified_by VARCHAR(255);
ALTER TABLE payments ADD COLUMN IF NOT EXISTS rejected_by VARCHAR(255);

CREATE INDEX IF NOT EXISTS idx_users_email_active ON users (email, is_active, is_deleted);
CREATE INDEX IF NOT EXISTS idx_products_active_search ON products (is_deleted, is_active, name);
CREATE INDEX IF NOT EXISTS idx_products_sku ON products (sku);
CREATE INDEX IF NOT EXISTS idx_carts_user_status ON carts (user_id, cart_status);
CREATE INDEX IF NOT EXISTS idx_orders_cashier_created_status ON orders (cashier_id, created_at DESC, order_status);
CREATE INDEX IF NOT EXISTS idx_orders_idempotency_key ON orders (idempotency_key);
CREATE INDEX IF NOT EXISTS idx_payments_external_id ON payments (external_id);
CREATE INDEX IF NOT EXISTS idx_payments_status_created ON payments (status, created_at);
CREATE INDEX IF NOT EXISTS idx_refresh_token_user ON refresh_token (user_id);
CREATE INDEX IF NOT EXISTS idx_password_reset_token_hash ON password_reset_token (hash_token);
