CREATE TABLE products (
    barcode TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    brand TEXT,
    more_info TEXT,
    image_url TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE INDEX idx_products_name ON products(name);
CREATE INDEX idx_products_brand ON products(brand);
CREATE INDEX idx_products_created_at ON products(created_at DESC);
