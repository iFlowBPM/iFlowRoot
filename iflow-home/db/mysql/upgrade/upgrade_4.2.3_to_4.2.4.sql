ALTER TABLE users ADD COLUMN employee_number VARCHAR(50) AFTER department;
UPDATE users SET employee_number = employeeid;
