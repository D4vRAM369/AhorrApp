-- Crear tabla de usuarios con autenticación
CREATE TABLE users (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    email TEXT UNIQUE NOT NULL,
    nickname TEXT,
    device_id TEXT, -- Para migrar usuarios existentes
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    last_login_at TIMESTAMP WITH TIME ZONE,
    is_active BOOLEAN DEFAULT TRUE,
    preferences JSONB DEFAULT '{}' -- Para configuraciones personalizadas
);

-- Crear índices para optimización
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_device_id ON users(device_id);
CREATE INDEX idx_users_created_at ON users(created_at DESC);

-- Crear tabla de métricas analíticas
CREATE TABLE user_analytics (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id UUID REFERENCES users(id) ON DELETE CASCADE,
    device_id TEXT, -- Para usuarios sin auth, temporalmente

    -- Métricas de engagement
    total_scans INTEGER DEFAULT 0,
    unique_products_scanned INTEGER DEFAULT 0,
    total_prices_reported INTEGER DEFAULT 0,
    total_favorites_added INTEGER DEFAULT 0,
    total_alerts_created INTEGER DEFAULT 0,

    -- Métricas financieras
    total_money_saved DECIMAL(10,2) DEFAULT 0.00,
    average_price_drop_percentage DECIMAL(5,2) DEFAULT 0.00,
    best_deal_found DECIMAL(10,2) DEFAULT 0.00,

    -- Métricas de uso
    app_open_count INTEGER DEFAULT 0,
    average_session_duration INTEGER DEFAULT 0, -- en segundos
    last_scan_at TIMESTAMP WITH TIME ZONE,
    first_scan_at TIMESTAMP WITH TIME ZONE,

    -- Métricas sociales/comunitarias
    community_contributions INTEGER DEFAULT 0, -- precios compartidos
    helpful_votes_received INTEGER DEFAULT 0,
    products_added_to_community INTEGER DEFAULT 0,

    -- Metadata
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Crear índices para consultas analíticas
CREATE INDEX idx_user_analytics_user_id ON user_analytics(user_id);
CREATE INDEX idx_user_analytics_device_id ON user_analytics(device_id);
CREATE INDEX idx_user_analytics_total_scans ON user_analytics(total_scans DESC);
CREATE INDEX idx_user_analytics_total_money_saved ON user_analytics(total_money_saved DESC);
CREATE INDEX idx_user_analytics_updated_at ON user_analytics(updated_at DESC);

-- Crear tabla de eventos analíticos (para tracking detallado)
CREATE TABLE analytics_events (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id UUID REFERENCES users(id) ON DELETE SET NULL,
    device_id TEXT,

    event_type TEXT NOT NULL, -- 'scan', 'price_report', 'favorite_add', 'alert_create', etc.
    event_data JSONB DEFAULT '{}', -- datos específicos del evento

    session_id TEXT, -- para agrupar eventos de una sesión
    user_agent TEXT,
    ip_address INET,

    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Índices para eventos
CREATE INDEX idx_analytics_events_user_id ON analytics_events(user_id);
CREATE INDEX idx_analytics_events_event_type ON analytics_events(event_type);
CREATE INDEX idx_analytics_events_created_at ON analytics_events(created_at DESC);
CREATE INDEX idx_analytics_events_session_id ON analytics_events(session_id);

-- Función para actualizar métricas automáticamente
CREATE OR REPLACE FUNCTION update_user_analytics()
RETURNS TRIGGER AS $$
BEGIN
    -- Actualizar contador de escaneos cuando se inserta un precio
    IF TG_TABLE_NAME = 'prices' AND TG_OP = 'INSERT' THEN
        INSERT INTO user_analytics (user_id, device_id, total_prices_reported, updated_at)
        VALUES (
            (SELECT id FROM users WHERE device_id = NEW.user_id LIMIT 1),
            COALESCE(NEW.user_id, NEW.nickname),
            1,
            NOW()
        )
        ON CONFLICT (COALESCE(user_id::text, device_id))
        DO UPDATE SET
            total_prices_reported = user_analytics.total_prices_reported + 1,
            updated_at = NOW();

    -- Actualizar contador de favoritos
    ELSIF TG_TABLE_NAME = 'user_favorites' AND TG_OP = 'INSERT' THEN
        INSERT INTO user_analytics (user_id, device_id, total_favorites_added, updated_at)
        VALUES (
            (SELECT id FROM users WHERE device_id = NEW.device_id LIMIT 1),
            NEW.device_id,
            1,
            NOW()
        )
        ON CONFLICT (COALESCE(user_id::text, device_id))
        DO UPDATE SET
            total_favorites_added = user_analytics.total_favorites_added + 1,
            updated_at = NOW();
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Crear triggers para actualizar métricas automáticamente
CREATE TRIGGER trigger_update_analytics_on_price
    AFTER INSERT ON prices
    FOR EACH ROW EXECUTE FUNCTION update_user_analytics();

CREATE TRIGGER trigger_update_analytics_on_favorite
    AFTER INSERT ON user_favorites
    FOR EACH ROW EXECUTE FUNCTION update_user_analytics();