ALTER TABLE users ADD COLUMN employee_number VARCHAR2(50);
UPDATE users SET employee_number = employeeid;
ALTER TABLE users DROP COLUMN employeeid;