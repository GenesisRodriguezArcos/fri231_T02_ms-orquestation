-- =====================================================
-- EDUNOVA DATABASE - STUDENT CONTROL SYSTEM
-- WITH CONSTRAINTS AND VALIDATIONS
-- =====================================================

-- 1. STUDENTS TABLE
CREATE TABLE IF NOT EXISTS students (
    id SERIAL PRIMARY KEY,
    code VARCHAR(10) UNIQUE NOT NULL,
    dni VARCHAR(8) UNIQUE NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    mother_last_name VARCHAR(50),
    email VARCHAR(100) UNIQUE NOT NULL,
    phone CHAR(9),
    grade CHAR(1) NOT NULL,
    section VARCHAR(2) NOT NULL,
    status CHAR(1) DEFAULT 'A',
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    -- Constraints
    CONSTRAINT chk_dni CHECK (dni ~ '^[0-9]{8}$'),
    CONSTRAINT chk_phone CHECK (phone ~ '^9[0-9]{8}$'),
    CONSTRAINT chk_grade CHECK (grade IN ('1', '2', '3', '4', '5')),
    CONSTRAINT chk_status CHECK (status IN ('A', 'I', 'S'))  -- A=ACTIVE, I=INACTIVE, S=SUSPENDED
);

-- 2. TARDINESS TABLE (individual tardiness records)
CREATE TABLE IF NOT EXISTS tardiness (
    id SERIAL PRIMARY KEY,
    student_id INTEGER NOT NULL REFERENCES students(id) ON DELETE CASCADE,
    date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    arrival_time TIME NOT NULL,
    minutes_late INTEGER NOT NULL,
    justified BOOLEAN DEFAULT FALSE,
    reason TEXT,
    registered_by VARCHAR(50) DEFAULT 'SYSTEM',
    
    -- Constraints
    CONSTRAINT chk_minutes_late CHECK (minutes_late > 0 AND minutes_late <= 60),
    CONSTRAINT chk_arrival_time CHECK (arrival_time > '07:00:00')
);

-- 3. WARNINGS TABLE (warning records)
CREATE TABLE IF NOT EXISTS warnings (
    id SERIAL PRIMARY KEY,
    student_id INTEGER NOT NULL REFERENCES students(id) ON DELETE CASCADE,
    date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    type CHAR(1) NOT NULL,
    reason TEXT NOT NULL,
    registered_by VARCHAR(50) DEFAULT 'SYSTEM',
    
    -- Constraints
    CONSTRAINT chk_warning_type CHECK (type IN ('L', 'G', 'M'))  -- L=LIGHT, G=GRAVE, M=VERY_GRAVE
);

-- =====================================================
-- INDEXES FOR BETTER PERFORMANCE
-- =====================================================
CREATE INDEX idx_student_code ON students(code);
CREATE INDEX idx_student_dni ON students(dni);
CREATE INDEX idx_student_status ON students(status);
CREATE INDEX idx_tardiness_student ON tardiness(student_id);
CREATE INDEX idx_tardiness_date ON tardiness(date);
CREATE INDEX idx_warning_student ON warnings(student_id);
CREATE INDEX idx_warning_date ON warnings(date);

-- =====================================================
-- TEST DATA (6 students)
-- =====================================================

-- Insert students
INSERT INTO students (code, dni, first_name, last_name, mother_last_name, email, phone, grade, section, status) VALUES
('E001', '12345678', 'Juan', 'Perez', 'Garcia', 'juan.perez@edunova.edu', '987654321', '4', 'A', 'A'),
('E002', '87654321', 'Maria', 'Lopez', 'Rodriguez', 'maria.lopez@edunova.edu', '987654322', '4', 'A', 'A'),
('E003', '11122233', 'Carlos', 'Sanchez', 'Diaz', 'carlos.sanchez@edunova.edu', '987654323', '5', 'B', 'A'),
('E004', '44455566', 'Ana', 'Torres', 'Flores', 'ana.torres@edunova.edu', '987654324', '3', 'C', 'I'),
('E005', '77788899', 'Luis', 'Ramirez', 'Castro', 'luis.ramirez@edunova.edu', '987654325', '4', 'B', 'S'),
('E006', '99900011', 'Lucia', 'Fernandez', 'Mendoza', 'lucia.fernandez@edunova.edu', '987654326', '5', 'A', 'A');

-- Insert tardiness records
INSERT INTO tardiness (student_id, arrival_time, minutes_late, justified, reason) VALUES
(1, '08:15:00', 15, FALSE, 'Traffic on the route'),
(1, '08:30:00', 30, TRUE, 'Medical appointment'),
(2, '08:10:00', 10, FALSE, 'Public transportation'),
(3, '08:45:00', 45, FALSE, 'Overslept'),
(4, '08:20:00', 20, TRUE, 'Family issues'),
(5, '08:05:00', 5, FALSE, NULL);

-- Insert warning records
INSERT INTO warnings (student_id, type, reason, registered_by) VALUES
(1, 'L', 'Recurrent late arrivals', 'Teacher Juan Carlos'),
(3, 'G', 'Bad behavior in class', 'Academic Coordination'),
(5, 'M', 'Disrespect to teacher', 'School Direction'),
(2, 'L', 'Does not submit homework', 'Teacher Maria Fernandez');

