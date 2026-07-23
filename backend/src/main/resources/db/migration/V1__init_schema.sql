-- KSP Shodhana Database Schema Migration V1
-- Supports PostgreSQL 15+ with PostGIS geospatial extension

-- Enable PostGIS extension for spatial queries
CREATE EXTENSION IF NOT EXISTS postgis;

-- 1. Crimes Table
CREATE TABLE IF NOT EXISTS crimes (
    row_id BIGSERIAL PRIMARY KEY,
    fir_number VARCHAR(64) UNIQUE NOT NULL,
    crime_type VARCHAR(100) NOT NULL,
    district VARCHAR(100) NOT NULL,
    police_station VARCHAR(100) NOT NULL,
    status VARCHAR(50) NOT NULL,
    severity VARCHAR(20) NOT NULL,
    incident_date TIMESTAMP WITH TIME ZONE NOT NULL,
    reported_date TIMESTAMP WITH TIME ZONE NOT NULL,
    location_name VARCHAR(255) NOT NULL,
    latitude DOUBLE PRECISION,
    longitude DOUBLE PRECISION,
    geom GEOMETRY(Point, 4326),
    description TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Index for PostGIS spatial queries
CREATE INDEX IF NOT EXISTS idx_crimes_geom ON crimes USING GIST(geom);
CREATE INDEX IF NOT EXISTS idx_crimes_district ON crimes(district);
CREATE INDEX IF NOT EXISTS idx_crimes_crime_type ON crimes(crime_type);

-- 2. Criminals Table
CREATE TABLE IF NOT EXISTS criminals (
    row_id BIGSERIAL PRIMARY KEY,
    name VARCHAR(150) NOT NULL,
    alias VARCHAR(100),
    age INT,
    gender VARCHAR(10),
    id_number VARCHAR(50),
    phone VARCHAR(20),
    address TEXT,
    district VARCHAR(100),
    criminal_history TEXT,
    risk_level VARCHAR(20) NOT NULL,
    status VARCHAR(50) NOT NULL,
    photo_url TEXT,
    fingerprint_id VARCHAR(50) UNIQUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_criminals_name ON criminals(name);
CREATE INDEX IF NOT EXISTS idx_criminals_district ON criminals(district);
CREATE INDEX IF NOT EXISTS idx_criminals_status ON criminals(status);

-- 3. Crime-Criminal Links Table (Junction)
CREATE TABLE IF NOT EXISTS crime_criminal_links (
    row_id BIGSERIAL PRIMARY KEY,
    crime_row_id BIGINT REFERENCES crimes(row_id) ON DELETE CASCADE,
    criminal_row_id BIGINT REFERENCES criminals(row_id) ON DELETE CASCADE,
    role VARCHAR(50) NOT NULL,
    involvement_detail TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 4. Criminal Network Graph Edges
CREATE TABLE IF NOT EXISTS criminal_network (
    row_id BIGSERIAL PRIMARY KEY,
    criminal_a_row_id BIGINT REFERENCES criminals(row_id) ON DELETE CASCADE,
    criminal_b_row_id BIGINT REFERENCES criminals(row_id) ON DELETE CASCADE,
    relationship_type VARCHAR(50) NOT NULL,
    strength INT DEFAULT 5,
    evidence_summary TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 5. Timeline Events
CREATE TABLE IF NOT EXISTS timeline_events (
    row_id BIGSERIAL PRIMARY KEY,
    investigation_id BIGINT NOT NULL,
    timestamp TIMESTAMP WITH TIME ZONE NOT NULL,
    title VARCHAR(200) NOT NULL,
    description TEXT,
    category VARCHAR(50) NOT NULL,
    officer_name VARCHAR(150),
    fir_number VARCHAR(64)
);
