-- KSP Shodhana Row-Level Security (RLS) & Dynamic ABAC Migration V2
-- Enforces station jurisdiction and clearance level row filtering at database level

-- 1. Enable Row-Level Security on Crimes and Criminals tables
ALTER TABLE crimes ENABLE ROW LEVEL SECURITY;
ALTER TABLE criminals ENABLE ROW LEVEL SECURITY;

-- 2. Create Dynamic ABAC Policy for Crimes Table
-- Ensures officers only read records within their station jurisdiction OR if clearance level matches
DROP POLICY IF EXISTS station_jurisdiction_policy ON crimes;
CREATE POLICY station_jurisdiction_policy ON crimes
    FOR SELECT
    USING (
        police_station = COALESCE(current_setting('app.current_officer_station', true), police_station)
        OR district = COALESCE(current_setting('app.current_officer_district', true), district)
        OR COALESCE(current_setting('app.current_officer_clearance', true), '1')::integer >= 3
    );

-- 3. Create Dynamic ABAC Policy for Criminals Table
DROP POLICY IF EXISTS criminal_clearance_policy ON criminals;
CREATE POLICY criminal_clearance_policy ON criminals
    FOR SELECT
    USING (
        district = COALESCE(current_setting('app.current_officer_district', true), district)
        OR COALESCE(current_setting('app.current_officer_clearance', true), '1')::integer >= 2
    );
