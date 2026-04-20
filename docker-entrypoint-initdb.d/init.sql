-- CREATE TABLES
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
    registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS tardiness (
    id SERIAL PRIMARY KEY,
    student_id INTEGER NOT NULL REFERENCES students(id) ON DELETE CASCADE,
    date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    arrival_time TIME NOT NULL,
    minutes_late INTEGER NOT NULL,
    justified BOOLEAN DEFAULT FALSE,
    reason TEXT,
    registered_by VARCHAR(50) DEFAULT 'SYSTEM'
);

CREATE TABLE IF NOT EXISTS warnings (
    id SERIAL PRIMARY KEY,
    student_id INTEGER NOT NULL REFERENCES students(id) ON DELETE CASCADE,
    date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    type CHAR(1) NOT NULL,
    reason TEXT NOT NULL,
    registered_by VARCHAR(50) DEFAULT 'SYSTEM'
);

-- INSERT TEST DATA
INSERT INTO students (code, dni, first_name, last_name, mother_last_name, email, phone, grade, section, status)
VALUES 
('E001', '12345678', 'Juan', 'Perez', 'Garcia', 'juan.perez@edunova.edu', '987654321', '4', 'A', 'A'),
('E002', '87654321', 'Maria', 'Lopez', 'Rodriguez', 'maria.lopez@edunova.edu', '987654322', '4', 'A', 'A'),
('E003', '11122233', 'Carlos', 'Sanchez', 'Diaz', 'carlos.sanchez@edunova.edu', '987654323', '5', 'B', 'A'),
('E004', '44455566', 'Ana', 'Torres', 'Flores', 'ana.torres@edunova.edu', '987654324', '3', 'C', 'I'),
('E005', '77788899', 'Luis', 'Ramirez', 'Castro', 'luis.ramirez@edunova.edu', '987654325', '4', 'B', 'S'),
('E006', '99900011', 'Lucia', 'Fernandez', 'Mendoza', 'lucia.fernandez@edunova.edu', '987654326', '5', 'A', 'A')
ON CONFLICT (code) DO NOTHING;
