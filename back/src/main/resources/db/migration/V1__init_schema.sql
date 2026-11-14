CREATE SCHEMA IF NOT EXISTS app;

CREATE TABLE IF NOT EXISTS service_center (
    id UUID PRIMARY KEY,
    code VARCHAR(16) UNIQUE NOT NULL,
    name VARCHAR(128) NOT NULL,
    address VARCHAR(255) NOT NULL,
    lat DOUBLE PRECISION NOT NULL,
    lon DOUBLE PRECISION NOT NULL,
    open_time TIME NOT NULL,
    close_time TIME NOT NULL,
    restock_duration_min INT NOT NULL DEFAULT 15
);

CREATE TABLE IF NOT EXISTS shift (
    id UUID PRIMARY KEY,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    break_duration_min INT NOT NULL DEFAULT 60,
    break_window_from TIME NOT NULL,
    break_window_to TIME NOT NULL
);

CREATE TABLE IF NOT EXISTS staff (
    id UUID PRIMARY KEY,
    code VARCHAR(16) UNIQUE NOT NULL,
    display_name VARCHAR(128) NOT NULL,
    service_center_id UUID NOT NULL REFERENCES service_center(id) ON DELETE RESTRICT,
    shift_id UUID NOT NULL REFERENCES shift(id) ON DELETE RESTRICT,
    height_permit BOOLEAN NOT NULL DEFAULT FALSE,
    experience_years DOUBLE PRECISION NOT NULL DEFAULT 0.0,
    equipment_start_ont INT NOT NULL DEFAULT 0,
    equipment_start_router INT NOT NULL DEFAULT 0,
    equipment_start_stb INT NOT NULL DEFAULT 0,
    equipment_capacity_ont INT NOT NULL DEFAULT 0,
    equipment_capacity_router INT NOT NULL DEFAULT 0,
    equipment_capacity_stb INT NOT NULL DEFAULT 0
);
CREATE INDEX IF NOT EXISTS idx_staff_sc ON staff(service_center_id);
CREATE INDEX IF NOT EXISTS idx_staff_shift ON staff(shift_id);

CREATE TABLE IF NOT EXISTS staff_profile (
    staff_id UUID NOT NULL REFERENCES staff(id) ON DELETE CASCADE,
    profile VARCHAR(32) NOT NULL,
    PRIMARY KEY (staff_id, profile)
);

CREATE TABLE IF NOT EXISTS staff_technology (
    staff_id UUID NOT NULL REFERENCES staff(id) ON DELETE CASCADE,
    technology VARCHAR(16) NOT NULL,
    PRIMARY KEY (staff_id, technology)
);

CREATE TABLE IF NOT EXISTS access_node (
    id UUID PRIMARY KEY,
    code VARCHAR(16) UNIQUE NOT NULL,
    node_type VARCHAR(16) NOT NULL,
    technology VARCHAR(16) NOT NULL,
    name VARCHAR(128) NOT NULL,
    address VARCHAR(255) NOT NULL,
    lat DOUBLE PRECISION NOT NULL,
    lon DOUBLE PRECISION NOT NULL
);

CREATE TABLE IF NOT EXISTS building (
    id UUID PRIMARY KEY,
    code VARCHAR(16) UNIQUE NOT NULL,
    access_node_id UUID NOT NULL REFERENCES access_node(id) ON DELETE RESTRICT,
    zone_service_center_id UUID REFERENCES service_center(id) ON DELETE SET NULL,
    address VARCHAR(255) NOT NULL,
    lat DOUBLE PRECISION NOT NULL,
    lon DOUBLE PRECISION NOT NULL,
    building_type VARCHAR(16) NOT NULL,
    overhead_line BOOLEAN NOT NULL DEFAULT FALSE
);
CREATE INDEX IF NOT EXISTS idx_building_node ON building(access_node_id);
CREATE INDEX IF NOT EXISTS idx_building_zone ON building(zone_service_center_id);

CREATE TABLE IF NOT EXISTS work_type (
    id UUID PRIMARY KEY,
    code VARCHAR(32) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    duration_min INT NOT NULL,
    max_start_delay_min INT,
    deadline_kind VARCHAR(16) NOT NULL,
    deadline_value INT,
    repeat_deadline_value INT,
    pgp_relevant BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE IF NOT EXISTS work_type_profile (
    work_type_id UUID NOT NULL REFERENCES work_type(id) ON DELETE CASCADE,
    profile VARCHAR(32) NOT NULL,
    PRIMARY KEY (work_type_id, profile)
);

CREATE TABLE IF NOT EXISTS work_day (
    id UUID PRIMARY KEY,
    date DATE UNIQUE NOT NULL,
    current_now TIMESTAMPTZ NOT NULL,
    plan_version INT NOT NULL DEFAULT 1,
    based_on_event_id VARCHAR(64)
);

CREATE TABLE IF NOT EXISTS work_day_absent_staff (
    day_id UUID NOT NULL REFERENCES work_day(id) ON DELETE CASCADE,
    staff_id UUID NOT NULL REFERENCES staff(id) ON DELETE CASCADE,
    PRIMARY KEY (day_id, staff_id)
);

CREATE TABLE IF NOT EXISTS work_order (
    id UUID PRIMARY KEY,
    code VARCHAR(32) UNIQUE NOT NULL,
    day_id UUID NOT NULL REFERENCES work_day(id) ON DELETE CASCADE,
    service_center_id UUID REFERENCES service_center(id) ON DELETE RESTRICT,
    work_type_id UUID NOT NULL REFERENCES work_type(id) ON DELETE RESTRICT,
    building_id UUID NOT NULL REFERENCES building(id) ON DELETE RESTRICT,
    access_node_id UUID NOT NULL REFERENCES access_node(id) ON DELETE RESTRICT,
    registered_at TIMESTAMPTZ NOT NULL,
    slot_start TIMESTAMPTZ NOT NULL,
    slot_end TIMESTAMPTZ NOT NULL,
    latest_start TIMESTAMPTZ NOT NULL,
    deadline TIMESTAMPTZ NOT NULL,
    required_ont INT NOT NULL DEFAULT 0,
    required_router INT NOT NULL DEFAULT 0,
    required_stb INT NOT NULL DEFAULT 0,
    is_repeat BOOLEAN NOT NULL DEFAULT FALSE,
    previous_order_id VARCHAR(32),
    status VARCHAR(32) NOT NULL DEFAULT 'PENDING'
);
CREATE INDEX IF NOT EXISTS idx_order_day ON work_order(day_id);
CREATE INDEX IF NOT EXISTS idx_order_sc ON work_order(service_center_id);
CREATE INDEX IF NOT EXISTS idx_order_status ON work_order(status);

CREATE TABLE IF NOT EXISTS route (
    id UUID PRIMARY KEY,
    day_id UUID NOT NULL REFERENCES work_day(id) ON DELETE CASCADE,
    staff_id UUID NOT NULL REFERENCES staff(id) ON DELETE CASCADE,
    service_center_id UUID NOT NULL REFERENCES service_center(id) ON DELETE RESTRICT,
    status VARCHAR(16) NOT NULL DEFAULT 'ACTIVE',
    unavailable_from TIMESTAMPTZ,
    unavailable_until TIMESTAMPTZ,
    CONSTRAINT uq_route_day_staff UNIQUE (day_id, staff_id)
);
CREATE INDEX IF NOT EXISTS idx_route_day ON route(day_id);
CREATE INDEX IF NOT EXISTS idx_route_staff ON route(staff_id);

CREATE TABLE IF NOT EXISTS route_stop (
    id UUID PRIMARY KEY,
    route_id UUID NOT NULL REFERENCES route(id) ON DELETE CASCADE,
    seq INT NOT NULL,
    type VARCHAR(16) NOT NULL,
    location_code VARCHAR(32) NOT NULL,
    order_id UUID REFERENCES work_order(id) ON DELETE SET NULL,
    arrival TIMESTAMPTZ NOT NULL,
    start_time TIMESTAMPTZ NOT NULL,
    end_time TIMESTAMPTZ NOT NULL,
    travel_sec INT NOT NULL DEFAULT 0,
    distance_m INT NOT NULL DEFAULT 0,
    status VARCHAR(16) NOT NULL DEFAULT 'PLANNED',
    equipment_after_ont INT,
    equipment_after_router INT,
    equipment_after_stb INT
);
CREATE INDEX IF NOT EXISTS idx_stop_route ON route_stop(route_id);
CREATE INDEX IF NOT EXISTS idx_stop_order ON route_stop(order_id);

CREATE TABLE IF NOT EXISTS unassigned_order (
    id UUID PRIMARY KEY,
    day_id UUID NOT NULL REFERENCES work_day(id) ON DELETE CASCADE,
    order_id UUID NOT NULL REFERENCES work_order(id) ON DELETE CASCADE,
    reason_code VARCHAR(32) NOT NULL,
    reason_text TEXT NOT NULL,
    suggested_action VARCHAR(32) NOT NULL,
    proposed_slot_start TIMESTAMPTZ,
    proposed_slot_end TIMESTAMPTZ,
    since TIMESTAMPTZ NOT NULL,
    CONSTRAINT uq_unassigned_day_order UNIQUE (day_id, order_id)
);

CREATE TABLE IF NOT EXISTS pgp_group (
    id UUID PRIMARY KEY,
    code VARCHAR(32) UNIQUE NOT NULL,
    day_id UUID NOT NULL REFERENCES work_day(id) ON DELETE CASCADE,
    access_node_id UUID NOT NULL REFERENCES access_node(id) ON DELETE RESTRICT,
    detected_at TIMESTAMPTZ NOT NULL,
    action VARCHAR(32) NOT NULL DEFAULT 'TRANSFER_TO_STATION',
    comment TEXT
);
CREATE INDEX IF NOT EXISTS idx_pgp_day ON pgp_group(day_id);

CREATE TABLE IF NOT EXISTS pgp_group_order (
    group_id UUID NOT NULL REFERENCES pgp_group(id) ON DELETE CASCADE,
    order_id UUID NOT NULL REFERENCES work_order(id) ON DELETE CASCADE,
    PRIMARY KEY (group_id, order_id)
);

CREATE TABLE IF NOT EXISTS alert (
    id UUID PRIMARY KEY,
    code VARCHAR(32) UNIQUE NOT NULL,
    day_id UUID NOT NULL REFERENCES work_day(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL,
    type VARCHAR(32) NOT NULL,
    status VARCHAR(16) NOT NULL DEFAULT 'ACTIVE',
    resolved_at TIMESTAMPTZ,
    order_id UUID REFERENCES work_order(id) ON DELETE SET NULL,
    staff_id UUID REFERENCES staff(id) ON DELETE SET NULL,
    suggested_action VARCHAR(32),
    message TEXT NOT NULL,
    applied BOOLEAN NOT NULL DEFAULT FALSE
);
CREATE INDEX IF NOT EXISTS idx_alert_day ON alert(day_id);

CREATE TABLE IF NOT EXISTS event (
    id UUID PRIMARY KEY,
    day_id UUID NOT NULL REFERENCES work_day(id) ON DELETE CASCADE,
    code VARCHAR(64) NOT NULL,
    type VARCHAR(32) NOT NULL,
    timestamp TIMESTAMPTZ NOT NULL,
    payload TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_event_day ON event(day_id);

CREATE TABLE IF NOT EXISTS app_user (
    id UUID PRIMARY KEY,
    username VARCHAR(64) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(32) NOT NULL DEFAULT 'DISPATCHER',
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);
